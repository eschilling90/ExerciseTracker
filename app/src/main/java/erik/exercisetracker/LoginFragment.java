package erik.exercisetracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eriks_000 on 8/11/2015.
 */
public class LoginFragment extends Fragment {

    private AsyncHttpClient httpClient = new AsyncHttpClient();

    public LoginFragment() {
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.login_fragment,container, false);

        Button login = (Button) rootView.findViewById(R.id.loginButtonLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EditText email = (EditText) rootView.findViewById(R.id.emailAddressTextLogin);
                final String emailAddress = email.getText().toString();

                EditText password = (EditText) rootView.findViewById(R.id.passwordTextLogin);
                final String passwordValue = password.getText().toString();

                Log.d("debug", Integer.toString(httpClient.getResponseTimeout()));
                Log.d("debug", Integer.toString(httpClient.getConnectTimeout()));
                Log.d("debug", Integer.toString(httpClient.getTimeout()));
                httpClient.setResponseTimeout(40000);
                httpClient.setConnectTimeout(40000);
                httpClient.setTimeout(40000);

                //if Email is valid and Password is not empty
                if(!emailAddress.equals("") && !passwordValue.equals("")) {
                    //Make connection to back end to check if user exists and if password is correct
                    httpClient.post(ExerciseTrackerActivity.REQUEST_URL + "login?emailAddress=" + emailAddress +"&password="+passwordValue, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] response) {
                            try{
                                JSONObject jsonResponse = new JSONObject(new String(response));
                                int code = jsonResponse.getInt("statusCode");
                                if(code == 202) {
                                    showToast("Name does not exist!",rootView);
                                } else if(code == 201) {
                                    showToast("Wrong Password!",rootView);
                                } else {
                                    //Save user's information to avoid logging in every time
                                    SharedPreferences.Editor editor = ExerciseTrackerActivity.pref.edit();
                                    editor.putString("emailAddress", emailAddress);
                                    editor.commit();

                                    // Redirect to view alL Expenses page
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    AddTraineeFragment frag  = new AddTraineeFragment();
                                    ft.replace(R.id.container, frag);
                                    ft.commit();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                            showToast("Internet Connection not Available!",rootView);
                        }
                    });
                } else {
                    showToast("Required Fields Missing", rootView);
                }
            }
        });

        Button register = (Button) rootView.findViewById(R.id.registerButtonLogin);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Redirect to Register page
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                RegisterFragment frag = new RegisterFragment();
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