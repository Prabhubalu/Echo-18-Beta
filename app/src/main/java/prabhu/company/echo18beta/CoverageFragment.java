package prabhu.company.echo18beta;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CoverageFragment extends Fragment {

    RatingBar ratingBar;
    EditText editText;
    Button button;
    String rating, feedback;
    DatabaseReference mdatabase;
    TelephonyManager tel;
    public String carrier, lattitude, longitude, id;
    double latt, longg;
    FusedLocationProviderClient fl;
    Map hashmap;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_coverage, container, false);

        ratingBar = v.findViewById(R.id.ratingbar);
        editText = v.findViewById(R.id.editText);
        button = v.findViewById(R.id.submitbutton);
        mdatabase = FirebaseDatabase.getInstance().getReference();

        tel = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (tel != null) {
            carrier = tel.getNetworkOperatorName().toLowerCase();
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
        }

        fl = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        fl.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latt = location.getLatitude();
                    longg = location.getLongitude();
                }
            }
        });




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                id = mdatabase.push().getKey();

                lattitude = String.valueOf(latt);
                longitude = String.valueOf(longg);

                rating = String.valueOf(ratingBar.getRating());

                feedback = editText.getText().toString();

                hashmap = new HashMap();
                hashmap.put("Lattitute", lattitude);
                hashmap.put("Longitude", longitude);
                hashmap.put("Rating", rating);
                hashmap.put("Feedback",feedback);

                mdatabase.child("feedback").child("app").child("Carrier").child(carrier).child(id).setValue(hashmap);

                Toast.makeText(getActivity(), "FeedBack Submitted", Toast.LENGTH_SHORT).show();

                ratingBar.setRating((float) 0);
                editText.setText("");

            }
        });

        return v;
    }
}