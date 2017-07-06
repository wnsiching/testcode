package willer.milkit;

/**
 * Created by User on 5/5/2017.
 */

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

public class BootComplete extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Milkit", "BootComplete invoke AutoStart");
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, AutoStartUp.class);
            context.startService(serviceIntent);
        }
    }

}