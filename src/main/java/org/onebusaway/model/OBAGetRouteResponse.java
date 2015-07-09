package org.onebusaway.model;

import org.onebusaway.gtfs.model.Route;

/**
 * Simple deserialization wrapper for OBA API responses.
 * 
 * @author jjzabkar
 */
public class OBAGetRouteResponse {
	private OBAGetRouteResponseData data;
	private String text;

	public OBAGetRouteResponseData getData() {
		return data;
	}

	public void setData(OBAGetRouteResponseData data) {
		this.data = data;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public class OBAGetRouteResponseData {
		private Route entry;

		public Route getEntry() {
			return entry;
		}

		public void setRoute(Route entry) {
			this.entry = entry;
		}
	}

}
