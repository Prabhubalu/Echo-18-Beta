package prabhu.company.echo18beta;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.anastr.speedviewlib.PointerSpeedometer;

import java.util.Timer;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpeedFragment extends Fragment {

    PointerSpeedometer speedometer;

    public SpeedFragment() {
        // Required empty public constructor
    }
    float myspeed = 0.0f;
    Button speedbutton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_speed, container, false);
        speedbutton = view.findViewById(R.id.speedbutton);
        speedometer = view.findViewById(R.id.meter);
        speedbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpeedTestTask().execute();

                speedometer.setSpeedAt(myspeed/1024);
            }
        });
        return view;
    }
    class SpeedTestTask extends AsyncTask<Void, Void, String> {

        public SpeedTestTask(){

        }

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {
                    // called when download/upload is finished
                    Log.v("speedtest", "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest", "[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
                    String myspeed2=String.valueOf(report.getTransferRateBit());
                    myspeed=Float.valueOf(myspeed2);
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
                    Log.v("speedtest", "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
                }
            });

            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");


            return null;
        }
    }
}