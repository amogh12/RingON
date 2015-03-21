package org.couchsource.dring.service.callback;

import org.couchsource.dring.application.DevicePosition;

/**
 * Callback interface for {@link org.couchsource.dring.service.SensorService}
 * <p/>
 * author Kunal Sanghavi
 */
public interface CallbackForSensorEventsAggregator extends SensorServiceCallback {


    /**
     * Signals new device position
     *
     * @param devicePosition new position of the device
     */
    public void signalNewPosition(DevicePosition devicePosition);


    /**
     * Signals if device proximity sensor registers any change in proximity.
     */
    public void signalDeviceProximityChanged();


}
