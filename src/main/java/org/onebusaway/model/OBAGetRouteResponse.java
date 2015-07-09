package org.onebusaway.model;

/**
 * Simple deserialization wrapper for OBA API responses.
 * 
 * @author jjzabkar
 */
public class OBAGetRouteResponse {
	private OBAData data;
	private String text;

	public OBAData getData() {
		return data;
	}

	public void setData(OBAData data) {
		this.data = data;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
