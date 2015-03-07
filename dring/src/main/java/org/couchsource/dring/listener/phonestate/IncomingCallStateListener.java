package org.couchsource.dring.listener.phonestate;

import android.content.Context;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.couchsource.dring.application.AppContextWrapper;

/**
 * author Kunal
 */
public class IncomingCallStateListener extends PhoneStateListener {
    private static final String TAG = IncomingCallStateListener.class.getName();
    //vibrate 7 times
    private static final long[] vibrationPattern = {0, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000};

    private Vibrator vibrator;
    private AppContextWrapper contextWrapper;

    public IncomingCallStateListener(Context context) {
        if (context == null) {
            Log.e(TAG, "Context found null");
        } else {
            contextWrapper = new AppContextWrapper(context);
        }
    }

    public void register(){
        TelephonyManager telephony = contextWrapper.getTelephonyService();
        telephony.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void unregister(){
        TelephonyManager telephony = contextWrapper.getTelephonyService();
        telephony.listen(this, PhoneStateListener.LISTEN_NONE);
    }



    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if (state == TelephonyManager.CALL_STATE_RINGING) {
            Log.d(TAG, "device ringing");
            startVibrate();
        }

        if ((state == TelephonyManager.CALL_STATE_IDLE) ||
                (state == TelephonyManager.CALL_STATE_OFFHOOK)) {
            Log.d(TAG, "device idle or off the hook");
            stopVibrate();
        }
    }

    private void startVibrate() {
        if (contextWrapper != null) {
            vibrator = contextWrapper.getVibratorService();
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(vibrationPattern, -1);
            }
        }
    }

    private void stopVibrate() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
