package org.couchsource.dring.application;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Wrapper class over Context.
 *
 * author Kunal Sanghavi
 */
public class ApplicationContextWrapper extends ContextWrapper {
    private static final String TAG = ApplicationContextWrapper.class.getName();

    public ApplicationContextWrapper(Context base) {
        super(base);
    }

    /**
     * Checks if audio mode is "Normal"
     * @return boolean
     */
    public boolean isAudioNormalMode() {
        AudioManager manager = getAudioService();
        if (manager.getMode() == AudioManager.MODE_NORMAL) {
            return true;
        }
        return false;
    }

    /**
     * Serves up telephony Service
     * @return {@link android.telephony.TelephonyManager}
     */
    public final TelephonyManager getTelephonyService() {
        return (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * Returns device SensorService
     * @return {@link android.hardware.SensorManager}
     */
    public final SensorManager getSensorService() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    /**
     * Gets Audio Service
     * @return {@link android.media.AudioManager}
     */
    public final AudioManager getAudioService() {
        return (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * Gets vibration service
     * @return {@link android.os.Vibrator}
     */
    public final Vibrator getVibratorService() {
        return (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * Gets boolean shared preference
     *
     * @param prefName required name of the preference.
     * @param key required preference key
     * @param defaultVal default value to return if pref key not found
     * @return boolean
     * @throws {@link java.lang.IllegalArgumentException} if prefName or key are empty.
     */
    public final boolean getBooleanPreference(String prefName, String key, boolean defaultVal) {
        if (TextUtils.isEmpty(prefName) || TextUtils.isEmpty(key)){
            throw new IllegalArgumentException("prefName or key cannot be empty.");
        }
        SharedPreferences sharedPreferences = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultVal);
    }

    /**
     * Gets float shared preference
     *
     * @param prefName required name of the preference.
     * @param key required preference key
     * @param defaultVal default value to return if pref key not found
     * @return float
     * @throws {@link java.lang.IllegalArgumentException} if prefName or key are empty.
     */
    public final float getFloatPreference(String prefName, String key, float defaultVal) {
        if (TextUtils.isEmpty(prefName) || TextUtils.isEmpty(key)){
            throw new IllegalArgumentException("prefName or key cannot be empty.");
        }
        SharedPreferences sharedPreferences = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key, defaultVal);
    }

    /**
     * Sets a boolean shared preference
     *
     * @param prefName required name of the preference.
     * @param key required preference key
     * @param value boolean value to be set.
     */
    public final void setBooleanPreference(String prefName, String key, boolean value){
        if (TextUtils.isEmpty(prefName) || TextUtils.isEmpty(key)){
            return;
        }
        SharedPreferences.Editor editor = getSharedPreferences(prefName, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Sets a float shared preference
     *
     * @param prefName required name of the preference.
     * @param key required preference key
     * @param value float value to be set.
     */
    public final void setFloatPreference(String prefName, String key, float value){
        if (TextUtils.isEmpty(prefName) || TextUtils.isEmpty(key)){
            return;
        }
        SharedPreferences.Editor editor = getSharedPreferences(prefName, Context.MODE_PRIVATE).edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * Is device ringing right now?
     * @return boolean
     */
    public boolean isDeviceRinging(){
        TelephonyManager telephonyManager = getTelephonyService();
        return (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_RINGING);
    }

}
