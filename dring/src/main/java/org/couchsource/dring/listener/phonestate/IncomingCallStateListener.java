package org.couchsource.dring.listener.phonestate;

import android.content.Context;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.couchsource.dring.application.ApplicationContextWrapper;
import org.couchsource.dring.application.Registrable;

/**
 * Registrable to listen call state if "Vibrate On Ring" feature is turned on.
 *
 * author Kunal Sanghavi
 */
public class IncomingCallStateListener extends PhoneStateListener implements Registrable {
    private static final String TAG = IncomingCallStateListener.class.getName();
    //vibrate 7 times
    private static final long[] vibrationPattern = {0, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000};
    private Vibrator vibrator;
    private ApplicationContextWrapper context;

    /**
     * Create new instance
     * @param context
     */
    public IncomingCallStateListener(Context context) {
        if (context == null) {
            Log.e(TAG, "Context found null");
        } else {
            this.context = new ApplicationContextWrapper(context);
        }
    }

    @Override
    public void register(){
        TelephonyManager telephony = context.getTelephonyService();
        telephony.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void unregister(){
        stopVibrate();
        TelephonyManager telephony = context.getTelephonyService();
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
        if (context != null) {
            vibrator = context.getVibratorService();
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
