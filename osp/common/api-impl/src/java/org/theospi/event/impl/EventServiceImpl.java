package org.theospi.event.impl;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.cover.EventTrackingService;
import org.theospi.event.EventService;

public class EventServiceImpl implements EventService {

	public void postEvent(String message, String objectReference) {
		Event event = EventTrackingService.newEvent(message, objectReference, true);
		EventTrackingService.post(event);
	}

}
