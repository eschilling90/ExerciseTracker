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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eriks_000 on 8/17/2015.
 */
public class CreateWorkoutFragment extends Fragment {

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState) {
        rootView = inflater.inflate(R.layout.create_workout_fragment, container, false);

        if (((ExerciseTrackerActivity)getActivity()).currentWorkout != null) {
            List<ExerciseContent> exercises = ((ExerciseTrackerActivity) getActivity()).currentWorkout.getWorkoutExercises();
            for (int j = 0; j < exercises.size(); j++) {
                addExerciseToWorkout(exercises.get(j), j);
            }
        }

        Button createButton = (Button) rootView.findViewById(R.id.createButtonCreateWorkout);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText workoutName = (EditText) rootView.findViewById(R.id.workoutNameCreateWorkout);
                if (workoutName.getText().toString().isEmpty()) {
                    UtilityFunctions.showToast("Need to set a name", getActivity(), rootView);
                } else {
                    EditText workoutDescription = (EditText) rootView.findViewById(R.id.workoutDescriptionTextCreateWorkout);
                    TextView workoutTags = (TextView) rootView.findViewById(R.id.tagsTextCreateWorkout);

                    if (((ExerciseTrackerActivity) getActivity()).currentWorkout == null) {
                        ((ExerciseTrackerActivity) getActivity()).currentWorkout = new CurrentWorkout();
                    }
                    ((ExerciseTrackerActivity) getActivity()).currentWorkout.name = workoutName.getText().toString();
                    ((ExerciseTrackerActivity) getActivity()).currentWorkout.description = workoutDescription.getText().toString();
                    ((ExerciseTrackerActivity) getActivity()).currentWorkout.tags = workoutTags.getText().toString();

                    List<ExerciseContent> tempExercises = new ArrayList<ExerciseContent>();
                    List<String> tempSets = new ArrayList<String>();
                    LinearLayout exerciseTable = (LinearLayout) rootView.findViewById(R.id.tableExercisesCreateWorkout);
                    for (int j = 0; j < exerciseTable.getChildCount(); j++) {
                        RelativeLayout outer = (RelativeLayout) exerciseTable.getChildAt(j);
                        RelativeLayout inner = (RelativeLayout) outer.getChildAt(1);
                        EditText setsText = (EditText) inner.getChildAt(1);
                        String sets = setsText.getText().toString();
                        tempSets.add(sets);
                        ExerciseContent exerciseInfo = ((ExerciseTrackerActivity) getActivity()).currentWorkout.getExercise(j);
                        tempExercises.add(exerciseInfo);
                    }
                    ((ExerciseTrackerActivity) getActivity()).currentWorkout.exercises.clear();
                    for (int j = 0; j < tempExercises.size(); j++) {
                        ((ExerciseTrackerActivity) getActivity()).currentWorkout.addToWorkout(tempExercises.get(j), tempSets.get(j));
                    }

                    Gson gson = new Gson();
                    String json = gson.toJson(((ExerciseTrackerActivity) getActivity()).currentWorkout);
                    StringEntity params = null;
                    try {
                        params = new StringEntity(json);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    ExerciseTrackerActivity.httpClient.post(getActivity(), ExerciseTrackerActivity.REQUEST_URL + "workout?emailAddress=" + ExerciseTrackerActivity.email, params, "application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] response) {
                            UtilityFunctions.showToast("Created Workout", getActivity(), rootView);
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                            UtilityFunctions.showToast("Failed to create workout", getActivity(), rootView);
                        }
                    });
                    Bundle workoutInfo = new Bundle();
                    workoutInfo.putString("newName", ((ExerciseTrackerActivity) getActivity()).currentWorkout.name);
                    workoutInfo.putString("newDescription", ((ExerciseTrackerActivity) getActivity()).currentWorkout.description);
                    workoutInfo.putString("newTags", ((ExerciseTrackerActivity) getActivity()).currentWorkout.tags);
                    ((ExerciseTrackerActivity) getActivity()).currentWorkout = new CurrentWorkout();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    SendWorkoutFragment frag = new SendWorkoutFragment();
                    frag.setArguments(workoutInfo);
                    ft.replace(R.id.container, frag);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

        Button addExercise = (Button) rootView.findViewById(R.id.addExercisesButtonCreateWorkout);
        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                AddExerciseFragment frag = new AddExerciseFragment();
                ft.replace(R.id.container, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        Button backButton = (Button) rootView.findViewById(R.id.backButtonCreateWorkout);
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

        Button addTagButton = (Button) rootView.findViewById(R.id.addTagButtonCreateWorkout);
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("New Tag");
                alert.setMessage("");
                final EditText input = new EditText(getActivity());
                alert.setView(input);
                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView tags = (TextView) rootView.findViewById(R.id.tagsTextCreateWorkout);
                        String tagsContent = tags.getText().toString();
                        if (tagsContent.isEmpty()) {
                            tags.setText(input.getText().toString());
                        } else {
                            tags.setText(tagsContent + "," + input.getText().toString());
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();
            }
        });
        Button removeTagButton = (Button) rootView.findViewById(R.id.removeTagButtonCreateWorkout);
        removeTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tags = (TextView) rootView.findViewById(R.id.tagsTextCreateWorkout);
                String tagsContent = tags.getText().toString();
                if (!tagsContent.isEmpty()) {
                    int index = tagsContent.lastIndexOf(",");
                    if (index != -1) {
                        tags.setText(tagsContent.substring(0, index));
                    } else {
                        tags.setText("");
                    }
                }
            }
        });

        return rootView;
    }

    private void addExerciseToWorkout(final ExerciseContent exercise, final int j) {
        final LinearLayout exerciseTable = (LinearLayout) rootView.findViewById(R.id.tableExercisesCreateWorkout);
        RelativeLayout relativeOuter = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams rpO = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT);
        int id = View.generateViewId();
        relativeOuter.setId(id);
        relativeOuter.setLayoutParams(rpO);

        TextView nameText = new TextView(getActivity());
        nameText.setText(exercise.name);
        nameText.setId(View.generateViewId());
        RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        nameParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        nameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relativeOuter.addView(nameText, nameParams);

        RelativeLayout relativeInner = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams rpI = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT);
        relativeInner.setId(View.generateViewId());
        rpI.addRule(RelativeLayout.END_OF, nameText.getId());
        relativeInner.setLayoutParams(rpI);

        Button removeButton = new Button(getActivity());
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        removeButton.setText("-");
        removeButton.setId(View.generateViewId());
        buttonParams.addRule(RelativeLayout.ALIGN_TOP, nameText.getId());
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeInner.addView(removeButton, buttonParams);

        EditText sets = new EditText(getActivity());
        sets.setId(View.generateViewId());
        RelativeLayout.LayoutParams tagParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        tagParams.addRule(RelativeLayout.START_OF, removeButton.getId());
        relativeInner.addView(sets, tagParams);

        relativeOuter.addView(relativeInner, rpI);

        exerciseTable.addView(relativeOuter);
    }
}
