package erik.exercisetracker.uiPackage.Notification;


import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.util.Log;


import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import erik.exercisetracker.ExerciseTrackerActivity;
import erik.exercisetracker.R;
import erik.exercisetracker.UtilityFunctions;

/**
 * Created by Alex on 8/17/2015.
 */
public class DescriptiveNotificationFragment extends Fragment {

    public static DescriptiveNotificationFragment newInstance(long notificationId, String notification, String date, String senderName, String senderEmail){
        DescriptiveNotificationFragment frag = new DescriptiveNotificationFragment();
        Bundle details = new Bundle();
        details.putLong("notificationId", notificationId);
        details.putString("notificationType", notification);
        details.putString("date", date);
        details.putString("senderEmail", senderEmail);
        details.putString("senderName", senderName);
        frag.setArguments(details);

        return frag;
    }

    public static DescriptiveNotificationFragment newInstance(long notificationId, String notification, String date, String senderName, String senderEmail, long workoutId, String workoutName){
        DescriptiveNotificationFragment frag = new DescriptiveNotificationFragment();
        Bundle details = new Bundle();
        details.putLong("notificationId", notificationId);
        details.putString("notificationType", notification);
        details.putLong("workoutId", workoutId);
        details.putString("date", date);
        details.putString("senderEmail", senderEmail);
        details.putString("senderName", senderName);
        //details.putString("description", description);
        details.putString("workoutName", workoutName);
        frag.setArguments(details);

        return frag;
    }

    public static DescriptiveNotificationFragment newInstance(long notificationId, String notification, String date, String senderName, String senderEmail, String recurrenceRate){
        DescriptiveNotificationFragment frag = new DescriptiveNotificationFragment();
        Bundle details = new Bundle();
        details.putLong("notificationId", notificationId);
        details.putString("notificationType", notification);
        details.putString("date", date);
        details.putString("senderEmail", senderEmail);
        details.putString("senderName", senderName);
        details.putString("recurrenceRate", recurrenceRate);
        frag.setArguments(details);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState){
        final View rootView = inflater.inflate(R.layout.descriptive_notification_fragment, container, false);

        Bundle details = getArguments();
        final long notificationId = details.getLong("notificationId");
        final String notificationType = details.getString("notificationType");
        final String date = details.getString("date");
        final String senderName = details.getString("senderName");
        final String senderEmail = details.getString("senderEmail");
        //final long workoutId = details.getLong("workoutId");
        //final String workoutName = details.getString("workoutName");

        TextView header = (TextView) rootView.findViewById(R.id.notificationTypeHeaderDescritpive_notification) ;
        header.setText(notificationType);
        TextView dateText = (TextView) rootView.findViewById(R.id.dateTextDescriptive_notification);
        dateText.setText("on " + date);
        final TextView contentText = (TextView)rootView.findViewById(R.id.textline1Descriptive_notification);
        RelativeLayout relativeLayoutDescriptive = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDescriptive_notification);


        switch(notificationType) {

            case ("Trainee request"):
                contentText.setText(senderName + " wants to be your trainer");

                Button declineButton = new Button(getActivity());
                declineButton.setId(View.generateViewId());
                declineButton.setText("Decline");
                RelativeLayout.LayoutParams declineButtonParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                relativeLayoutDescriptive.addView(declineButton, declineButtonParams);
                declineButtonParams.addRule(RelativeLayout.BELOW, contentText.getId());
                declineButtonParams.topMargin = 100;
                declineButtonParams.addRule(RelativeLayout.ALIGN_START, 100);
                declineButton.setLayoutParams(declineButtonParams);

                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //delete notification
                        ExerciseTrackerActivity.httpClient.delete(ExerciseTrackerActivity.REQUEST_URL + "notification?notificationId=" + notificationId, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] response) {
                                Log.d("debug", "response is:" + new String(response));
                                UtilityFunctions.showToast("Notification was deleted", getActivity(), rootView);
                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                UtilityFunctions.showToast("Notification was NOT deleted", getActivity(), rootView);

                            }
                        });

                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        NotificationFragment frag = new NotificationFragment();
                        ft.replace(R.id.container, frag);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });

                Button acceptButton = new Button(getActivity());
                acceptButton.setId(View.generateViewId());
                acceptButton.setText("Accept");
                RelativeLayout.LayoutParams acceptButtonParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                relativeLayoutDescriptive.addView(acceptButton, acceptButtonParams);
                acceptButtonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                acceptButtonParams.addRule(RelativeLayout.BELOW, contentText.getId());
                acceptButtonParams.topMargin = 100;
                acceptButtonParams.setMarginEnd(100);
                acceptButton.setLayoutParams(acceptButtonParams);


                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //adds user to trainer's trainee list
                        //______________________________________email needs to be senders email____________________________________________
                        ExerciseTrackerActivity.httpClient.post(ExerciseTrackerActivity.REQUEST_URL + "trainer?trainerEmail=" + senderEmail + "&traineeEmail=" + ExerciseTrackerActivity.email, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] response) {
                                Log.d("debug", "response is: " + new String(response));
                                UtilityFunctions.showToast("Accepted request", getActivity(), rootView);

                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                UtilityFunctions.showToast("Could not accept request", getActivity(), rootView);

                            }
                        });

                        //delete notification
                        ExerciseTrackerActivity.httpClient.delete(ExerciseTrackerActivity.REQUEST_URL + "notification?notificationId=" + notificationId, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] response) {
                                Log.d("debug", "response is:" + new String(response));
                                UtilityFunctions.showToast("Notification was deleted", getActivity(), rootView);
                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                UtilityFunctions.showToast("Notification was NOT deleted", getActivity(), rootView);

                            }
                        });



                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        NotificationFragment frag = new NotificationFragment();
                        ft.replace(R.id.container, frag);
                        ft.addToBackStack(null); //_________________________________may want to change for discard and delete
                        ft.commit();
                    }
                });

                break;

            case "Workout":
                ExerciseTrackerActivity.httpClient.get(ExerciseTrackerActivity.REQUEST_URL + "notification?notificationId=" + notificationId, new AsyncHttpResponseHandler() {
                    //+ "&receiverEmail=" + ExerciseTrackerActivity.email
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] response) {
                        Log.d("debug", "response is: " + new String(response));
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response));
                            JSONArray notifications = jsonObject.getJSONArray("notifications");
                            String statusCode = jsonObject.getString("statusCode");
                            String contentsString = notifications.getJSONObject(0).getString("contents");
                            JSONObject contents = new JSONObject(contentsString);
                            String childContentsStringUnformatted = contents.getString("contents");
                            String childContentsString = childContentsStringUnformatted.replace("\'", "\"");
                            JSONObject childContents = new JSONObject(childContentsString);//contents.getJSONObject("contents");
                            final String workoutName = childContents.getString("name");
                            final long workoutId = childContents.getLong("workoutId");

                            RelativeLayout relativeLayoutDescriptive = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDescriptive_notification);
                            TextView workoutHeader = (TextView) rootView.findViewById(R.id.notificationTypeHeaderDescritpive_notification);
                            workoutHeader.setText(workoutName);

                            TextView dateText = (TextView) rootView.findViewById(R.id.dateTextDescriptive_notification);
                            dateText.setText("from " + senderName + " on " + date);
                            TextView workoutContent = (TextView)rootView.findViewById(R.id.textline1Descriptive_notification);
                            workoutHeader.setText(workoutName);
                            relativeLayoutDescriptive.removeView(workoutContent);

                            getWorkout(workoutId);





                        } catch (JSONException e) {
                            Log.d("debug", "contents not correctly extracted");
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Log.d("debug", "contents not correctly extracted (failure)");

                    }
                });


                Button discardButton = new Button(getActivity());
                discardButton.setId(View.generateViewId());
                discardButton.setText("Discard");
                RelativeLayout.LayoutParams discardButtonParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                relativeLayoutDescriptive.addView(discardButton, discardButtonParams);
                discardButtonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                discardButtonParams.addRule(RelativeLayout.BELOW, contentText.getId());
                discardButtonParams.topMargin = 100;
                discardButtonParams.setMarginEnd(100);
                discardButton.setLayoutParams(discardButtonParams);

                Button saveButton = new Button(getActivity());
                saveButton.setId(View.generateViewId());
                saveButton.setText("Save");
                RelativeLayout.LayoutParams saveButtonParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                relativeLayoutDescriptive.addView(saveButton, saveButtonParams);
                saveButtonParams.addRule(RelativeLayout.BELOW, contentText.getId());
                saveButtonParams.topMargin = 100;
                saveButtonParams.addRule(RelativeLayout.ALIGN_START, 100);
                saveButton.setLayoutParams(saveButtonParams);


                discardButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //delete notification
                        ExerciseTrackerActivity.httpClient.delete(ExerciseTrackerActivity.REQUEST_URL + "notification?notificationId=" + notificationId, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] response) {
                                Log.d("debug", "response is:" + new String(response));
                                UtilityFunctions.showToast("Notification was deleted", getActivity(), rootView);
                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                UtilityFunctions.showToast("Notification was NOT deleted", getActivity(), rootView);
                            }
                        });

                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        NotificationFragment frag = new NotificationFragment();
                        ft.replace(R.id.container, frag);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });

                break;

            case "WeightCheckIn":
                contentText.setText(senderName + " wants to check your weight");


                EditText weightInput = new EditText(getActivity());
                weightInput.setId(View.generateViewId());
                RelativeLayout.LayoutParams weightInputParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                relativeLayoutDescriptive.addView(weightInput, weightInputParams);
                weightInput.setHint("Enter weight...");
                weightInputParams.addRule(RelativeLayout.BELOW, contentText.getId());
                weightInputParams.topMargin = 100;
                weightInputParams.addRule(RelativeLayout.ALIGN_START, 100);
                weightInput.setLayoutParams(weightInputParams);

                Button submitWeightButton = new Button(getActivity());
                submitWeightButton.setId(View.generateViewId());
                submitWeightButton.setText("Submit");
                RelativeLayout.LayoutParams submitWeightButtonParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                relativeLayoutDescriptive.addView(submitWeightButton, submitWeightButtonParams);
                submitWeightButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                submitWeightButtonParams.addRule(RelativeLayout.BELOW, contentText.getId());
                submitWeightButtonParams.topMargin = 100;
                submitWeightButtonParams.setMarginEnd(100);
                submitWeightButton.setLayoutParams(submitWeightButtonParams);

                break;


            default:
                contentText.setText("Invalid Notification Type");

        }


        Button backButton = (Button) rootView.findViewById(R.id.backButtonDescritpive_notification);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                NotificationFragment frag = new NotificationFragment();
                ft.replace(R.id.container, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return rootView;
    }

    private void getWorkout(long workoutId){

        ExerciseTrackerActivity.httpClient.get(ExerciseTrackerActivity.REQUEST_URL + "workout?workoutId=" + workoutId, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                Log.d("debug", "response is: " + new String(response));
                try {
                    JSONObject jsonObject = new JSONObject(new String(response));
                    JSONObject workout = jsonObject.getJSONObject("workouts");
                    String statusCode = jsonObject.getString("statusCode");



                    //JSON content


                    //String contentString = workout.getJSONObject(0).getString("content");


                    //TextView content = new TextView(getActivity());
                    //contentText.setText(contentString);


                } catch (JSONException e) {
                    Log.d("debug", "Error loading workout");

                }


            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });


    }
}
