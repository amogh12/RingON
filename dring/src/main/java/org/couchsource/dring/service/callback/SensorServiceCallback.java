package org.couchsource.dring.service.callback;

import org.couchsource.dring.application.ApplicationContextWrapper;

/**
 * Base interface for all SensorService callbacks
 * author Kunal Sanghavi
 */
public interface SensorServiceCallback {
    /**
     * Gets the context
     * @return {@link org.couchsource.dring.application.ApplicationContextWrapper}
     */
    public ApplicationContextWrapper getContext();

}
