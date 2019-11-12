package application.events;

import io.cloudevents.CloudEvent;

/**
 * An interface for event handlers
 */
public interface EventHandler {
    
    void eventReceived(CloudEvent<?, ?> evt) throws Exception;

}
