package com.jjz.tsaca.domain;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Arrival {

	private List<Station> stations = new LinkedList<Station>();
	private Date asOf = new Date();

	public void addStation(Station s) {
		this.stations.add(s);
	}

	public List<Station> getStations() {
		return stations;
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}

	public Date getAsOf() {
		return asOf;
	}

	public void setAsOf(Date asOf) {
		this.asOf = asOf;
	}

}
