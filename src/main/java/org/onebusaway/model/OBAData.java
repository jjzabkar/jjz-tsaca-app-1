package org.onebusaway.model;

import org.onebusaway.gtfs.model.Route;

/**
 * Simple deserialization wrapper for OBA API responses.
 * 
 * @author jjzabkar
 */
public class OBAData {
	private Route entry;

	public Route getEntry() {
		return entry;
	}

	public void setRoute(Route entry) {
		this.entry = entry;
	}
}
