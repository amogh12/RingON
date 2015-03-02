package org.couchsource.dring.service;

import org.couchsource.dring.application.AppContextWrapper;
import org.couchsource.dring.application.DeviceStatus;

/**
 * author Kunal
 */
public interface DeviceStateListenerCallback {

  public void signalNewDevicePlacement(DeviceStatus deviceStatus);

  public void signalDeviceProximityChanged();

  public AppContextWrapper getContext();

}
