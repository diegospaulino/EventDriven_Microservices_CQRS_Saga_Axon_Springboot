package com.appsdeveloperblog.estore.ProductsService.command.rest;

import java.util.Optional;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class EventsReplayController {

    @Autowired
    private EventProcessingConfiguration eventProcessingConfiguration;

    @PostMapping("/eventProcessor/{processorName}/reset")
    public ResponseEntity<String> replayEvents(@PathVariable String processorName) {
        // This method is used to reset the event processor and replay events.
        // The actual implementation would depend on the Axon Framework's Event Processing API.
        // For example, you might use an EventProcessor instance to reset the processor.
        // Here, we are just providing a placeholder for the method.
        
        // Example:
        // eventProcessingConfiguration.eventProcessor(processorName).resetTokens();
        
        System.out.println("Replaying events for processor: " + processorName);

        Optional<TrackingEventProcessor> trackingEventProcessor = eventProcessingConfiguration.eventProcessor(processorName, TrackingEventProcessor.class);

        if(trackingEventProcessor.isPresent()) {
            TrackingEventProcessor eventProcessor = trackingEventProcessor.get();
            eventProcessor.shutDown();
            eventProcessor.resetTokens();
            eventProcessor.start();
            System.out.println("Events replay started for processor: " + processorName);

            return ResponseEntity.ok().body(String.format("The event processor with a name [%s] has been reset", processorName));
        } else {
            System.out.println("No TrackingEventProcessor found for processor: " + processorName);
            return ResponseEntity.badRequest().body(String.format("The event processor with a name [%s] is not a tracking event processor"
                            + " Only Tracking event processor is supported", processorName));
        }
    }
}
