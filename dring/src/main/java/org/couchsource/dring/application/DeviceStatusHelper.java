package org.couchsource.dring.application;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kunal on 1/4/2015.
 */
public class DeviceStatusHelper {

    private static final Map<String, Integer> statusToResourceMap = new HashMap<>();
    static{
        statusToResourceMap.put(DeviceStatus.FACE_UP.name(), R.string.FACE_UP_Label);
        statusToResourceMap.put(DeviceStatus.FACE_DOWN.name(), R.string.FACE_DOWN_Label);
        statusToResourceMap.put(DeviceStatus.IN_POCKET.name(), R.string.INPOCKET_Label);
    }

    public static int getResId(DeviceStatus deviceStatus){
        return statusToResourceMap.get(deviceStatus.name());
    }
}
