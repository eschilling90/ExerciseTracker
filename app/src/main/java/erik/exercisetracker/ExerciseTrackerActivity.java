package erik.exercisetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;


public class ExerciseTrackerActivity extends ActionBarActivity {

    public static AsyncHttpClient httpClient = new AsyncHttpClient();
    public static String REQUEST_URL = "http://exercise-tracker-webservice.appspot.com/";
    public static SharedPreferences pref;
    public static String email;
    public static List<WorkoutContent> workouts;
    public static List<String> exerciseTags = new ArrayList<String>();

    public CurrentWorkout currentWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exerciseTags.add("Powerlifting");
        exerciseTags.add("Hamstrings");
        exerciseTags.add("Adductors");
        exerciseTags.add("Barbell");
        exerciseTags.add("Compound");
        exerciseTags.add("Intermediate");
        exerciseTags.add("Strength");
        exerciseTags.add("Quadriceps");
        exerciseTags.add("Glutes");
        exerciseTags.add("Machine");
        exerciseTags.add("Beginner");
        exerciseTags.add("Calves");
        exerciseTags.add("Isolation");
        exerciseTags.add("Chest");
        exerciseTags.add("Shoulders");
        exerciseTags.add("Triceps");
        exerciseTags.add("Dumbbell");
        exerciseTags.add("Stretching");
        exerciseTags.add("Foam Roll");
        exerciseTags.add("Bands");
        exerciseTags.add("Plyometrics");
        exerciseTags.add("Abductors");
        exerciseTags.add("Abdominals");
        exerciseTags.add("Kettlebells");
        exerciseTags.add("Lats");
        exerciseTags.add("Olympic Weightlifting");
        exerciseTags.add("Biceps");
        exerciseTags.add("Forearms");
        exerciseTags.add("Middle Back");
        exerciseTags.add("Body Only");
        exerciseTags.add("Neck");
        exerciseTags.add("Lower Back");
        exerciseTags.add("Exercise Ball");
        exerciseTags.add("Cable");
        exerciseTags.add("Expert");
        exerciseTags.add("Traps");
        exerciseTags.add("Cardio");
        exerciseTags.add("Medicine Ball");
        exerciseTags.add("Strongman");
        exerciseTags.add("E-Z Curl Bar");

        httpClient.setURLEncodingEnabled(true);
        httpClient.setTimeout(40000);
        httpClient.setResponseTimeout(40000);
        httpClient.setConnectTimeout(40000);

        pref = getApplicationContext().getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        if(pref.contains("emailAddress")) {
            email = pref.getString("emailAddress", "");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new HomepageFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment()).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
