package org.onebusaway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OBAGAADData {

	@JsonProperty("entry")
	private OBAGAADEntry entry;

	public OBAGAADData() {
	}

	public OBAGAADData(OBAGAADEntry entry) {
		this.entry = entry;
	}

	public OBAGAADEntry getEntry() {
		return entry;
	}

	@JsonSetter("entry")
	public void setEntry(OBAGAADEntry entry) {
		this.entry = entry;
	}

}
