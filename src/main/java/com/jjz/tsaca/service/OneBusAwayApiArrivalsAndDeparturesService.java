package com.jjz.tsaca.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.onebusaway.model.OBAGetArrivalsAndDeparturesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jjz.tsaca.domain.Route;
import com.jjz.tsaca.domain.Station;

import fr.dudie.onebusaway.model.ArrivalAndDeparture;

@Service
@ConfigurationProperties(prefix = "onebusaway.arrivalsAndDeparturesService", ignoreUnknownFields = false)
public class OneBusAwayApiArrivalsAndDeparturesService {

	private static final String COMMA = ",";

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private OneBusAwayApiService obaApiService;
	@Inject
	private OneBusAwayApiRouteService obaApiRouteService;
	@Inject
	private OneBusAwayApiStopService obaApiStationService;
	private String uri;

	private ConcurrentHashMap<String, ArrivalAndDeparture> arrivalsMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, List<ArrivalAndDeparture>> arrivalsPerStation = new ConcurrentHashMap<>();

	public String getUri() {
		return uri;
	}

	/**
	 * @see YAML property {@code onebusaway.arrivalsAndDeparturesService.uri}
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	public List<ArrivalAndDeparture> fetchForStopId(String stopId) {
		@SuppressWarnings("unchecked")
		List<ArrivalAndDeparture> result = Collections.EMPTY_LIST;
		Map<String, Object> myMap = new HashMap<>();
		myMap.put("stopId", stopId);

		log.debug("fetch for stopId={}", stopId);
		OBAGetArrivalsAndDeparturesResponse response = obaApiService.getForObject(this.uri, OBAGetArrivalsAndDeparturesResponse.class,
				myMap);
		if (response != null && response.getData() != null && response.getData().getEntry() != null) {
			result = response.getData().getEntry().getArrivalsAndDepartures();
			for (ArrivalAndDeparture ad : result) {
				log.debug("stopId={},\t routeId={},\t ad={}", ad.getStopId(), ad.getRouteId(), ad);
			}
		}
		return result;
	}

	@Scheduled(fixedDelay = 60000L)
	public void fixedDelay() {
		log.info("fixedDelay()");
		List<Station> stations = obaApiStationService.findAll();
		Map<String,Route> routeIdMap = obaApiRouteService.findAllMap();
		Map<String, ArrivalAndDeparture> newAads = new HashMap<>();
		Map<String, List<ArrivalAndDeparture>> newArrivalsPerStation = new HashMap<>();

		for (Station station : stations){
			final String stopId = station.getStopId();
			log.info("stopId={},\t name='{}'", stopId, station.getName());
			log.info("routeIdMap={}", routeIdMap.keySet());
			List<ArrivalAndDeparture> aadListForStation = fetchForStopId(stopId);
			newArrivalsPerStation.put(stopId, aadListForStation);
			for (ArrivalAndDeparture aad : aadListForStation) {
				final String routeId = aad.getRouteId();
				if(routeIdMap.containsKey(routeId)){
					Route r = routeIdMap.get(routeId);
					log.info("Found: aaD.routeId={},\t name='{}',\t route={}", aad.getRouteLongName(), routeId, r);
					newAads.put(aad.getTripId(), aad);
				}
			}
		}
		synchronized (this.arrivalsMap) {
			synchronized (this.arrivalsPerStation) {
				this.arrivalsMap.clear();
				this.arrivalsMap.putAll(newAads);
				this.arrivalsPerStation.clear();
				this.arrivalsPerStation.putAll(newArrivalsPerStation);
			}
		}
	}

	// TODO: Do the Map or AAD need to be immutable?
	public Map<String, ArrivalAndDeparture> getArrivalsMap() {
		return arrivalsMap;
	}

	public String fetchArduinoCsvData() {
		StringBuilder output = new StringBuilder();
		List<Station> stations = obaApiStationService.findAll();
		for (Station s : stations) {
			final Integer outputSlots = ObjectUtils.defaultIfNull(s.getOutputSlots(), new Integer(4));
			final String stopId = s.getStopId();
			output.append(stopId);
			List<ArrivalAndDeparture> aadListForStation = this.arrivalsPerStation.get(stopId);
			for (int i = 0; i < outputSlots; i++) {
				if (i < aadListForStation.size()) {
					output.append(COMMA);
					output.append(aadListForStation.get(i).getTripId());
				} else {
					output.append(COMMA).append("black");
				}
			}
			output.append("\n");
		}

		return output.toString();
	}

}
