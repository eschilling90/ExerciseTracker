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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Alex on 8/13/2015.
 */
public class NotificationFragment extends Fragment {

    private AsyncHttpClient httpClient = new AsyncHttpClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState) {
        final View rootView = inflater.inflate(R.layout.notification_fragment, container, false);

        final String email = ExerciseTrackerActivity.pref.getString("emailAddress", "");

        httpClient.get(ExerciseTrackerActivity.REQUEST_URL + "notification?email=" + email, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                Log.d("debug", "response is: " + new String(response));
                try {
                    JSONArray jsonArray = new JSONArray(new String(response));
                    for (int j = 0; j < jsonArray.length(); j++){
                        String newNotification = jsonArray.getJSONObject(j).getString("title");
                        String senderName = jsonArray.getJSONObject(j).getString("senderID");
                        String isoDate = jsonArray.getJSONObject(j).getString("creationDate");
                        DateFormat df = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss");
                        Boolean read = jsonArray.getJSONObject(j).getBoolean("read");
                        Date newDate = null;
                        try {
                            newDate = df.parse(isoDate);
                        } catch (ParseException e){
                            newDate = new Date();
                        }
                        DateFormat formatDate = new SimpleDateFormat("MM/dd/yy");
                        addNotificationToList(newNotification, formatDate.format(newDate), senderName, read, rootView);
                    }
                } catch (JSONException e) {
                    setNotificationListErrorMessage("Error loading Notifications", rootView);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                setNotificationListErrorMessage("Error loading Notifications", rootView);
            }
        });


        return rootView;
    }


    private void addNotificationToList(String notification, String date, String sender, Boolean read, View rootView){

        LinearLayout notificationTable = (LinearLayout) rootView.findViewById(R.id.tableNotification);
        RelativeLayout linear = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT + 70);
        linear.setClickable(true);
        lp.setMargins(0, 0, 0, 0);
        linear.setLayoutParams(lp);

        TextView dateText = new TextView(getActivity());
        dateText.setText(date);
        dateText.setId(View.generateViewId());
        dateText.setTextAppearance(getActivity(), R.style.notification_text);
        RelativeLayout.LayoutParams dateParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dateParams.setMarginStart(20);
        dateParams.setMarginEnd(150);
        linear.addView(dateText, dateParams);

        TextView notificationText = new TextView(getActivity());
        notificationText.setText(notification);
        notificationText.setTextAppearance(getActivity(), R.style.notification_text);
        RelativeLayout.LayoutParams notificationParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        notificationParams.addRule(RelativeLayout.RIGHT_OF, dateText.getId());
        linear.addView(notificationText, notificationParams);

        TextView senderText = new TextView(getActivity());
        senderText.setText(sender);
        senderText.setTextAppearance(getActivity(), R.style.notification_text);
        RelativeLayout.LayoutParams senderParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        senderParams.addRule(RelativeLayout.RIGHT_OF, notificationText.getId());
        linear.addView(senderText, senderParams);

        TextView readFlagText = new TextView(getActivity());
        if(read){
            readFlagText.setText("*");
        }else{
            readFlagText.setText("");
        }
        readFlagText.setTextAppearance(getActivity(), R.style.notification_text);
        RelativeLayout.LayoutParams readFlagParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        readFlagParams.addRule(RelativeLayout.RIGHT_OF, senderText.getId());
        linear.addView(readFlagText, readFlagParams);

        notificationTable.addView(linear, 0);
    }

    private void setNotificationListErrorMessage(String message, View rootView) {
        LinearLayout notificationTable = (LinearLayout) rootView.findViewById(R.id.tableNotification);

        TextView errorMessage = new TextView(getActivity());
        errorMessage.setText(message);

        notificationTable.addView(errorMessage, 0);
    }

    public void showToast(String message, View rootView)
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) rootView.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 350);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}

