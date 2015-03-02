package org.couchsource.dring.application;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Telephony;
import android.telephony.TelephonyManager;

/**
 * author Kunal
 */
public class AppContextWrapper extends ContextWrapper {
    private static final String TAG = AppContextWrapper.class.getName();

    public AppContextWrapper(Context base) {
        super(base);
    }

    public boolean isAudioNormalMode() {
        AudioManager manager = getAudioService();
        if (manager.getMode() == AudioManager.MODE_NORMAL) {
            return true;
        }
        return false;
    }

    public TelephonyManager getTelephonyService() {
        return (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
    }

    public SensorManager getSensorService() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    public AudioManager getAudioService() {
        return (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    public Vibrator getVibratorService() {
        return (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public boolean getBooleanSharedPref(String prefName, String key, boolean defaultVal) {
        SharedPreferences sharedPreferences = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultVal);
    }

    public float getFloatSharedPref(String prefName, String key, float defaultVal) {
        SharedPreferences sharedPreferences = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key, defaultVal);
    }


}
