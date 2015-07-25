package com.jjz.tsaca.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.onebusaway.model.OBAGetArrivalsAndDeparturesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.jjz.tsaca.domain.Arrival;
import com.jjz.tsaca.domain.ArrivalDeparture;
import com.jjz.tsaca.domain.Route;
import com.jjz.tsaca.domain.SortableColor;
import com.jjz.tsaca.domain.Station;

@Service
@ConfigurationProperties(prefix = "onebusaway.arrivalsAndDeparturesService", ignoreUnknownFields = false)
public class OneBusAwayApiArrivalsAndDeparturesService {

	public static final String CSV_LINE_CONSTANT_PREFIX_STATION = "STATION";

	private static final String COMMA = ",";

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private OneBusAwayApiService obaApiService;
	@Inject
	private OneBusAwayApiRouteService obaApiRouteService;
	@Inject
	private OneBusAwayApiStopService obaApiStationService;
	@Inject
	private TimeCalculationService timeCalculationService;
	private String uri;

	private ConcurrentHashMap<String, ArrivalDeparture> arrivalsMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, List<ArrivalDeparture>> arrivalsPerStation = new ConcurrentHashMap<>();

	public String getUri() {
		return uri;
	}

	/**
	 * @see YAML property {@code onebusaway.arrivalsAndDeparturesService.uri}
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	public List<ArrivalDeparture> fetchForStopId(String stopId) {
		@SuppressWarnings("unchecked")
		List<ArrivalDeparture> result = Collections.EMPTY_LIST;
		Map<String, Object> myMap = new HashMap<>();
		myMap.put("stopId", stopId);

		log.debug("fetch for stopId={}", stopId);
		OBAGetArrivalsAndDeparturesResponse response = obaApiService.getForObject(this.uri, OBAGetArrivalsAndDeparturesResponse.class,
				myMap);
		if (response != null && response.getData() != null && response.getData().getEntry() != null) {
			result = response.getData().getEntry().getArrivalsAndDepartures();
			for (ArrivalDeparture ad : result) {
				log.debug("stopId={},\t routeId={},\t ad={}", ad.getStopId(), ad.getRouteId(), ad);
			}
		}
		return result;
	}

	@Scheduled(fixedDelay = 60000L)
	public void fixedDelay() {
		log.info("fixedDelay()");
		List<Station> stations = obaApiStationService.findAll();
		List<Route> routeList = obaApiRouteService.findAll();
		Map<String, ArrivalDeparture> newAads = new HashMap<>();
		Map<String, List<ArrivalDeparture>> newArrivalsPerStation = new HashMap<>();

		for (Station station : stations) {
			final String stopId = station.getStopId();
			log.info("stopId={},\t name='{}'", stopId, station.getName());
			List<ArrivalDeparture> aadListForStation = fetchForStopId(stopId);
			newArrivalsPerStation.put(stopId, aadListForStation);
			for (ArrivalDeparture aad : aadListForStation) {
				final String routeId = aad.getRouteId();
				for (Route r : routeList) {
					if (routeId.equals(r.getRouteId())) {
						log.info("Found: aaD.routeId={},\t name='{}',\t route={}", aad.getRouteLongName(), routeId, r);
						newAads.put(aad.getTripId(), aad);
					}
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
	public Map<String, ArrivalDeparture> getArrivalsMap() {
		return arrivalsMap;
	}

	public String fetchArduinoCsvData() {
		StringBuilder output = new StringBuilder();
		buildFetchedData(null, output);
		return output.toString();
	}

	public Arrival getArrival() {
		Arrival result = new Arrival();
		buildFetchedData(result, null);
		return result;
	}

	private void buildFetchedData(final Arrival arrivalOutput, final StringBuilder sbOutput) {
		List<Station> stations = obaApiStationService.findAll();
		for (Station s : stations) {
			final Integer outputSlots = ObjectUtils.defaultIfNull(s.getOutputSlots(), new Integer(4));
			final String stopId = s.getStopId();
			if (sbOutput != null) {
				sbOutput.append(CSV_LINE_CONSTANT_PREFIX_STATION);
				sbOutput.append(COMMA);
				sbOutput.append(stopId);
				sbOutput.append(COMMA);
			} else if (arrivalOutput != null) {
				arrivalOutput.addStation(s);
				s.setArrivals(new LinkedList<ArrivalDeparture>());
			}
			List<ArrivalDeparture> aadListForStation = this.arrivalsPerStation.get(stopId);
			for (ArrivalDeparture aad : aadListForStation) {
				final String color = timeCalculationService.getColor(aad, s);
				final Date myEstimatedBoardableTime = timeCalculationService.getEstimatedBoardableTime(aad, s);
				aad.setColor(color);
				aad.setMyEstimatedBoardableTime(myEstimatedBoardableTime);
			}
			aadListForStation.sort(sortByColorThenBoardableTimeComparator);

			for (int i = 0; i < outputSlots; i++) {
				if (i < aadListForStation.size()) {
					final ArrivalDeparture aad = aadListForStation.get(i);
					final String color = aad.getColor();
					final Date myEstimatedBoardableTime = aad.getMyEstimatedBoardableTime();
					log.debug("color={}\t myEstimatedBoardableTime={}\t routeId={}\t stopId={}", color, myEstimatedBoardableTime,
							aad.getRouteId(), s.getStopId());
					log.debug("\tStop  Page: {}", obaApiStationService.getUrlForStopId(stopId));
					if (sbOutput != null) {
						sbOutput.append(color);
						sbOutput.append(COMMA);
					} else if (arrivalOutput != null) {
						s.getArrivals().add(aad);
					}
				} else {
					if (sbOutput != null) {
						sbOutput.append(COMMA).append("off");
					} else if (arrivalOutput != null) {
						ArrivalDeparture newAad = new ArrivalDeparture();
						newAad.setColor("off");
						s.getArrivals().add(newAad);
					}
				}
			}
			if (sbOutput != null) {
				sbOutput.append("\n");
			}
		}
	}

	public static Comparator<ArrivalDeparture> sortByColorThenBoardableTimeComparator = new Comparator<ArrivalDeparture>() {
		@Override
		public int compare(ArrivalDeparture o1, ArrivalDeparture o2) {
			return ComparisonChain.start() //
					.compare(SortableColor.valueOf(StringUtils.upperCase(o1.getColor())), //
							SortableColor.valueOf(StringUtils.upperCase(o2.getColor()))) //
					.compare(o1.getMyEstimatedBoardableTime(), o2.getMyEstimatedBoardableTime(), Ordering.natural().nullsLast())//
					.result();
		}
	};

}
