package erik.exercisetracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by eriks_000 on 8/27/2015.
 */
public class WeightTrackerFragment extends Fragment {
    List<WeightContent> weights = new ArrayList<>();
    WeightListViewAdapter listAdapter;
    View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.weight_tracker_fragment, container, false);

        ExerciseTrackerActivity.httpClient.get(ExerciseTrackerActivity.REQUEST_URL + "weight?emailAddress=" + ExerciseTrackerActivity.email, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                Log.d("debug", "response is: " + new String(response));
                try {
                    JSONObject jsonObject = new JSONObject(new String(response));
                    Gson gson = new Gson();
                    JSONArray weightsJSON = jsonObject.getJSONArray("weights");
                    String statusCode = jsonObject.getString("statusCode");
                    WeightContent[] weightsArray = gson.fromJson(weightsJSON.toString(), WeightContent[].class);
                    for (WeightContent weight : weightsArray) {
                        String isoDate = weight.date;
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date newDate = null;
                        try {
                            newDate = df.parse(isoDate);
                        } catch (ParseException e) {
                            newDate = new Date();
                        }
                        DateFormat formatDate = new SimpleDateFormat("MM/dd/yy");
                        WeightContent displayWeight = new WeightContent();
                        displayWeight.weight = weight.weight;
                        displayWeight.date = formatDate.format(newDate);
                        weights.add(displayWeight);
                    }
                    createWeightTable();
                } catch (JSONException e) {
                    UtilityFunctions.showToast("Error loading weights", getActivity(), rootView);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                UtilityFunctions.showToast("Error retrieving weights", getActivity(), rootView);
            }
        });

        Button submitButton = (Button) rootView.findViewById(R.id.submitButtonWeightTracker);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TextView weightBox = (TextView) rootView.findViewById(R.id.weightTextWeightTracker);
                weightBox.clearFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(weightBox.getWindowToken(), 0);
                String newWeight = weightBox.getText().toString();
                if (!newWeight.isEmpty()) {
                    if (!newWeight.contains(".")) {
                        newWeight = newWeight + ".0";
                    }
                    Gson gson = new Gson();
                    WeightContent weight = new WeightContent();
                    weight.weight = Float.parseFloat(newWeight);
                    StringEntity params = null;
                    try {
                        params = new StringEntity(gson.toJson(weight));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    DateFormat formatDate = new SimpleDateFormat("MM/dd/yy");
                    weight.date = formatDate.format(new Date());
                    listAdapter.addEntry(weight);
                    ExerciseTrackerActivity.httpClient.post(getActivity(), ExerciseTrackerActivity.REQUEST_URL + "weight?emailAddress=" + ExerciseTrackerActivity.email, params, "application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(new String(response));
                                int code = jsonResponse.getInt("statusCode");
                                if (code == 200) {
                                    UtilityFunctions.showToast("Weight added", getActivity(), rootView);
                                    DateFormat df = new SimpleDateFormat("MM/dd/yy");
                                    //addWeightToList(weightBox.getText().toString(), df.format(new Date()), rootView);
                                } else {
                                    UtilityFunctions.showToast("Error adding weight", getActivity(), rootView);
                                }
                            } catch (JSONException e) {
                                Log.d("response", "response is: " + new String(response));
                                UtilityFunctions.showToast("Failed to add weight", getActivity(), rootView);
                            }
                            weightBox.setText("");
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                            UtilityFunctions.showToast("Error adding weight", getActivity(), rootView);
                            weightBox.setText("");
                        }
                    });
                }
            }
        });

        Button goToGraphButton = (Button) rootView.findViewById(R.id.goToGraphViewButtonWeightTracker);
        goToGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView weightBox = (TextView) rootView.findViewById(R.id.weightTextWeightTracker);
                weightBox.clearFocus();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(weightBox.getWindowToken(), 0);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                WeightGraphFragment frag = new WeightGraphFragment();
                ft.replace(R.id.container, frag);
                ft.commit();
            }
        });

        return rootView;
    }

    private void createWeightTable() {
        listAdapter = new WeightListViewAdapter(getActivity(), weights);
        ListView list = (ListView) rootView.findViewById(R.id.weightListViewWeightTracker);
        list.setAdapter(listAdapter);
    }

    public static int getDateDiff(String newDate, String prevDate) {
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = df.parse(prevDate);
            date2 = df.parse(newDate);
        } catch (ParseException e) {
            date1 = date2 = new Date();
        }
        long diffInMills = date2.getTime() - date1.getTime();
        return (int) TimeUnit.DAYS.convert(diffInMills, TimeUnit.MILLISECONDS);
    }

    private void getOldWeights() {
        ExerciseTrackerActivity.httpClient.get("http://info-tracker-web-service.appspot.com/weight?username=Erik", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                Log.d("debug", "response is: " + new String(response));
                try {
                    JSONObject jsonObject = new JSONObject(new String(response));
                    JSONArray weightsJSON = jsonObject.getJSONArray("weights");
                    Gson gson = new Gson();
                    WeightContent[] weights = gson.fromJson(weightsJSON.toString(), WeightContent[].class);
                    for (WeightContent weight : weights) {
                        StringEntity params = new StringEntity(gson.toJson(weight));
                        ExerciseTrackerActivity.httpClient.post(getActivity(), ExerciseTrackerActivity.REQUEST_URL + "weight?emailAddress=erik.schilling@gmail.com", params, "application/json", new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                            }
                        });
                    }
                } catch (JSONException e) {
                    UtilityFunctions.showToast("Error loading weights", getActivity(), rootView);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                UtilityFunctions.showToast("Filed to load weights", getActivity(), rootView);
            }
        });
    }
}
