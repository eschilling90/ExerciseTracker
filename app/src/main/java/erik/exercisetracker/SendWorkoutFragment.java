package erik.exercisetracker;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eriks_000 on 8/14/2015.
 */
public class SendWorkoutFragment extends Fragment{

    List<UserContent> trainees = new ArrayList<UserContent>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState) {
        final View rootView = inflater.inflate(R.layout.send_workout_fragment, container, false);

        final Spinner workoutSpinner = (Spinner) rootView.findViewById(R.id.workoutNameSpinnerSendWorkout);
        final List<String> workouts = new ArrayList<>();
        workouts.add(0, "Select Workout");
        workouts.add(1, "Create Workout");
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, workouts);
        workoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    CreateWorkoutFragment frag = new CreateWorkoutFragment();
                    ft.replace(R.id.container, frag);
                    ft.addToBackStack(null);
                    ft.commit();
                } else if (position > 1) {
                    WorkoutContent workout = ExerciseTrackerActivity.workouts.get(position-2);
                    EditText description = (EditText) rootView.findViewById(R.id.workoutDescriptionTextSendWorkout);
                    TextView tags = (TextView) rootView.findViewById(R.id.tagsTextSendWorkout);
                    description.setText(workout.description);
                    tags.setText(workout.tags);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Bundle args = getArguments();
        if (args != null) {
            workouts.add(args.getString("newName"));
            EditText description = (EditText) rootView.findViewById(R.id.workoutDescriptionTextSendWorkout);
            TextView tags = (TextView) rootView.findViewById(R.id.tagsTextSendWorkout);
            description.setText(args.getString("newDescription"));
            tags.setText(args.getString("newTags"));
            workoutSpinner.setSelection(workouts.size());
        }
        workoutSpinner.setAdapter(adapter);
        if (ExerciseTrackerActivity.workouts == null) {
            ExerciseTrackerActivity.httpClient.get(getActivity(), ExerciseTrackerActivity.REQUEST_URL + "workout?workoutNames=1", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] response) {
                    Log.d("debug", new String(response));
                    try {
                        JSONObject jsonObject = new JSONObject(new String(response));
                        String statusCode = jsonObject.getString("statusCode");
                        switch (Integer.parseInt(statusCode)) {
                            case 200:
                                JSONArray workoutsArray = jsonObject.getJSONArray("workouts");
                                Gson gson = new Gson();
                                ExerciseTrackerActivity.workouts = Arrays.asList(gson.fromJson(workoutsArray.toString(), WorkoutContent[].class));
                                for (WorkoutContent workout : ExerciseTrackerActivity.workouts) {
                                    adapter.add(workout.name);
                                }
                                adapter.notifyDataSetChanged();
                                break;
                            default:
                                UtilityFunctions.showToast("Some error occurred, could not retrieve workouts", getActivity(), rootView);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                    UtilityFunctions.showToast("Failed request", getActivity(), rootView);
                }
            });
        }

        ExerciseTrackerActivity.httpClient.get(getActivity(), ExerciseTrackerActivity.REQUEST_URL + "trainer?emailAddress=" + ExerciseTrackerActivity.email, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                Log.d("debug", new String(response));
                try {
                    JSONObject jsonObject = new JSONObject(new String(response));
                    String statusCode = jsonObject.getString("statusCode");
                    switch (Integer.parseInt(statusCode)) {
                        case 200:
                            JSONArray traineesArray = jsonObject.getJSONArray("trainees");
                            Gson gson = new Gson();
                            trainees = Arrays.asList(gson.fromJson(traineesArray.toString(), UserContent[].class));
                            for (UserContent trainee : trainees) {
                                trainee.name = trainee.name.replaceAll("#", " ");
                            }
                            break;
                        case 201:
                            UtilityFunctions.showToast("Invalid email address, could not retrieve trainees", getActivity(), rootView);
                            break;
                        default:
                            UtilityFunctions.showToast("Some error occurred, could not retrieve trainees", getActivity(), rootView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                UtilityFunctions.showToast("Failed request", getActivity(), rootView);
            }
        });

        Button addRecipients = (Button) rootView.findViewById(R.id.addRecipientsButtonSendWorkout);
        addRecipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView recipientsText = (TextView) rootView.findViewById(R.id.recipientsTextSendWorkout);
                List<String> recipients = Arrays.asList(recipientsText.getText().toString().split(","));


                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Add Recipients");
                alert.setMessage("");

                ScrollView scrollView = new ScrollView(getActivity());
                final LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);

                Iterator<UserContent> iterator = trainees.iterator();
                while (iterator.hasNext()) {
                    UserContent trainee = iterator.next();
                    RelativeLayout relativeInner = new RelativeLayout(getActivity());
                    RelativeLayout.LayoutParams rpI = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT);
                    relativeInner.setId(View.generateViewId());
                    relativeInner.setLayoutParams(rpI);

                    TextView nameText = new TextView(getActivity());
                    nameText.setText(trainee.name);
                    nameText.setId(View.generateViewId());
                    //nameText.setTextAppearance(getActivity(), R.style.notification_text);
                    RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                    //dateParams.setMarginStart(20);
                    //dateParams.setMarginEnd(150);
                    relativeInner.addView(nameText, nameParams);

                    TextView emailText = new TextView(getActivity());
                    emailText.setText(trainee.emailAddress);
                    emailText.setId(View.generateViewId());
                    //tagsText.setTextAppearance(getActivity(), R.style.notification_text);
                    RelativeLayout.LayoutParams emailParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                    emailParams.addRule(RelativeLayout.BELOW, nameText.getId());
                    relativeInner.addView(emailText, emailParams);

                    RelativeLayout relativeOuter = new RelativeLayout(getActivity());
                    RelativeLayout.LayoutParams rpO = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
                    //rp.setMargins(0, 0, 0, 0);
                    relativeOuter.setLayoutParams(rpO);

                    relativeOuter.addView(relativeInner);

                    CheckBox checkBox = new CheckBox(getActivity());
                    RelativeLayout.LayoutParams checkBoxParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                    checkBoxParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    checkBoxParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    if (recipients.contains((String) trainee.emailAddress)) {
                        checkBox.setSelected(true);
                    }
                    relativeOuter.addView(checkBox, checkBoxParams);

                    layout.addView(relativeOuter, 0);
                    scrollView.addView(layout);
                }
                alert.setView(scrollView);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newRecipients = "";
                        for (int j = 0; j < layout.getChildCount(); j++) {
                            RelativeLayout child = (RelativeLayout) layout.getChildAt(j);
                            if (((CheckBox) child.getChildAt(1)).isChecked()) {
                                RelativeLayout inner = (RelativeLayout) child.getChildAt(0);
                                String name = ((TextView) inner.getChildAt(0)).getText().toString();
                                String email = ((TextView) inner.getChildAt(1)).getText().toString();
                                if (newRecipients.isEmpty()) {
                                    newRecipients = name + "(" + email + ")";
                                } else {
                                    newRecipients = newRecipients + "," + name + "(" + email + ")";
                                }
                            }
                        }
                        EditText recText = (EditText) rootView.findViewById(R.id.recipientsTextSendWorkout);
                        recText.setText(newRecipients);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();
            }
        });

        Button sendButton = (Button) rootView.findViewById(R.id.sendButtonSendWorkout);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = workoutSpinner.getSelectedItemPosition();
                if (position <= 1) {
                    UtilityFunctions.showToast("Select a workout to send", getActivity(), rootView);
                } else {
                    EditText recipientsText = (EditText) rootView.findViewById(R.id.recipientsTextSendWorkout);
                    if (recipientsText.getText().toString().isEmpty()) {
                        UtilityFunctions.showToast("Select at least one recipient", getActivity(), rootView);
                    } else {
                        WorkoutContent workout = ExerciseTrackerActivity.workouts.get(position - 2);
                        long id = workout.workoutId;
                        EditText descriptionText = (EditText) rootView.findViewById(R.id.workoutDescriptionTextSendWorkout);
                        TextView tagsText = (TextView) rootView.findViewById(R.id.tagsTextSendWorkout);
                        String description = descriptionText.getText().toString();
                        String tags = tagsText.getText().toString();
                        String[] recipients = recipientsText.getText().toString().split(",");
                        for (String recipient : recipients) {
                            Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(recipient);
                            String recipientEmail = "";
                            if (m.find()) {
                                recipientEmail = m.group(1);
                            } else {
                                UtilityFunctions.showToast("Invalid recipient " + recipient + ". Failed to send", getActivity(), rootView);
                            }
                            NotificationContent contents = new NotificationContent();
                            Gson gson = new Gson();
                            contents.title = "Workout";
                            contents.contents = "{'name': " + workout.name + ", 'description': " + description + ", 'tags': " + tags + ", 'workoutId': " + workout.workoutId + "}";
                            String json = gson.toJson(contents);
                            StringEntity params = null;
                            try {
                                params = new StringEntity(json);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            ExerciseTrackerActivity.httpClient.post(getActivity(), ExerciseTrackerActivity.REQUEST_URL + "notification?senderAddress=" + ExerciseTrackerActivity.email + "&receiverAddress=" + recipientEmail + "&recurrenceRate=0", params, "application/json", new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int i, Header[] headers, byte[] response) {
                                    Log.d("debug", "response is: " + new String(response));
                                    try {
                                        JSONObject jsonResponse = new JSONObject(new String(response));
                                        int code = jsonResponse.getInt("statusCode");
                                        if (code == 202) {
                                            UtilityFunctions.showToast("Invalid email address", getActivity(), rootView);
                                        } else if (code == 201) {
                                            UtilityFunctions.showToast("Invalid trainee email address", getActivity(), rootView);
                                        } else {
                                            UtilityFunctions.showToast("Submitted notification to user", getActivity(), rootView);
                                        }
                                    } catch (JSONException e) {
                                        UtilityFunctions.showToast("Failed request", getActivity(), rootView);
                                        Log.d("debug", new String(response));
                                    }
                                }

                                @Override
                                public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                                    UtilityFunctions.showToast("Failed request", getActivity(), rootView);
                                }
                            });
                        }
                    }
                }
            }
        });

        Button backButton = (Button) rootView.findViewById(R.id.backButtonCreateWorkout);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                HomepageFragment frag = new HomepageFragment();
                ft.replace(R.id.container, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return rootView;
    }
}
