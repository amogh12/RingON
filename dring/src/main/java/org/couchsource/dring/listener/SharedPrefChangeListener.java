package org.couchsource.dring.listener;

import android.content.Context;
import android.content.SharedPreferences;

import org.couchsource.dring.application.ApplicationContextWrapper;
import org.couchsource.dring.application.DevicePosition;
import org.couchsource.dring.application.Event;
import org.couchsource.dring.application.Registrable;
import org.couchsource.dring.service.callback.CallbackForRegistrable;

/**
 * Listens to changes in user preferences on device positions
 * author Kunal Sanghavi
 */
public class SharedPrefChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener, Registrable{
    private final ApplicationContextWrapper context;
    private final CallbackForRegistrable callback;

    public SharedPrefChangeListener(CallbackForRegistrable callback){
        if (callback == null){
            throw new IllegalArgumentException("callback found null");
        }
        this.callback = callback;
        this.context = callback.getContext();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        callback.signalEvent(Event.USER_PREF_CHANGED);
    }

    @Override
    public void register() {
        for (DevicePosition devicePosition : DevicePosition.getAllUserPreferredPositions()) {
            context.getSharedPreferences(devicePosition.name(), Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void unregister() {
        for (DevicePosition devicePosition : DevicePosition.getAllUserPreferredPositions()) {
            context.getSharedPreferences(devicePosition.name(), Context.MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
