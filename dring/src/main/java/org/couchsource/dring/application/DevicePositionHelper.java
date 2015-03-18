package org.couchsource.dring.application;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to get the right labels for all device positions
 * @author Kunal Sanghavi
 */
public class DevicePositionHelper {

    private static final Map<String, Integer> statusToResourceMap = new HashMap<>();
    static{
        statusToResourceMap.put(DevicePosition.FACE_UP.getLabel(), R.string.FACE_UP_Label);
        statusToResourceMap.put(DevicePosition.FACE_DOWN.getLabel(), R.string.FACE_DOWN_Label);
        statusToResourceMap.put(DevicePosition.IN_POCKET.getLabel(), R.string.INPOCKET_Label);
    }

    public static int getResId(String devicePosition){
        return statusToResourceMap.get(devicePosition);
    }
}
