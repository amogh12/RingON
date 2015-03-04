package org.couchsource.dring.listener.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.couchsource.dring.application.AppContextWrapper;
import org.couchsource.dring.service.DeviceStateListener;

/**
 * author Kunal
 */
public abstract class DeviceSensorEventListener implements SensorEventListener{

    private final AppContextWrapper contextWrapper;
    private SensorManager mSensorManager;
    private final DeviceStateListener deviceStateListener;


    protected DeviceSensorEventListener(AppContextWrapper contextWrapper, DeviceStateListener deviceStateListener){
        this.contextWrapper = contextWrapper;
        this.deviceStateListener = deviceStateListener;
    }

    protected void register(int sensorType){
        mSensorManager = contextWrapper.getSensorService();
        Sensor mSensor = mSensorManager.getDefaultSensor(sensorType);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister(){
        mSensorManager = contextWrapper.getSensorService();
        mSensorManager.unregisterListener(this);
    }

    protected DeviceStateListener getDeviceStateListener() {
        return deviceStateListener;
    }

    protected float getMaxProximity(){
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        return mSensor.getMaximumRange();
    }

}
