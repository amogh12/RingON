package org.couchsource.dring.listener.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.couchsource.dring.application.ApplicationContextWrapper;
import org.couchsource.dring.listener.Listener;
import org.couchsource.dring.service.SensorEventsAggregator;

/**
 * Abstract SensorEventListener class for Accelerometer, Proximity and Light sensors.
 *
 * author Kunal Sanghavi
 */
public abstract class DeviceSensorEventListener implements SensorEventListener, Listener{

    private final ApplicationContextWrapper contextWrapper;
    private SensorManager mSensorManager;
    private final SensorEventsAggregator sensorEventsAggregator;


    protected DeviceSensorEventListener(SensorEventsAggregator sensorEventsAggregator){
        this.contextWrapper = sensorEventsAggregator.getContext();
        this.sensorEventsAggregator = sensorEventsAggregator;
    }

    /**
     * registers a Sensor Type
     * @param sensorType (Accelerometer, Light or Proximity Sensor)
     */
    protected void register(int sensorType){
        mSensorManager = contextWrapper.getSensorService();
        Sensor mSensor = mSensorManager.getDefaultSensor(sensorType);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void unregister(){
        mSensorManager = contextWrapper.getSensorService();
        mSensorManager.unregisterListener(this);
    }

    protected SensorEventsAggregator getSensorEventsAggregator() {
        return sensorEventsAggregator;
    }

    /**
     * Gets maximum proximity for a device. The value varies per device.
     * @return float value of max proximity range
     */
    protected float getMaxProximity(){
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        return mSensor.getMaximumRange();
    }

}
