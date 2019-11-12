package application.events;

import io.cloudevents.CloudEvent;

/**
 * An interface for event handlers
 */
public interface EventHandler {
    
    /**
     * Called by the framework when a <code>CloudEvent</code> is received.
     * 
     * @param evt A cloud event instance
     * 
     * @throws Exception
     */
    void eventReceived(CloudEvent<?, ?> evt) throws Exception;

}
