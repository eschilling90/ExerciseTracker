package erik.exercisetracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by eriks_000 on 8/12/2015.
 */
public class AddTraineeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.add_trainee_fragment, container, false);

        Button submitButton = (Button) rootView.findViewById(R.id.addButtonAdd_trainee);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String emailAddress = ExerciseTrackerActivity.pref.getString("emailAddress","");
                final String otherAddress = ((EditText) rootView.findViewById(R.id.emailAddressTextAdd_trainee)).getText().toString();
                //contents, recurrenceRate, senderId/senderEmail, receiverId/receiverEmail
                StringEntity params = null;
                NotificationContent contents = new NotificationContent();
                contents.title = "Trainee request";
                Gson gson = new Gson();
                String json = gson.toJson(contents);
                try {
                    params = new StringEntity(json);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ExerciseTrackerActivity.httpClient.post(getActivity(), ExerciseTrackerActivity.REQUEST_URL + "notification?senderAddress=" + emailAddress + "&receiverAddress=" + otherAddress + "&recurrenceRate=0", params, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] response) {
                        Log.d("debug", "response is: " + new String(response));
                        try {
                            JSONObject jsonResponse = new JSONObject(new String(response));
                            int code = jsonResponse.getInt("statusCode");
                            if (code == 202) {
                                showToast("Invalid email address", rootView);
                            } else if (code == 201) {
                                showToast("Invalid trainee email address", rootView);
                            } else {
                                showToast("Submitted notification to user", rootView);
                            }
                        } catch (JSONException e) {
                            showToast("Failed request", rootView);
                            Log.d("debug", new String(response));
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                        showToast("Failed request", rootView);
                    }
                });
            }
        });

        Button backButton = (Button) rootView.findViewById(R.id.backButtonAdd_trainee);
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
