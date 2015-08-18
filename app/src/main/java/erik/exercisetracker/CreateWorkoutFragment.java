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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by eriks_000 on 8/17/2015.
 */
public class CreateWorkoutFragment extends Fragment {

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState) {
        rootView = inflater.inflate(R.layout.create_workout_fragment, container, false);

        List<ExerciseContent> exercises = CurrentWorkout.getWorkoutExercises();
        Iterator<ExerciseContent> iterator = exercises.iterator();
        while (iterator.hasNext()) {
            addExerciseToWorkout(iterator.next());
        }
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
        return rootView;
    }

    private void addExerciseToWorkout(ExerciseContent exercise) {
        LinearLayout exerciseTable = (LinearLayout) rootView.findViewById(R.id.tableExercisesCreateWorkout);
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
        removeButton.setId(View.generateViewId());
        buttonParams.addRule(RelativeLayout.ALIGN_TOP, nameText.getId());
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        relativeInner.addView(removeButton, buttonParams);

        EditText sets = new EditText(getActivity());
        sets.setId(View.generateViewId());
        RelativeLayout.LayoutParams tagParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        tagParams.addRule(RelativeLayout.START_OF, removeButton.getId());
        relativeInner.addView(sets, tagParams);

        relativeOuter.addView(relativeInner, rpI);

        exerciseTable.addView(relativeOuter, 0);
    }
}
