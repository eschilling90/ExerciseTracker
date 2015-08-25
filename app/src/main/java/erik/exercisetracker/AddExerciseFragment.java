package erik.exercisetracker;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eriks_000 on 8/14/2015.
 */
public class AddExerciseFragment extends Fragment {

    Map<Integer, ExerciseContent> exerciseMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState) {
        final View rootView = inflater.inflate(R.layout.add_exercise_fragment, container, false);

        final Spinner tagSpinner = (Spinner) rootView.findViewById(R.id.filterSpinnerAdd_exercise);
        final List<String> tags = new ArrayList<>();
        for (String tag : ExerciseTrackerActivity.exerciseTags) {
            tags.add(tag);
        }
        tags.add(0, "No Filter");
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, tags);
        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tagSpinner.setAdapter(adapter);

        ExerciseTrackerActivity.httpClient.get(getActivity(), ExerciseTrackerActivity.REQUEST_URL + "exercise", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(response));
                    JSONArray exercisesJSON = jsonObject.getJSONArray("exercises");
                    String statusCode = jsonObject.getString("statusCode");
                    Gson gson = new Gson();
                    ExerciseContent[] exercises = gson.fromJson(exercisesJSON.toString(), ExerciseContent[].class);
                    for (int j=0; j<exercises.length; j++) {
                        addExerciseToTable(exercises[j], rootView);
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

        Button addButton = (Button) rootView.findViewById(R.id.addButtonAdd_exercise);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.tableExercise);
                for (int j=0; j<layout.getChildCount(); j++) {
                    RelativeLayout outer = (RelativeLayout) layout.getChildAt(j);
                    if (((CheckBox) outer.getChildAt(1)).isChecked()) {
                        RelativeLayout inner = (RelativeLayout) outer.getChildAt(0);
                        int id = inner.getId();
                        if (((ExerciseTrackerActivity)getActivity()).currentWorkout == null) {
                            ((ExerciseTrackerActivity)getActivity()).currentWorkout = new CurrentWorkout();
                        }
                        ((ExerciseTrackerActivity)getActivity()).currentWorkout.addToWorkout(exerciseMap.get(id));
                    }
                }
            }
        });

        Button createButton = (Button) rootView.findViewById(R.id.createExerciseButtonAddExercise);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                CreateExerciseFragment frag = new CreateExerciseFragment();
                ft.replace(R.id.container, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return rootView;
    }

    private void addExerciseToTable(ExerciseContent exercise, View rootView) {
        String name = exercise.name;
        String tags = exercise.tags;
        final String description = exercise.description;
        final String notes = exercise.notes;

        LinearLayout exerciseTable = (LinearLayout) rootView.findViewById(R.id.tableExercise);
        RelativeLayout relativeInner = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams rpI = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT);
        int id = View.generateViewId();
        exerciseMap.put(id, exercise);
        relativeInner.setId(id);
        relativeInner.setClickable(true);
        relativeInner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Exercise Details");
                alert.setMessage("");
                final TextView details = new TextView(getActivity());
                details.setText("Description: " + description + "\nNotes: " + notes);
                alert.setView(details);
                alert.setCancelable(false);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();
            }
        });
        //rp.setMargins(0, 0, 0, 0);
        relativeInner.setLayoutParams(rpI);

        TextView nameText = new TextView(getActivity());
        nameText.setText(name);
        nameText.setId(View.generateViewId());
        //nameText.setTextAppearance(getActivity(), R.style.notification_text);
        RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        //dateParams.setMarginStart(20);
        //dateParams.setMarginEnd(150);
        relativeInner.addView(nameText, nameParams);

        TextView tagsText = new TextView(getActivity());
        tagsText.setText(tags);
        tagsText.setId(View.generateViewId());
        //tagsText.setTextAppearance(getActivity(), R.style.notification_text);
        RelativeLayout.LayoutParams tagParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        tagParams.addRule(RelativeLayout.BELOW, nameText.getId());
        relativeInner.addView(tagsText, tagParams);

        RelativeLayout relativeOuter = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams rpO = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        //rp.setMargins(0, 0, 0, 0);
        relativeOuter.setLayoutParams(rpO);

        relativeOuter.addView(relativeInner);

        CheckBox checkBox = new CheckBox(getActivity());
        RelativeLayout.LayoutParams checkBoxParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        checkBoxParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        checkBoxParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeOuter.addView(checkBox, checkBoxParams);

        exerciseTable.addView(relativeOuter, 0);
    }
}
