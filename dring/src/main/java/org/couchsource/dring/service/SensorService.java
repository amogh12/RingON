package org.couchsource.dring.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import org.couchsource.dring.application.ApplicationContextWrapper;
import org.couchsource.dring.application.Constants;
import org.couchsource.dring.application.DeviceProperty;
import org.couchsource.dring.application.DevicePosition;
import org.couchsource.dring.listener.Listener;
import org.couchsource.dring.listener.phonestate.IncomingCallStateListener;
import org.couchsource.dring.listener.sensor.AccelerometerSensorListener;
import org.couchsource.dring.listener.sensor.LightSensorListener;
import org.couchsource.dring.listener.sensor.ProximitySensorListener;

/**
 * Sticky service that manages all sensor listeners, phone state listener and broadcast receiver.
 * It the main command center of RingON. The service also smartly registers and un-registers
 * poll-based Accelerometer Sensor to save battery.
 *
 * @author Kunal Sanghavi
 */
public class SensorService extends Service implements SensorEventsAggregatorCallback, Constants {


    private static final String TAG = SensorService.class.getName();
    private static volatile boolean isServiceRunning = false;
    private static boolean isAccelerometerAndLightSensorOn = false;
    private static boolean isPhoneListenerRegistered = false;
    private ApplicationContextWrapper context;
    private Listener mAccelerometerSensorListener;
    private Listener mLightSensorListener;
    private Listener mProximitySensorListener;
    private Listener phoneListener;
    private SensorEventsAggregator sensorEventsAggregator;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPrefsListener;
    private String currentDeviceStatus;
    private final Object currentStatusLock = new Object();
    private int countDownToLowPowerMode;

    /**
     * Returns the current status of the service
     *
     * @return boolean indicating whether the service is running or not
     */
    public static boolean isServiceRunning() {
        return isServiceRunning;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        context = new ApplicationContextWrapper(this.getBaseContext());
        if (sensorEventsAggregator == null) {
            sensorEventsAggregator = new SensorEventsAggregator(this);
        }
        if (phoneListener == null) {
            phoneListener = new IncomingCallStateListener(context);
        }
        registerSensorListeners();
        registerSharedPrefsListener();
        flagServiceStatus(true);
        context.setBooleanPreference(RING_ON, SENSOR_SERVICE_ON, true);
        Log.d(TAG, "Service Started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        context.setBooleanPreference(RING_ON, SENSOR_SERVICE_ON, false);
        flagServiceStatus(false);
        unregisterPhoneListener();
        unregisterProximitySensor();
        unregisterAccelerometerAndLightSensors();
        unregisterSharedPrefsListener();
        sensorEventsAggregator = null;
        context = null;
        super.onDestroy();
    }

    @Override
    public void signalNewPosition(DevicePosition devicePosition) {
        if (!isServiceRunning){
            return;
        }
        Log.d(TAG, "Signalled new device status " + devicePosition);
        if (devicePosition == null) {
            unregisterPhoneListener();
            resetCountdownToLowPowerMode();
        } else {
            synchronized (currentStatusLock) {
                if (currentDeviceStatus != devicePosition.name()) {
                    currentDeviceStatus = devicePosition.name();
                    if (devicePosition.isUserPreferredPosition()) {
                        handleNewDevicePlacement(devicePosition.name());
                        resetCountdownToLowPowerMode();
                    }
                } else {
                    if (devicePosition.isUserPreferredPosition()) {
                        if (attemptLowPowerMode()) {
                            Log.d(TAG, "Switched off AccelerometerSensorListener and LightSensorListener with current status " + devicePosition);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void signalDeviceProximityChanged() {
        if (isServiceRunning) {
            exitLowPowerMode();
        }
    }

    @Override
    public ApplicationContextWrapper getContext() {
        return context;
    }

    private static synchronized void flagServiceStatus(boolean isServiceRunning) {
        SensorService.isServiceRunning = isServiceRunning;
        Log.d(TAG, "Is SensorService running? " + isServiceRunning);
    }

    private void handleNewDevicePlacement(String deviceStatus) {
        boolean isActive = context.getBooleanPreference(deviceStatus, DeviceProperty.ACTIVE.name(), false);
        boolean doVibrate = false;
        if (isActive) {
            float ringerLevel = context.getFloatPreference(deviceStatus, DeviceProperty.RINGER.name(), 0);
            doVibrate = context.getBooleanPreference(deviceStatus, DeviceProperty.VIBRATE.name(), false);
            changeRingerLevel(ringerLevel);
        }
        if (doVibrate) {
            registerPhoneListener();
        } else {
            unregisterPhoneListener();
        }
    }

    private synchronized void resetCountdownToLowPowerMode() {
        countDownToLowPowerMode = 10;
    }

    private boolean attemptLowPowerMode() {
        if (countDownToLowPowerMode > 0) {
            countDownToLowPowerMode--;
            return false;
        } else {
            unregisterAccelerometerAndLightSensors();
            countDownToLowPowerMode = 10;
            return true;
        }
    }

    private void signalUserPreferenceChanged() {
        synchronized (currentStatusLock) {
            currentDeviceStatus = null;
        }
        exitLowPowerMode();
    }

    private void exitLowPowerMode() {
        registerAccelerometerAndLightSensors();
    }

    private void registerSensorListeners() {
        registerAccelerometerAndLightSensors();
        registerProximitySensor();
    }

    private synchronized void registerAccelerometerAndLightSensors() {
        if (!isAccelerometerAndLightSensorOn) {
            if (mAccelerometerSensorListener == null) {
                mAccelerometerSensorListener = new AccelerometerSensorListener(sensorEventsAggregator);
            }
            mAccelerometerSensorListener.register();
            Log.d(TAG, "AccelerometerSensorListener registered");

            if (mLightSensorListener == null) {
                mLightSensorListener = new LightSensorListener(sensorEventsAggregator);
            }
            mLightSensorListener.register();
            Log.d(TAG, "LightSensorListener registered");
            isAccelerometerAndLightSensorOn = true;
        }
    }

    private void registerProximitySensor() {
        if (mProximitySensorListener == null) {
            mProximitySensorListener = new ProximitySensorListener(sensorEventsAggregator);
        }
        mProximitySensorListener.register();
        Log.d(TAG, "ProximitySensorListener registered");
    }

    private void unregisterAccelerometerAndLightSensors() {
        if (isAccelerometerAndLightSensorOn) {
            mAccelerometerSensorListener.unregister();
            mAccelerometerSensorListener = null;
            Log.d(TAG, "AccelerometerSensorListener unregistered");
            mLightSensorListener.unregister();
            mLightSensorListener = null;
            Log.d(TAG, "LightSensorListener unregistered");
            isAccelerometerAndLightSensorOn = false;
        }
    }

    private void unregisterProximitySensor() {
        mProximitySensorListener.unregister();
        mProximitySensorListener = null;
        Log.d(TAG, "ProximitySensorListener unregistered");
    }

    private void registerSharedPrefsListener() {
        sharedPrefsListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        Log.d(TAG, "Shared Prefs change detected for " + key);
                        signalUserPreferenceChanged();
                    }
                };

        for (DevicePosition devicePosition : DevicePosition.getAllUserPreferredPositions()) {
            context.getSharedPreferences(devicePosition.name(), Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(sharedPrefsListener);
        }
    }

    private void unregisterSharedPrefsListener() {
        for (DevicePosition devicePosition : DevicePosition.getAllUserPreferredPositions()) {
            context.getSharedPreferences(devicePosition.name(), Context.MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(sharedPrefsListener);
        }
        sharedPrefsListener = null;
    }

    private void registerPhoneListener() {
        if (!isPhoneListenerRegistered) {
            phoneListener.register();
            isPhoneListenerRegistered = true;
            Log.d(TAG, "PhoneListener successfully registered");
        }
    }

    private void unregisterPhoneListener() {
        if (isPhoneListenerRegistered) {
            isPhoneListenerRegistered = false;
            phoneListener.unregister();
            Log.d(TAG, "PhoneListener successfully unregistered");
        }
    }

    private void changeRingerLevel(float ringerLevel) {
        Log.d(TAG, "Ringer " + String.valueOf(ringerLevel));
        AudioManager audioManager = context.getAudioService();
        if (ringerLevel == 0) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, Math.round(ringerLevel / 100 * audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)), AudioManager.FLAG_ALLOW_RINGER_MODES);
        }
    }

}
