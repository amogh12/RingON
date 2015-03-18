package org.couchsource.dring.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.couchsource.dring.application.ApplicationContextWrapper;
import org.couchsource.dring.application.Constants;
import org.couchsource.dring.service.SensorService;

/**
 * Broadcast receiver to listen to {@Link Intent.ACTION_BOOT_COMPLETED} action
 * to turn on the Sensor Service on boot complete.
 * 
 * author Kunal Sanghavi
 */
public class StartUpSignalReceiver extends BroadcastReceiver implements Constants {

    private static final String TAG = StartUpSignalReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationContextWrapper contextWrapper = new ApplicationContextWrapper(context);
        boolean serviceOn = contextWrapper.getBooleanPreference(RING_ON, SENSOR_SERVICE_ON, false);
        if (!serviceOn) {
            return;
        }
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                && (!SensorService.isServiceRunning())) {
            startRingerService(contextWrapper);
        }
    }

    private void startRingerService(ApplicationContextWrapper context) {
        Intent intent = new Intent(context, SensorService.class);
        context.startService(intent);
        Log.i(TAG, "Service Start Requested");
    }

}
