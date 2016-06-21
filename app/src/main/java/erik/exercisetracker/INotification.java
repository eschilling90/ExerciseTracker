package erik.exercisetracker;


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
 * Created by Alex on 5/10/2016.
 */
public interface INotification{

    INotification newInstance();

}
