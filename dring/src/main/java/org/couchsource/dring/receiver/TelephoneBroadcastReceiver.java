package org.couchsource.dring.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.couchsource.dring.application.AppContextWrapper;

public class TelephoneBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = TelephoneBroadcastReceiver.class.getName();
    public static final String ANDROID_INTENT_ACTION_PHONE_STATE = "android.intent.action.PHONE_STATE";
    private IncomingCallStateListener phoneListener;
    private AppContextWrapper contextWrapper;

    public TelephoneBroadcastReceiver(AppContextWrapper contextWrapper) {
        phoneListener = new IncomingCallStateListener(contextWrapper);
        this.contextWrapper = contextWrapper;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "phone state changed");
        TelephonyManager telephony = contextWrapper.getTelephonyService();
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void register(){
        contextWrapper.registerReceiver(this, new IntentFilter(ANDROID_INTENT_ACTION_PHONE_STATE));
    }

    public void unregisterReceiver(){
        contextWrapper.unregisterReceiver(this);
        TelephonyManager telephony = contextWrapper.getTelephonyService();
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
    }
}
