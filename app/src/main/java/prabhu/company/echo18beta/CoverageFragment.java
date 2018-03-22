package prabhu.company.echo18beta;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class CoverageFragment extends Fragment {


    public CoverageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_coverage, container, false);
        boolean isAirplane = Settings.System.getInt(getActivity().getContentResolver(),Settings.System.AIRPLANE_MODE_ON,0) == 1;

        final AlertDialog.Builder alretDialog =  new AlertDialog.Builder(getActivity());
        alretDialog.setTitle("Survey");
        alretDialog.setMessage("Please attend our survey");
        alretDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Toast.makeText(getActivity(),"YOU CANNOT QUIT!!!",Toast.LENGTH_SHORT).show();

                AlertDialog alertDialog = alretDialog.create();
                alertDialog.show();
            }
        });
        alretDialog.setPositiveButton("ANSWER SURVEY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(),"YAYY!",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alretDialog.setNegativeButton("NO ANSWER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(),"YOU CANNOT ANSWER SURVEY THEN NOOOO!",Toast.LENGTH_LONG).show();

                AlertDialog alertDialog = alretDialog.create();
                alertDialog.show();
            }
        });

        AlertDialog alertDialog = alretDialog.create();
        alertDialog.show();
        return view;
    }

}
