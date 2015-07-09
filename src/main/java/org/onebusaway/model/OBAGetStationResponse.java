package org.onebusaway.model;

import org.onebusaway.gtfs.model.Stop;

/**
 * Simple deserialization wrapper for OBA API responses.
 * 
 * @author jjzabkar
 */
public class OBAGetStationResponse {

	private OBAGetStationResponseData data;
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public OBAGetStationResponseData getData() {
		return data;
	}

	public void setData(OBAGetStationResponseData data) {
		this.data = data;
	}

	public class OBAGetStationResponseData {
		private Stop entry;

		public Stop getEntry() {
			return entry;
		}

		public void setEntry(Stop entry) {
			this.entry = entry;
		}
	}
}
