package com.jjz.tsaca.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A Station.
 */
@Entity
@Table(name = "STATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Station implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "output_slots")
    private Integer outputSlots;

    @Column(name = "stop_id")
    private String stopId;

    @Column(name = "travel_time_from_home_to_station_in_seconds")
    private Long travelTimeFromHomeToStationInSeconds;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "STATION_ROUTE",
               joinColumns = @JoinColumn(name="stations_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="routes_id", referencedColumnName="ID"))
    private Set<Route> routes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOutputSlots() {
        return outputSlots;
    }

    public void setOutputSlots(Integer outputSlots) {
        this.outputSlots = outputSlots;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public Long getTravelTimeFromHomeToStationInSeconds() {
        return travelTimeFromHomeToStationInSeconds;
    }

    public void setTravelTimeFromHomeToStationInSeconds(Long travelTimeFromHomeToStationInSeconds) {
        this.travelTimeFromHomeToStationInSeconds = travelTimeFromHomeToStationInSeconds;
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(Set<Route> routes) {
        this.routes = routes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Station station = (Station) o;

        if ( ! Objects.equals(id, station.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
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
