package erik.exercisetracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by eriks_000 on 8/22/2015.
 */
public class PersonalRecordsFragment extends Fragment {
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState) {
        rootView = inflater.inflate(R.layout.personal_records_fragment, container, false);

        ExerciseTrackerActivity.httpClient.get(getActivity(), ExerciseTrackerActivity.REQUEST_URL + "personalRecords?emailAddress=" + ExerciseTrackerActivity.email, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(response));
                    String statusCode = jsonObject.getString("statusCode");
                    String records = jsonObject.getString("records");
                    switch (Integer.parseInt(statusCode)) {
                        case 200:
                            String[] splitRecords = records.split(",");
                            if (!splitRecords[0].isEmpty()) {
                                for (int j = 0; j < splitRecords.length; j++) {
                                    String[] temp = splitRecords[j].split(":");
                                    String name = temp[0];
                                    String weight = temp[1];
                                    LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View row = li.inflate(R.layout.personal_records_row, null);
                                    EditText nameText = (EditText) row.findViewById(R.id.nameTextPersonalRecordsRow);
                                    nameText.setText(name);
                                    EditText weightText = (EditText) row.findViewById(R.id.weightTextPersonalRecordsRow);
                                    weightText.setText(weight);
                                    ImageButton removeButton = (ImageButton) row.findViewById(R.id.removeButtonPersonalRecordsRow);
                                    removeButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            LinearLayout row = (LinearLayout) v.getParent();
                                            LinearLayout table = (LinearLayout) row.getParent();
                                            table.removeView(row);
                                        }
                                    });
                                    LinearLayout table = (LinearLayout) rootView.findViewById(R.id.recordsTablePersonalRecords);
                                    table.addView(row);
                                }
                            }
                            UtilityFunctions.showToast("Loaded personal records", getActivity(), rootView);
                            break;
                        default:
                            UtilityFunctions.showToast("Failed to load records, invalid email address", getActivity(), rootView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                UtilityFunctions.showToast("Failed to load records", getActivity(), rootView);
            }
        });

        Button saveButton = (Button) rootView.findViewById(R.id.saveButtonPersonalRecords);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout table = (LinearLayout) rootView.findViewById(R.id.recordsTablePersonalRecords);
                String records = "";
                for (int j=0; j<table.getChildCount(); j++) {
                    LinearLayout row = (LinearLayout) table.getChildAt(j);
                    EditText name = (EditText) row.getChildAt(0);
                    EditText weight = (EditText) row.getChildAt(1);
                    if (!name.getText().toString().isEmpty() && !weight.getText().toString().isEmpty()) {
                        if (records.isEmpty()) {
                            records = name.getText().toString() + ":" + weight.getText().toString();
                        } else {
                            records += ", " + name.getText().toString() + ":" + weight.getText().toString();
                        }
                    }
                }
                StringEntity params = null;
                try {
                    params = new StringEntity(records);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ExerciseTrackerActivity.httpClient.post(getActivity(), ExerciseTrackerActivity.REQUEST_URL + "personalRecords?emailAddress=" + ExerciseTrackerActivity.email, params, "text/plain", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] response) {
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response));
                            String statusCode = jsonObject.getString("statusCode");
                            switch (Integer.parseInt(statusCode)) {
                                case 200:
                                    UtilityFunctions.showToast("Personal records saved", getActivity(), rootView);
                                    break;
                                default:
                                    UtilityFunctions.showToast("Failed to save records, invalid email", getActivity(), rootView);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                        UtilityFunctions.showToast("Failed to save records", getActivity(), rootView);
                    }
                });
            }
        });

        Button addButton = (Button) rootView.findViewById(R.id.addWeightButtonPersonalRecords);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View row = li.inflate(R.layout.personal_records_row, null);
                ImageButton removeButton = (ImageButton) row.findViewById(R.id.removeButtonPersonalRecordsRow);
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout row = (LinearLayout) v.getParent();
                        LinearLayout table = (LinearLayout) row.getParent();
                        table.removeView(row);
                    }
                });
                LinearLayout table = (LinearLayout) rootView.findViewById(R.id.recordsTablePersonalRecords);
                table.addView(row);
            }
        });
        return rootView;
    }
}
