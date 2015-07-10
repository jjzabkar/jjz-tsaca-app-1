package org.onebusaway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OBAGetArrivalsAndDeparturesResponse {

	@JsonProperty("data")
	private OBAGAADData data;
	private String text;

	public OBAGetArrivalsAndDeparturesResponse() {
	}

	public OBAGetArrivalsAndDeparturesResponse(OBAGAADData data) {
		this.data = data;
	}

	public OBAGAADData getData() {
		return data;
	}

	public String getText() {
		return text;
	}

	@JsonSetter("data")
	public void setData(OBAGAADData data) {
		this.data = data;
	}

	public void setText(String text) {
		this.text = text;
	}

}
