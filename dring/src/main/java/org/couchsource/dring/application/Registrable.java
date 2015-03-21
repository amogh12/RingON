package org.couchsource.dring.application;

/**
 * Base interface for all Listeners/Receivers
 * that are Registrable with the Sensor Service
 *
 * author Kunal Sanghavi
 */
public interface Registrable {

    /**
     * register a Registrable
     */
    public void register();

    /**
     * unregister a Registrable
     */
    public void unregister();
}
