package org.couchsource.dring.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.couchsource.dring.application.ApplicationContextWrapper;
import org.couchsource.dring.application.Constants;
import org.couchsource.dring.application.DeviceProperty;
import org.couchsource.dring.application.DevicePosition;
import org.couchsource.dring.application.Event;
import org.couchsource.dring.application.Registrable;
import org.couchsource.dring.listener.SharedPrefChangeListener;
import org.couchsource.dring.listener.phonestate.IncomingCallStateListener;
import org.couchsource.dring.listener.sensor.AccelerometerSensorListener;
import org.couchsource.dring.listener.sensor.LightSensorListener;
import org.couchsource.dring.listener.sensor.ProximitySensorListener;
import org.couchsource.dring.receiver.IncomingCallReceiver;
import org.couchsource.dring.service.callback.CallbackForRegistrable;
import org.couchsource.dring.service.callback.CallbackForSensorEventsAggregator;

/**
 * Sticky service that manages all sensor listeners, phone state listener and broadcast receiver.
 * It the main command center of RingON. The service also smartly registers and un-registers
 * poll-based Accelerometer Sensor to save battery.
 *
 * @author Kunal Sanghavi
 */
public class SensorService extends Service implements CallbackForSensorEventsAggregator, CallbackForRegistrable, Constants {


    private static final String TAG = SensorService.class.getName();
    private static volatile boolean isServiceRunning = false;
    private static boolean isAccelerometerAndLightSensorOn = false;
    private static boolean isPhoneListenerRegistered = false;
    private static boolean isIncomingCallReceiverRegistered = false;
    private static boolean isSharedPrefListenerRegistered = false;
    private ApplicationContextWrapper context;
    private SensorEventsAggregator sensorEventsAggregator;
    private Registrable mAccelerometerSensorListener;
    private Registrable mLightSensorListener;
    private Registrable mProximitySensorListener;
    private Registrable phoneListener;
    private Registrable incomingCallReceiver;
    private Registrable sharedPrefsListener;
    private String currentDeviceStatus;
    private int countDownToLowPowerMode;

    /**
     * Returns the current status of the service
     *
     * @return boolean indicating whether the service is running or not
     */
    public synchronized static boolean isServiceRunning() {
        return isServiceRunning;
    }

    private static void flagServiceStatus(boolean isServiceRunning) {
        SensorService.isServiceRunning = isServiceRunning;
        Log.d(TAG, "Is SensorService running? " + isServiceRunning);
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
        registerIncomingCallReceiver();
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
        unregisterIncomingCallReceiver();
        sensorEventsAggregator = null;
        context = null;
        super.onDestroy();
    }

    @Override
    public synchronized void signalNewPosition(DevicePosition devicePosition) {
        if (!isServiceRunning) {
            return;
        }
        Log.d(TAG, "Signalled new device status " + devicePosition);
        if (devicePosition == null) {
            unregisterPhoneListener();
            resetCountdownToLowPowerMode();
        } else {
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

    @Override
    public synchronized void signalDeviceProximityChanged() {
        exitLowPowerMode();
    }

    @Override
    public ApplicationContextWrapper getContext() {
        return context;
    }

    @Override
    public synchronized void signalEvent(Event event) {
        if (event == null) {
            return;
        }
        currentDeviceStatus = null;
        exitLowPowerMode();

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

    private void resetCountdownToLowPowerMode() {
        countDownToLowPowerMode = 10;
    }

    private boolean attemptLowPowerMode() {
        if (countDownToLowPowerMode > 0) {
            countDownToLowPowerMode--;
            return false;
        } else {
            unregisterAccelerometerAndLightSensors();
            resetCountdownToLowPowerMode();
            return true;
        }
    }

    private void exitLowPowerMode() {
        if (isServiceRunning()) {
            resetCountdownToLowPowerMode();
            registerAccelerometerAndLightSensors();
        }
    }

    private void registerSensorListeners() {
        registerAccelerometerAndLightSensors();
        registerProximitySensor();
    }

    private void registerAccelerometerAndLightSensors() {
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
            if (mAccelerometerSensorListener != null) {
                mAccelerometerSensorListener.unregister();
            }
            mAccelerometerSensorListener = null;
            Log.d(TAG, "AccelerometerSensorListener unregistered");
            if (mLightSensorListener != null) {
                mLightSensorListener.unregister();
            }
            mLightSensorListener = null;
            Log.d(TAG, "LightSensorListener unregistered");
            isAccelerometerAndLightSensorOn = false;
        }
    }

    private void unregisterProximitySensor() {
        if (mProximitySensorListener != null) {
            mProximitySensorListener.unregister();
        }
        mProximitySensorListener = null;
        Log.d(TAG, "ProximitySensorListener unregistered");
    }

    private void registerSharedPrefsListener() {
        if (!isSharedPrefListenerRegistered) {
            if (sharedPrefsListener == null) {
                sharedPrefsListener = new SharedPrefChangeListener(this);
            }
            sharedPrefsListener.register();
            isSharedPrefListenerRegistered = true;
            Log.d(TAG, "Shared pref change listener registered");
        }
    }

    private void unregisterSharedPrefsListener() {
        if (isSharedPrefListenerRegistered) {
            isSharedPrefListenerRegistered = false;
            if (sharedPrefsListener != null) {
                sharedPrefsListener.unregister();
            }
            sharedPrefsListener = null;
            Log.d(TAG, "Shared pref listener unregistered");
        }
    }

    private void registerPhoneListener() {
        if (!isPhoneListenerRegistered) {
            if (phoneListener == null) {
                phoneListener = new IncomingCallStateListener(this);
            }
            phoneListener.register();
            isPhoneListenerRegistered = true;
            Log.d(TAG, "PhoneListener successfully registered");
        }
    }

    private void unregisterPhoneListener() {
        if (isPhoneListenerRegistered) {
            isPhoneListenerRegistered = false;
            if (phoneListener != null) {
                phoneListener.unregister();
            }
            phoneListener = null;
            Log.d(TAG, "PhoneListener successfully unregistered");
        }
    }

    private void registerIncomingCallReceiver() {
        if (!isIncomingCallReceiverRegistered) {
            if (incomingCallReceiver == null) {
                incomingCallReceiver = new IncomingCallReceiver(this);
            }
            incomingCallReceiver.register();
            isIncomingCallReceiverRegistered = true;
            Log.d(TAG, "Incoming call receiver registered");
        }
    }

    private void unregisterIncomingCallReceiver() {
        if (isIncomingCallReceiverRegistered) {
            isIncomingCallReceiverRegistered = false;
            if (incomingCallReceiver != null) {
                incomingCallReceiver.unregister();
            }
            incomingCallReceiver = null;
            Log.d(TAG, "Incoming call receiver unregistered");
        }
    }

    private void changeRingerLevel(float ringerLevel) {
        Log.d(TAG, "Ringer " + String.valueOf(ringerLevel));
        AudioManager audioManager = context.getAudioService();
        //setRingerMode does not function when the phone is ringing.
        if (context.isDeviceRinging()) {
            audioManager.setStreamVolume(AudioManager.STREAM_RING, Math.round(ringerLevel / 100 * audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)), AudioManager.FLAG_ALLOW_RINGER_MODES);
        } else {
            if (ringerLevel == 0) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, Math.round(ringerLevel / 100 * audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)), AudioManager.FLAG_ALLOW_RINGER_MODES);
            }
        }
    }
}
