package erik.exercisetracker;


import android.app.ActionBar;
import android.app.Notification;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by Alex on 8/17/2015.
 */
public class DescriptiveNotificationFragment extends Fragment {

    public static DescriptiveNotificationFragment newInstance(int notificationId, String notification, String date, String senderName, String senderEmail){
        DescriptiveNotificationFragment frag = new DescriptiveNotificationFragment();
        Bundle details = new Bundle();
        details.putInt("notificationId", notificationId);
        details.putString("notificationType", notification);
        details.putString("date", date);
        details.putString("senderEmail", senderEmail);
        details.putString("senderName", senderName);
        frag.setArguments(details);

        return frag;
    }

    public static DescriptiveNotificationFragment newInstance(int notificationId, String notification, String date, String senderName, String senderEmail, int workoutId){
        DescriptiveNotificationFragment frag = new DescriptiveNotificationFragment();
        Bundle details = new Bundle();
        details.putInt("notificationId", notificationId);
        details.putString("notificationType", notification);
        details.putInt("workoutId", workoutId);
        details.putString("date", date);
        details.putString("senderEmail", senderEmail);
        details.putString("senderName", senderName);
        frag.setArguments(details);

        return frag;
    }

    public static DescriptiveNotificationFragment newInstance(int notificationId, String notification, String date, String senderName, String senderEmail, String recurrenceRate){
        DescriptiveNotificationFragment frag = new DescriptiveNotificationFragment();
        Bundle details = new Bundle();
        details.putInt("notificationId", notificationId);
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
        final int notificationId = details.getInt("notificationId");
        String notificationType = details.getString("notificationType");
        String date = details.getString("date");
        String senderName = details.getString("senderName");
        final String senderEmail = details.getString("senderEmail");

        TextView header = (TextView) rootView.findViewById(R.id.notificationTypeHeaderDescritpive_notification) ;
        header.setText(notificationType);
        TextView dateText = (TextView) rootView.findViewById(R.id.dateTextDescriptive_notification);
        dateText.setText("on " + date);
        TextView contentText = (TextView)rootView.findViewById(R.id.textline1Descriptive_notification);
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

/*
                        //asdf';lkasd'f;lkas';dfk'a;skf'a;sfkd';akdf';asd
                        ExerciseTrackerActivity.httpClient.get(ExerciseTrackerActivity.REQUEST_URL + "login?user=" + senderId, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] response) {
                                Log.d("debug", "response is: " + new String(response));
                                try{
                                    JSONObject jsonResponse = new JSONObject(new String(response));
                                    JSONArray trainer = jsonResponse.getJSONArray("user");
                                    String statusCode = jsonResponse.getString("statusCode");
                                    String senderAddress =

                                }catch(JSONException e){
                                    UtilityFunctions.showToast("Could not find trainer", getActivity(), rootView);

                                }
                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                            }
                        });

                        //finds trainer's list
                        //______________________________________email needs to be senders email____________________________________________
                        ExerciseTrackerActivity.httpClient.get(ExerciseTrackerActivity.REQUEST_URL + "trainer?emailAddress=" + senderEmail, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] response) {
                                Log.d("debug", "response is: " + new String(response));
                                try {
                                    JSONObject jsonObject = new JSONObject(new String(response));
                                    JSONArray trainer = jsonObject.getJSONArray("trainer"); // watch name
                                    String statusCode = jsonObject.getString("statusCode");
                                    int trainerId = trainer.getJSONObject(0).getInt("trainerId");
                                } catch (JSONException e) {
                                    UtilityFunctions.showToast("Could not find trainer", getActivity(), rootView);
                                }
                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                            }
                        });
*/
                        //adds user to trainer's trainee list
                        //______________________________________email needs to be senders email____________________________________________
                        ExerciseTrackerActivity.httpClient.post(ExerciseTrackerActivity.REQUEST_URL + "trainer?trainerId=" + senderEmail + "&traineeId=" + ExerciseTrackerActivity.email, new AsyncHttpResponseHandler() {
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
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });

                break;

            case "Workout":
                contentText.setText(senderName + " sent you a workout");

                Button viewButton = new Button(getActivity());
                viewButton.setId(View.generateViewId());
                viewButton.setText("View");
                RelativeLayout.LayoutParams viewButtonParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                relativeLayoutDescriptive.addView(viewButton, viewButtonParams);
                viewButtonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                viewButtonParams.addRule(RelativeLayout.BELOW, contentText.getId());
                viewButtonParams.topMargin = 100;
                viewButtonParams.setMarginEnd(100);
                viewButton.setLayoutParams(viewButtonParams);

                Button saveButton = new Button(getActivity());
                saveButton.setId(View.generateViewId());
                saveButton.setText("Save");
                RelativeLayout.LayoutParams saveButtonParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                relativeLayoutDescriptive.addView(saveButton, saveButtonParams);
                saveButtonParams.addRule(RelativeLayout.BELOW, contentText.getId());
                saveButtonParams.topMargin = 100;
                saveButtonParams.addRule(RelativeLayout.ALIGN_START, 100);
                saveButton.setLayoutParams(saveButtonParams);

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
}
