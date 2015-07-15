package com.jjz.tsaca.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Station.
 */
@Entity
@Table(name = "STATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Station implements Serializable {

	private static final long serialVersionUID = -7573908425864200985L;

	@Transient
	private transient List<ArrivalDeparture> arrivals;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "output_slots")
    private Integer outputSlots;

	@ManyToMany
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@JoinTable(name = "STATION_ROUTE", joinColumns = @JoinColumn(name = "stations_id", referencedColumnName = "ID") , inverseJoinColumns = @JoinColumn(name = "routes_id", referencedColumnName = "ID") )
	private Set<Route> routes = new HashSet<>();

    @Column(name = "stop_id")
    private String stopId;

    @Column(name = "travel_time_from_home_to_station_in_seconds")
    private Long travelTimeFromHomeToStationInSeconds;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Station station = (Station) o;

		if (!Objects.equals(id, station.id))
			return false;

		return true;
	}

	public List<ArrivalDeparture> getArrivals() {
		return arrivals;
	}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getOutputSlots() {
        return outputSlots;
    }

	public Set<Route> getRoutes() {
		return routes;
    }

    public String getStopId() {
        return stopId;
    }

	public Long getTravelTimeFromHomeToStationInSeconds() {
		return travelTimeFromHomeToStationInSeconds;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
    }

	public void setArrivals(List<ArrivalDeparture> arrivals) {
		this.arrivals = arrivals;
	}

	public void setId(Long id) {
		this.id = id;
    }

	public void setName(String name) {
		this.name = name;
    }

	public void setOutputSlots(Integer outputSlots) {
		this.outputSlots = outputSlots;
    }

    public void setRoutes(Set<Route> routes) {
        this.routes = routes;
    }

	public void setStopId(String stopId) {
		this.stopId = stopId;
    }

	public void setTravelTimeFromHomeToStationInSeconds(Long travelTimeFromHomeToStationInSeconds) {
		this.travelTimeFromHomeToStationInSeconds = travelTimeFromHomeToStationInSeconds;
    }

	@Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", outputSlots='" + outputSlots + "'" +
                ", stopId='" + stopId + "'" +
                ", travelTimeFromHomeToStationInSeconds='" + travelTimeFromHomeToStationInSeconds + "'" +
                '}';
    }
}
