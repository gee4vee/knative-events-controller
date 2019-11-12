package application.events;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.cloudevents.CloudEvent;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.http.Unmarshallers;

/*
 * Best practice is to use a version identifier in the request mapping, e.g. /v1. however, Knative Eventing does not yet
 * support routing to a custom URL path. When this is implemented, the request mapping URL can change.
 * https://github.com/knative/eventing/issues/1918
 */

/**
 * REST controller for events.
 */
@RestController
public class EventController {
    
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    
    @Autowired
    private EventHandlerMgr evtHandlerMgr;

    public EventController() {
    }

    @GetMapping(value = "/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> landing() {
        // customize landing page response here
        return ResponseEntity.ok().build();
    }

    /**
     * Receives an event and passes it to registered event handlers. When Knative supports routing to a custom URL path, the value here should change to something like /event.
     *
     * @param headers The request headers.
     * @param body    The request body.
     * 
     * @return 202 if the event is successfully stored
     * @throws Exception 
     */
    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> event(@RequestHeader Map<String, Object> headers, @RequestBody String body) throws Exception {
        logger.debug("Receved request headers: " + headers);
        logger.debug("Received request body: " + body);
        try {
            Object contentTypeVal = headers.get("Content-Type");
            if (contentTypeVal == null || MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentTypeVal.toString())) {
                @SuppressWarnings("rawtypes")
                CloudEvent<AttributesImpl, Map> cloudEvent = Unmarshallers.binary(Map.class)
                        .withHeaders(() -> headers)
                        .withPayload(() -> body)
                        .unmarshal();
                logger.debug("Received CloudEvent: " + cloudEvent);
                this.evtHandlerMgr.handleEvent(cloudEvent);
            }
            return ResponseEntity.accepted().build();
            
        } catch (Exception e) {
            String errMsg = "ERROR: Exception processing received event";
            logger.error(errMsg, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errMsg, e);
        }
    }

}
