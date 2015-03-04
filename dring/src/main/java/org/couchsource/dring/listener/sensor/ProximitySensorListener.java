package org.couchsource.dring.listener.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import org.couchsource.dring.application.AppContextWrapper;
import org.couchsource.dring.service.DeviceStateListener;

/**
 * Listener for the proximity sensor
 * @author Kunal Sanghavi
 */
public class ProximitySensorListener extends DeviceSensorEventListener {

    /**
     * Instantiates a proximity sensor listener
     */
    public ProximitySensorListener(AppContextWrapper contextWrapper, DeviceStateListener deviceStateListener){
        super(contextWrapper,deviceStateListener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            processEvent(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void processEvent(SensorEvent event) {
        if (event.values[0] == getMaxProximity()) {
            getDeviceStateListener().registerDistantProximity();
        } else {
            getDeviceStateListener().registerCloseProximity();
        }
    }

    public void register() {
        super.register(Sensor.TYPE_PROXIMITY);
    }

}
