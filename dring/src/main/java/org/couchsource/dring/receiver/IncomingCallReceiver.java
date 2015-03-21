package org.couchsource.dring.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

import org.couchsource.dring.application.Event;
import org.couchsource.dring.application.Registrable;
import org.couchsource.dring.service.callback.CallbackForRegistrable;

/**
 * Incoming call receiver alerts the Sensor service in the event of an incoming call
 * author Kunal Sanghavi
 */
public class IncomingCallReceiver extends BroadcastReceiver implements Registrable {
    private final CallbackForRegistrable callback;

    public IncomingCallReceiver(CallbackForRegistrable callback) {
        if (callback == null){
            throw new IllegalArgumentException("callback cannot be null");
        }
        this.callback = callback;
    }

    @Override
    public void register(){
        callback.getContext().registerReceiver(this, new IntentFilter("android.intent.action.PHONE_STATE"));
    }

    @Override
    public void unregister(){
        callback.getContext().unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                callback.signalEvent(Event.INCOMING_CALL);
            }
        }

    }
}
