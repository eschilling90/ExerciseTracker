package erik.exercisetracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by eriks_000 on 8/25/2015.
 */
public class ExerciseListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<ExerciseContent> list = null;
    private ArrayList<ExerciseContent> arrayList;
    private String filter;
    private String[] search;

    public ExerciseListViewAdapter(Context context, List<ExerciseContent> list) {
        mContext = context;
        this.list = list;
        inflater = LayoutInflater.from(mContext);
        arrayList = new ArrayList<>();
        arrayList.addAll(list);
        filter = "No Filter";
        search = new String[0];
    }

    public class ViewHolder {
        TextView name;
        TextView tags;
        RelativeLayout inner;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.exercise_list_view_row, null);
            holder.name = (TextView) view.findViewById(R.id.nameTextExerciseListViewRow);
            holder.tags = (TextView) view.findViewById(R.id.tagsTextExerciseListViewRow);
            holder.inner = (RelativeLayout) view.findViewById(R.id.innerExerciseListViewRow);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(list.get(position).name);
        holder.tags.setText(list.get(position).tags.replace(",",", "));
        holder.inner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Exercise Details for " + holder.name.getText().toString());
                alert.setMessage("");
                final TextView details = new TextView(mContext);
                details.setText("Description: " + list.get(position).description + "\nNotes: " + list.get(position).notes);
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

        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
            }
        });
        return view;
    }

    public void setFilter(String fil) {
        filter = fil.toLowerCase(Locale.getDefault());
    }

    public void setSearch(String sea) {
        sea = sea.toLowerCase(Locale.getDefault());
        sea.replace(", ", ",");
        sea.replace(" ,", ",");
        if (sea.contains(",")) {
            search = sea.split(",");
        } else {
            search = new String[1];
            search[0] = sea;
        }
    }

    public void filter() {
        list.clear();
        if (filter.equals("no filter") && search.length == 0) {
            list.addAll(arrayList);
        }
        else {
            for (ExerciseContent e : arrayList) {
                if (e.tags.toLowerCase(Locale.getDefault()).contains(filter) || filter.equals("no filter")) {
                    boolean goodSearch = true;
                    for (int j=0; j<search.length; j++) {
                        if (!e.tags.toLowerCase(Locale.getDefault()).contains(search[j]) && !e.name.toLowerCase(Locale.getDefault()).contains(search[j])) {
                            goodSearch = false;
                        }
                    }
                    if (search.length == 0) {
                        goodSearch = true;
                    }
                    if (goodSearch) {
                        list.add(e);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public ExerciseContent getItemAt(int j) {
        return list.get(j);
    }
}
