package org.couchsource.dring.listener.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import org.couchsource.dring.service.SensorEventsAggregator;

/**
 * Listener for the proximity sensor on a device
 * @author Kunal Sanghavi
 */
public class ProximitySensorListener extends DeviceSensorEventListener {

    /**
     * Instantiates a proximity sensor listener
     * @param sensorEventsAggregator {@link org.couchsource.dring.service.SensorEventsAggregator}
     */
    public ProximitySensorListener(SensorEventsAggregator sensorEventsAggregator){
        super(sensorEventsAggregator);
    }

    @Override
    public void register() {
        super.register(Sensor.TYPE_PROXIMITY);
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
            getSensorEventsAggregator().registerDistantProximity();
        } else {
            getSensorEventsAggregator().registerCloseProximity();
        }
    }
}
