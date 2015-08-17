package erik.exercisetracker;

/**
 * Created by eriks_000 on 8/12/2015.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.SharedPreferences;
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

public class RegisterFragment extends Fragment {

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.registration_fragment,container, false);

        Button registerButton = (Button) rootView.findViewById(R.id.registerButtonRegistration);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Connection to back end to add user
                EditText first = (EditText) rootView.findViewById(R.id.firstNameTextRegistration);
                EditText last = (EditText) rootView.findViewById(R.id.lastNameTextRegistration);
                EditText email = (EditText) rootView.findViewById(R.id.emailAddressTextRegistration);
                EditText password = (EditText) rootView.findViewById(R.id.passwordTextRegistration);
                EditText passwordRetype = (EditText) rootView.findViewById(R.id.repasswordTextRegistration);

                String firstName = first.getText().toString();
                String lastName = last.getText().toString();
                final String emailAddress = email.getText().toString();
                String passwordValue = password.getText().toString();
                String passwordRetypeValue = passwordRetype.getText().toString();

                Log.d("debug","password is"+passwordValue);
                Log.d("debug","password retype is"+ passwordRetypeValue);

                //Check if passwords are equal and email is a valid email address
                if (!emailAddress.isEmpty() && !passwordValue.isEmpty() && !passwordRetypeValue.isEmpty() && (passwordValue.equals(passwordRetypeValue)))
                {
                    //Connect to webservice to add user and check result
                    ExerciseTrackerActivity.httpClient.post(ExerciseTrackerActivity.REQUEST_URL + "register?firstName=" + firstName + "&lastName=" + lastName + "&emailAddress=" + emailAddress + "&password=" + passwordValue + "&permissions=1", new AsyncHttpResponseHandler() {
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            try{
                                JSONObject jsonResponse = new JSONObject(new String(response));
                                int code = jsonResponse.getInt("statusCode");
                                if(code == 200)  {
                                    SharedPreferences.Editor editor = ExerciseTrackerActivity.pref.edit();
                                    editor.putString("emailAddress", emailAddress);
                                    editor.commit();                                    //if Successful then take to weight tracker fragment
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.container, new AddTraineeFragment());
                                    ft.addToBackStack(null);
                                    ft.commit();
                                } else {
                                    UtilityFunctions.showToast("email address already exists", getActivity(), rootView);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            UtilityFunctions.showToast("Request Failed", getActivity(), rootView);
                        }
                    });
                } else if (emailAddress.isEmpty() || passwordValue.isEmpty() || passwordRetypeValue.isEmpty()) {
                    UtilityFunctions.showToast("Required Fields Missing", getActivity(), rootView);
                } else if (!passwordValue.equals(passwordRetypeValue)) {
                    UtilityFunctions.showToast("Passwords don't Match", getActivity(), rootView);
                } else {
                    UtilityFunctions.showToast("Unknown Error", getActivity(), rootView);
                }
            }
        });
        return rootView;
    }
}
