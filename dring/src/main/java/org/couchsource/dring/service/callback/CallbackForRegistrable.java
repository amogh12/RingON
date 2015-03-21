package org.couchsource.dring.service.callback;

import org.couchsource.dring.application.Event;

/**
 * Interface to register incoming calls
 * author Kunal Sanghavi
 */
public interface CallbackForRegistrable extends SensorServiceCallback {

    public void signalEvent(Event event);

}
