package prabhu.company.echo18beta;


import android.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpeedFragment extends Fragment {

    PointerSpeedometer speedometer;
    TextView setView;

    public SpeedFragment() {
    }

    float myspeed = 0.0f;
    Button speedbutton;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_speed, container, false);
        speedbutton = view.findViewById(R.id.speedbutton);
        speedometer = view.findViewById(R.id.meter);
        setView = view.findViewById(R.id.speed3);
        speedbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpeedTestTask().execute();
            }
        });
        return view;
    }

    class SpeedTestTask extends AsyncTask<Void, Void, String> {

        public SpeedTestTask() {

        }

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {
                    // called when download/upload is finished
                    Log.v("speedtest", "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                    Log.e("speedtest", "[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
                    String myspeed2 = String.valueOf(report.getTransferRateBit());
                    myspeed = Float.valueOf(myspeed2);

                    //speedometer.speedTo(myspeed);
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                speedometer.setTrembleDegree(0);
                                speedometer.speedTo(0);
                                speedometer.stop();
                                setView.setText(String.valueOf(myspeed / 1048576));
                            }
                        });
                    } catch (Exception ignore) {
                    }
                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
                    // called to notify download/upload progress
                    Log.v("speedtest", "[PROGRESS] progress : " + percent + "%");
                    Log.v("speedtest", "[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                    Log.e("speedtest", "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
                    String myspeed2 = String.valueOf(report.getTransferRateBit());
                    myspeed = Float.valueOf(myspeed2);

                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                speedometer.setTrembleDegree(5);
                                speedometer.speedTo(myspeed / 1048576);


                            }
                        });
                    } catch (Exception ignore) {
                    }

                }
            });

            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");
            return null;
        }
    }
}