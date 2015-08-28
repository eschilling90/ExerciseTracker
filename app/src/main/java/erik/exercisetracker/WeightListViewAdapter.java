package erik.exercisetracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by eriks_000 on 8/28/2015.
 */
public class WeightListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<WeightContent> list = null;
    private ArrayList<WeightContent> arrayList;

    public WeightListViewAdapter(Context context, List<WeightContent> list) {
        mContext = context;
        this.list = list;
        inflater = LayoutInflater.from(mContext);
        arrayList = new ArrayList<>();
        arrayList.addAll(list);
    }

    public class ViewHolder {
        TextView weight;
        TextView date;
    }

    public void addEntry(WeightContent weight) {
        list.add (0, weight);
        this.notifyDataSetChanged();
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
            view = inflater.inflate(R.layout.weight_tracker_list_view_row, null);
            holder.weight = (TextView) view.findViewById(R.id.weightTextWeightTrackerRow);
            holder.date = (TextView) view.findViewById(R.id.dateTextWeightTrackerRow);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.weight.setText(Float.toString(list.get(position).weight));
        holder.date.setText(list.get(position).date);

        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
            }
        });
        return view;
    }

    public WeightContent getItemAt(int j) {
        return list.get(j);
    }
}
