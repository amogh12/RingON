package org.couchsource.dring.service;

import org.couchsource.dring.application.AppContextWrapper;
import org.couchsource.dring.application.DevicePosition;

/**
 * author Kunal
 */
public interface DeviceStateListenerCallback {

  public void signalNewDevicePlacement(DevicePosition devicePosition);

  public void signalDeviceProximityChanged();

  public AppContextWrapper getContext();

}
