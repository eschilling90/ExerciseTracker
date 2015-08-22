package erik.exercisetracker;

import android.app.ActionBar;
import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Alex on 8/13/2015.
 */
public class NotificationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState) {
        final View rootView = inflater.inflate(R.layout.notification_fragment, container, false);

        ExerciseTrackerActivity.httpClient.get(ExerciseTrackerActivity.REQUEST_URL + "notification?recieverEmail=" + ExerciseTrackerActivity.email, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                Log.d("debug", "response is: " + new String(response));
                try {
                    JSONObject jsonObject = new JSONObject(new String(response));
                    JSONArray notifications = jsonObject.getJSONArray("notifications");
                    String statusCode = jsonObject.getString("statusCode");
                    for (int j = 0; j < notifications.length(); j++) {
                        int notificationId = notifications.getJSONObject(j).getInt("notificationId");
                        String newNotification = notifications.getJSONObject(j).getString("title");
                        String senderName = notifications.getJSONObject(j).getString("senderName");
                        String senderEmail = notifications.getJSONObject(j).getString("senderEmail");
                        String isoDate = notifications.getJSONObject(j).getString("creationDate");
                        DateFormat df = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss");
                        Boolean read = notifications.getJSONObject(j).getBoolean("read");
                        Date newDate = null;
                        try {
                            newDate = df.parse(isoDate);
                        } catch (ParseException e) {
                            newDate = new Date();
                        }
                        DateFormat formatDate = new SimpleDateFormat("MM/dd/yy");
                        senderName = senderName.replace('#', ' ');
                        addNotificationToList(notificationId, newNotification, formatDate.format(newDate), senderName, senderEmail, read, rootView);
                    }
                } catch (JSONException e) {
                    setNotificationListErrorMessage("Error loading Notifications (catch)", rootView);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                setNotificationListErrorMessage("Error loading Notifications", rootView);
            }
        });


        Button backButton  = (Button) rootView.findViewById(R.id.backButtonNotification);
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


    private void addNotificationToList(final int notificationId, final String notification, final String date, final String senderName, final String senderEmail, Boolean read, View rootView){

        int marginValue = 50;

        LinearLayout notificationTable = (LinearLayout) rootView.findViewById(R.id.tableNotification);
        RelativeLayout relative = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        relative.setClickable(true);
        //rp.setMargins(0, 0, 0, 0);
        relative.setLayoutParams(rp);

        //date init
        TextView dateText = new TextView(getActivity());
        dateText.setText(date);
        dateText.setId(View.generateViewId());
        dateText.setTextAppearance(getActivity(), R.style.notification_text);
        RelativeLayout.LayoutParams dateParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        //dateParams.setMarginStart(20);
        //dateParams.setMarginEnd(150);
        relative.addView(dateText, dateParams);


        //notification init
        TextView notificationText = new TextView(getActivity());
        notificationText.setText(notification);
        notificationText.setId(View.generateViewId());
        notificationText.setTextAppearance(getActivity(), R.style.notification_text);
        RelativeLayout.LayoutParams notificationParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        notificationParams.addRule(RelativeLayout.RIGHT_OF, dateText.getId());
        relative.addView(notificationText, notificationParams);
        notificationParams.setMarginStart(marginValue);

        //listener for clicks on Notification title
        notificationText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DescriptiveNotificationFragment frag = new DescriptiveNotificationFragment();
                switch(notification) {

                    case("Trainee request"):
                        frag = frag.newInstance(notificationId, notification, date, senderName, senderEmail);
                        break;


                }
                ft.replace(R.id.container, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        //sender init
        TextView senderText = new TextView(getActivity());
        final String fromSenderName = "From " + senderName;
        senderText.setText(fromSenderName);
        senderText.setId(View.generateViewId());
        senderText.setTextAppearance(getActivity(), R.style.notification_text);
        RelativeLayout.LayoutParams senderParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        senderParams.addRule(RelativeLayout.BELOW, notificationText.getId());
        senderParams.addRule(RelativeLayout.ALIGN_LEFT, notificationText.getId());
        senderParams.setMarginStart(marginValue + 20);
        relative.addView(senderText, senderParams);

        //listener for clicks on Sender text
        senderText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DescriptiveNotificationFragment frag = new DescriptiveNotificationFragment();
                frag = frag.newInstance(notificationId, notification, date, fromSenderName, senderEmail);

                ft.replace(R.id.container, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        //read flag init
        TextView readFlagText = new TextView(getActivity());
        if(read){
            readFlagText.setText("*");
        }else{
            readFlagText.setText("");
        }
        readFlagText.setTextAppearance(getActivity(), R.style.notification_text);
        RelativeLayout.LayoutParams readFlagParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        readFlagParams.addRule(RelativeLayout.RIGHT_OF, senderText.getId());
        relative.addView(readFlagText, readFlagParams);

        notificationTable.addView(relative, 0);
    }

    private void setNotificationListErrorMessage(String message, View rootView) {
        LinearLayout notificationTable = (LinearLayout) rootView.findViewById(R.id.tableNotification);

        TextView errorMessage = new TextView(getActivity());
        errorMessage.setText(message);

        notificationTable.addView(errorMessage, 0);
    }
}

