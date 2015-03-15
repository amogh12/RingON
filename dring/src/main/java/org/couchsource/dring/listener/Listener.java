package org.couchsource.dring.listener;

/**
 * An interface for all device listeners
 *
 * author Kunal Sanghavi
 */
public interface Listener {

    /**
     * register a Listener
     */
    public void register();

    /**
     * unregister a listener
     */
    public void unregister();
}
