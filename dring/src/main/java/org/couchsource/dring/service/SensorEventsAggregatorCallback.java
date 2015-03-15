package org.couchsource.dring.service;

import org.couchsource.dring.application.ApplicationContextWrapper;
import org.couchsource.dring.application.DevicePosition;

/**
 * Callback interface for {@link org.couchsource.dring.service.SensorService}
 * <p/>
 * author Kunal Sanghavi
 */
public interface SensorEventsAggregatorCallback {


    /**
     * Signals new device position
     * @param devicePosition new position of the device
     */
    public void signalNewPosition(DevicePosition devicePosition);


    /**
     * Signals if device proximity sensor registers any change in proximity.
     */
    public void signalDeviceProximityChanged();

    /**
     * Gets the context
     * @return {@link org.couchsource.dring.application.ApplicationContextWrapper}
     */
    public ApplicationContextWrapper getContext();

}
