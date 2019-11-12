/**
 * 
 */
package application.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import io.cloudevents.CloudEvent;

/**
 * Manages registered event handlers.
 */
public class EventHandlerMgr {
    
    private static final Logger logger = Logger.getLogger(EventHandlerMgr.class.getName());
    
    private static EventHandlerMgr instance;
    
    private final List<EventHandler> handlers;
    
    private EventHandlerMgr() {
        this.handlers = new ArrayList<>();
    }
    
    public static EventHandlerMgr getInstance() {
        if (instance == null) {
            synchronized (EventHandlerMgr.class) {
                if (instance == null) {
                    instance = new EventHandlerMgr();
                }
            }
        }
        
        return instance;
    }
    
    public synchronized List<EventHandler> getRegisteredHandlers() {
        return Collections.unmodifiableList(this.handlers);
    }
    
    public synchronized void registerHandler(EventHandler handler) {
        this.handlers.add(handler);
    }
    
    public synchronized void unregisterHandler(EventHandler handler) {
        this.handlers.remove(handler);
    }
    
    public synchronized void handleEvent(CloudEvent<?, ?> evt) throws Exception {
        for (EventHandler evtHandler : this.handlers) {
            logger.fine("Invoking event handler " + evtHandler + " for cloud event " + evt);
            evtHandler.eventReceived(evt);
        }
    }

}
