package prabhu.company.echo18beta;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.anastr.speedviewlib.PointerSpeedometer;

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