package org.onebusaway.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jjz.tsaca.domain.ArrivalDeparture;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OBAGAADEntry {

	@JsonProperty(value = "arrivalsAndDepartures")
	@JsonDeserialize(contentAs = ArrivalDeparture.class)
	private ArrayList<ArrivalDeparture> arrivalsAndDepartures;

	private String stopId;

	public OBAGAADEntry() {
	}

	public ArrayList<ArrivalDeparture> getArrivalsAndDepartures() {
		return arrivalsAndDepartures;
	}

	public String getStopId() {
		return stopId;
	}

	public void setArrivalsAndDepartures(ArrayList<ArrivalDeparture> arrivalsAndDepartures) {
		this.arrivalsAndDepartures = arrivalsAndDepartures;
	}

	@JsonSetter("stopId")
	public void setStopId(String stopId) {
		this.stopId = stopId;
	}

}
