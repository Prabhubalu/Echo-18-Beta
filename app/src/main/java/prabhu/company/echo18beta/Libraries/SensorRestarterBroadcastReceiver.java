package prabhu.company.echo18beta.Libraries;

/**
 * Created by shrey on 20-03-2018.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    Boolean run = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("bs.inc.MyService", MODE_PRIVATE);
        run = prefs.getBoolean("run", true);
        //
        if (run) {
            Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stopped!");
            context.startService(new Intent(context, BGService.class));
        } else {

        }
    }
}