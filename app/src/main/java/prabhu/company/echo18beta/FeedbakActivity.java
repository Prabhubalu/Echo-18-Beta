package prabhu.company.echo18beta;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FeedbakActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbak);


    }



}
