package erik.exercisetracker;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
    ExerciseListViewAdapter listAdapter;
    ArrayAdapter filterAdapter;
    View rootView;
    List<String> tags;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState) {
        rootView = inflater.inflate(R.layout.add_exercise_fragment, container, false);

        tags = new ArrayList<>();
        for (String tag : ExerciseTrackerActivity.exerciseTags) {
            tags.add(tag);
        }
        tags.add(0, "No Filter");

        if (ExerciseTrackerActivity.exerciseList.size() == 0) {
            ExerciseTrackerActivity.httpClient.get(getActivity(), ExerciseTrackerActivity.REQUEST_URL + "exercise", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] response) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(response));
                        JSONArray exercisesJSON = jsonObject.getJSONArray("exercises");
                        String statusCode = jsonObject.getString("statusCode");
                        Gson gson = new Gson();
                        ExerciseContent[] exercises = gson.fromJson(exercisesJSON.toString(), ExerciseContent[].class);
                        for (int j = 0; j < exercises.length; j++) {
                            ExerciseTrackerActivity.exerciseList.add(exercises[j]);
                        }
                        createExerciseTable();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                    UtilityFunctions.showToast("Failed request", getActivity(), rootView);
                }
            });
        } else {
            createExerciseTable();
        }

        Button addButton = (Button) rootView.findViewById(R.id.addButtonAdd_exercise);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView list = (ListView) rootView.findViewById(R.id.exerciseListViewAddExercise);
                for (int j=0; j<list.getChildCount(); j++) {
                    RelativeLayout outer = (RelativeLayout) list.getChildAt(j);
                    if (((CheckBox) outer.getChildAt(1)).isChecked()) {
                        RelativeLayout inner = (RelativeLayout) outer.getChildAt(0);
                        int id = inner.getId();
                        if (((ExerciseTrackerActivity)getActivity()).currentWorkout == null) {
                            ((ExerciseTrackerActivity)getActivity()).currentWorkout = new CurrentWorkout();
                        }
                        ((ExerciseTrackerActivity)getActivity()).currentWorkout.addToWorkout(((ExerciseListViewAdapter) list.getAdapter()).getItemAt(j));
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

    private void createExerciseTable() {
        listAdapter = new ExerciseListViewAdapter(getActivity(), ExerciseTrackerActivity.exerciseList);
        ListView list = (ListView) rootView.findViewById(R.id.exerciseListViewAddExercise);
        list.setAdapter(listAdapter);
        EditText search = (EditText) rootView.findViewById(R.id.searchBarTextAdd_exercise);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = ((EditText)rootView.findViewById(R.id.searchBarTextAdd_exercise)).getText().toString();
                listAdapter.setSearch(searchText);
                listAdapter.filter();
            }
        });

        Spinner filter = (Spinner) rootView.findViewById(R.id.filterSpinnerAdd_exercise);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filterText = ((Spinner) rootView.findViewById(R.id.filterSpinnerAdd_exercise)).getSelectedItem().toString();
                listAdapter.setFilter(filterText);
                listAdapter.filter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        filterAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, tags);
        Spinner tagSpinner = (Spinner) rootView.findViewById(R.id.filterSpinnerAdd_exercise);
        tagSpinner.setAdapter(filterAdapter);
    }
}
