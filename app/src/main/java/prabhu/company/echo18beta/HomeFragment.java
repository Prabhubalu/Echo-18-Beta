package prabhu.company.echo18beta;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.anastr.speedviewlib.SpeedView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prabhu.company.echo18beta.Libraries.BGService;
import prabhu.company.echo18beta.misc.Cell;
import prabhu.company.echo18beta.misc.CellMain;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public static final int INVALID = Integer.MAX_VALUE;
    Intent mServiceIntent;
    private BGService mSensorService;
    Context ctx;
    private MaterialDialog materialDialog;
    public int signalStrengthDbm = INVALID;
    public int signalStrengthAsuLevel = INVALID;
    private FusedLocationProviderClient fusedLocationProviderClient;
    public TextView NetworkType, Dbm, Asu;
    String carrierConnenctionType = "";
    //TextView currentSignalView;
    //Button knowBtn;
    int mSignalStrength = 0;
    String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.INTERNET};
    TelephonyManager Tel, telephonyManager;
    PhoneCustomStateListener MyListener;
    public String carrierName = "", carrierNetwork = "";
    int carrierlang = 0, carriercid = 0, mcc = 0, mnc = 0;
    double mlat = 0, mlang = 0, malt = 0, mspeed = 0;
    SpeedView speedView;
    boolean all = false, run = true;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Map messageMap;
    TextView type, feedback;
    CircularFillableLoaders circularFillableLoaders;
    ImageView symbol;
    Button popupButton;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);


        NetworkType = view.findViewById(R.id.type);
        Dbm = view.findViewById(R.id.dbm);
        Asu = view.findViewById(R.id.asu);
        type = view.findViewById(R.id.type);
        circularFillableLoaders = view.findViewById(R.id.circularFillableLoaders);
        symbol = view.findViewById(R.id.symbol);
        feedback = view.findViewById(R.id.feedback);
        popupButton = view.findViewById(R.id.popup);

        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), popupButton);
                popup.getMenuInflater().inflate(R.menu.top_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getTitle().toString()) {
                            case "Share..":
                                break;
                            case "My Connections":
                                break;
                            case "Feedback":
                                Intent intent = new Intent(getActivity(), FeedbakActivity.class);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });


        messageMap = new HashMap();
        prefs = getActivity().getSharedPreferences("bs.inc.MyService", MODE_PRIVATE);
        editor = prefs.edit();
        all = true;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mlat = location.getLatitude();
                    mlang = location.getLongitude();
                    malt = location.getAltitude();
                    mspeed = location.getSpeed();
                }
            }
        });

        MyListener = new PhoneCustomStateListener();
        Tel = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        carrierName = Tel.getNetworkOperatorName();
        Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                run = true;

                Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            }
        }, 1000);


        mSensorService = new BGService(getActivity());
        mServiceIntent = new Intent(getActivity(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            editor.putBoolean("run", true);
            editor.apply();
            getActivity().startService(mServiceIntent);
        }

        String TAG = "MAIN";


        return view;

    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    class PhoneCustomStateListener extends PhoneStateListener {
        public static final int INVALID = Integer.MAX_VALUE;

        public int signalSupport = 0, signalStrengthValue = 0;
        int where = 0;

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            try {
                messageMap.put("deviceID", Tel.getDeviceId());

                TextView Carriertxt = (TextView) getActivity().findViewById(R.id.carrier);

                String tempText = carrierName.substring(0, 1).toUpperCase() + carrierName.substring(1);
                Carriertxt.setText(tempText);

                List<CellInfo> cellInfoList = Tel.getAllCellInfo();
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoLte) {
                        // cast to CellInfoLte and call all the CellInfoLte methods you need

                        messageMap.put("TestDBM", ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm());
                        messageMap.put("RSRQ", ((CellInfoLte) cellInfo).getCellSignalStrength().getLevel());
                        messageMap.put("TAC", ((CellInfoLte) cellInfo).getCellSignalStrength().getTimingAdvance());

                        if (Build.VERSION.SDK_INT > 25) {
                            messageMap.put("CQI", ((CellInfoLte) cellInfo).getCellSignalStrength().getCqi());
                            messageMap.put("RSRQ", ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrq());
                            messageMap.put("RSSNR", ((CellInfoLte) cellInfo).getCellSignalStrength().getRssnr());
                        }
                    }
                }

                signalStrengthDbm = getSignalStrengthByName(signalStrength, "getDbm");


                signalStrengthAsuLevel = getSignalStrengthByName(signalStrength, "getAsuLevel");

                if (signalStrength.isGsm()) {
                    where = 1;
                } else {
                    where = 2;
                    signalStrengthValue = signalStrength.getCdmaDbm();
                }


                String ssignal = signalStrength.toString();

                String[] parts = ssignal.split(" ");
                int dbm = 0;

                carrierNetwork = getNetworkClass(getActivity());

                GsmCellLocation cellLocation = (GsmCellLocation) Tel.getCellLocation();
                carriercid = cellLocation.getCid();
                carrierlang = cellLocation.getLac() & 0xffff;
                String networkOperator = Tel.getNetworkOperator();
                if (!TextUtils.isEmpty(networkOperator)) {
                    mcc = Integer.parseInt(networkOperator.substring(0, 3));
                    mnc = Integer.parseInt(networkOperator.substring(3));
                }

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mlat = location.getLatitude();
                            mlang = location.getLongitude();
                        }
                    }
                });

                NetworkType.setText(carrierNetwork);
                type.setText(carrierNetwork);
                Dbm.setText("DBM : " + signalStrengthDbm);
                Asu.setText("ASU : " + signalStrengthAsuLevel);


                if (carrierNetwork == "4G") {
                    if (signalStrengthDbm >= -60 && signalStrengthDbm <= -40) {
                        symbol.setImageDrawable(getResources().getDrawable(R.drawable.correct));
                        feedback.setText("GOOD");
                        circularFillableLoaders.setProgress(0);
                        circularFillableLoaders.setColor(Color.parseColor("#00C000"));
                    }
                    if (signalStrengthDbm >= -80 && signalStrengthDbm <= -61) {
                        symbol.setImageDrawable(getResources().getDrawable(R.drawable.correct));
                        feedback.setText("GOOD");
                        circularFillableLoaders.setProgress(10);
                        circularFillableLoaders.setColor(Color.parseColor("#68CB27"));
                    }
                    if (signalStrengthDbm >= -100 && signalStrengthDbm <= -81) {
                        symbol.setImageDrawable(getResources().getDrawable(R.drawable.correct));
                        feedback.setText("GOOD");
                        circularFillableLoaders.setProgress(20);
                        circularFillableLoaders.setColor(Color.parseColor("#68CB27"));
                    }
                    if (signalStrengthDbm >= -110 && signalStrengthDbm <= -101) {
                        symbol.setImageDrawable(getResources().getDrawable(R.drawable.poor));
                        feedback.setText("  AVG");
                        circularFillableLoaders.setProgress(30);
                        circularFillableLoaders.setColor(Color.parseColor("#FA9628"));
                    }
                    if (signalStrengthDbm >= -120 && signalStrengthDbm <= -111) {
                        symbol.setImageDrawable(getResources().getDrawable(R.drawable.poor));
                        feedback.setText("  AVG");
                        circularFillableLoaders.setProgress(45);
                        circularFillableLoaders.setColor(Color.parseColor("#F96622"));
                    }
                    if (signalStrengthDbm >= -130 && signalStrengthDbm <= -121) {
                        symbol.setImageDrawable(getResources().getDrawable(R.drawable.poor));
                        feedback.setText("  AVG");
                        circularFillableLoaders.setProgress(70);
                        circularFillableLoaders.setColor(Color.parseColor("#F9351E"));
                    }
                    if (signalStrengthDbm <= -131) {
                        symbol.setImageDrawable(getResources().getDrawable(R.drawable.wrong));
                        feedback.setText("  BAD");
                        circularFillableLoaders.setProgress(85);
                        circularFillableLoaders.setColor(Color.parseColor("#CA0A16"));
                    }
                }


                messageMap.put("Carrier", carrierName);
                messageMap.put("DBM", signalStrengthDbm);
                messageMap.put("ASU", signalStrengthAsuLevel);
                messageMap.put("NetworkType", carrierNetwork);
                messageMap.put("CellTowerType", carrierConnenctionType);
                messageMap.put("CellId", carriercid);
                messageMap.put("LAC", carrierlang);
                messageMap.put("MCC", mcc);
                messageMap.put("MNC", mnc);
                messageMap.put("MyLatitude", mlat);
                messageMap.put("MyLongitude", mlang);
                messageMap.put("Time", ServerValue.TIMESTAMP);

                // DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference().child("TEST_FOR_SHRAVAN_NEVER_CHECK_OR_REFER_THIS_GET_IT_OR_YOU_WILL_DIE").child("Main");

                //String push_id = fireDB.push().getKey();
                //  fireDB.child(push_id).setValue(messageMap);
                messageMap.clear();
                //Toast.makeText(getApplicationContext(),"Updated in firebase",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("Error", "Error on displayMain " + e.toString());
                try {

                    Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
                } catch (Exception ignore) {
                }
            }
        }
    }

    private int getSignalStrengthByName(SignalStrength signalStrength, String methodName) {
        try {
            Class classFromName = Class.forName(SignalStrength.class.getName());
            java.lang.reflect.Method method = classFromName.getDeclaredMethod(methodName);
            Object object = method.invoke(signalStrength);
            return (int) object;
        } catch (Exception ex) {
            return INVALID;
        }
        //}
    }

    //Git hub integration test

    public String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                carrierConnenctionType = "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                carrierConnenctionType = "EDGE";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                carrierConnenctionType = "CDMA";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                carrierConnenctionType = "1xRTT";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                carrierConnenctionType = "IDEN";
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                carrierConnenctionType = "UMTS";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                carrierConnenctionType = "EVDO";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                carrierConnenctionType = "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                carrierConnenctionType = "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                carrierConnenctionType = "HSPA";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                carrierConnenctionType = "EVDO_B";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                carrierConnenctionType = "EHRPD";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                carrierConnenctionType = "HSPAP";
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                carrierConnenctionType = "LTE";
                return "4G";
            default:
                return "Unknown";
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                } else {
                    // permission denied
                }
                return;
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void onDestroy() {
        try {
            Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
        } catch (Exception e) {

        }
        super.onDestroy();
    }
}
