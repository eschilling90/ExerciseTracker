package erik.exercisetracker;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Alex on 8/17/2015.
 */
public class DescriptiveNotificationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState){
        final View rootView = inflater.inflate(R.layout.descriptive_notification_fragment, container, false);

        Button backButton = (Button) rootView.findViewById(R.id.backButtonDescritpive_notification);
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                NotificationFragment frag = new NotificationFragment();
                ft.replace(R.id.container, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return rootView;
    }
}
