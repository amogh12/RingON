package org.couchsource.dring.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.util.Log;

import org.couchsource.dring.application.Constants;
import org.couchsource.dring.service.SensorService;

/**
 * author Kunal
 */
public class StartUpSignalReceiver extends BroadcastReceiver implements Constants{

    private static final String TAG = StartUpSignalReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences(RING_ON, Context.MODE_PRIVATE);
        if (preferences.getBoolean(SENSOR_SERVICE_ON,false)){
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                if (!SensorService.isServiceRunning())
                    startRingerService(context);
            }
        }
    }

    private void startRingerService(Context context) {
        Intent intent= new Intent(context, SensorService.class);
        context.startService(intent);
        Log.i(TAG, "Service Start Requested");
    }

}
