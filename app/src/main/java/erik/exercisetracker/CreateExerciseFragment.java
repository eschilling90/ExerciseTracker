package erik.exercisetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
 * Created by eriks_000 on 8/14/2015.
 */
public class CreateExerciseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState) {
        final View rootView = inflater.inflate(R.layout.create_exercise_fragment, container, false);

        Button createButton = (Button) rootView.findViewById(R.id.createButtonCreate_exercise);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailAddress = ExerciseTrackerActivity.email;
                StringEntity params = null;
                ExerciseContent exercise = new ExerciseContent();
                exercise.name = ((EditText) rootView.findViewById(R.id.exerciseNameTextCreate_exercise)).getText().toString();
                exercise.multimedia = ((EditText) rootView.findViewById(R.id.multiMediaTextCreate_exercise)).getText().toString();
                exercise.description = ((EditText) rootView.findViewById(R.id.exerciseDescriptionTextCreate_exercise)).getText().toString();
                exercise.notes = ((EditText) rootView.findViewById(R.id.exerciseNotesTextCreate_exercise)).getText().toString();
                exercise.tags = ((TextView) rootView.findViewById(R.id.tagsTextViewCreateExercise)).getText().toString();
                Gson gson = new Gson();
                String json = gson.toJson(exercise);
                try {
                    params = new StringEntity(json);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (!exercise.name.isEmpty()) {
                    ExerciseTrackerActivity.httpClient.post(getActivity(), ExerciseTrackerActivity.REQUEST_URL + "exercise?emailAddress=" + emailAddress, params, "application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] response) {
                            UtilityFunctions.showToast("Added Exercise", getActivity(), rootView);
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                            UtilityFunctions.showToast("Failed request", getActivity(), rootView);
                        }
                    });
                } else {
                    UtilityFunctions.showToast("Name required", getActivity(), rootView);
                }
            }
        });

        Button addTagButton = (Button) rootView.findViewById(R.id.addTagButtonCreateExercise);
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
                        TextView tags = (TextView) rootView.findViewById(R.id.tagsTextViewCreateExercise);
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

        Button removeTagButton = (Button) rootView.findViewById(R.id.removeTagButtonCreateExercise);
        removeTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tags = (TextView) rootView.findViewById(R.id.tagsTextViewCreateExercise);
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

        Button backButton = (Button) rootView.findViewById(R.id.backButtonCreate_exercise);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                AddExerciseFragment frag = new AddExerciseFragment();
                ft.replace(R.id.container, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return rootView;
    }
}
