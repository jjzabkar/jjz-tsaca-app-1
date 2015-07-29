package com.jjz.tsaca.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

	/*
	 * Array: PREFIX,led0,led1,led2,...,led24
	 * 
	 */
	public String fetchArduinoCsvData() {
		final Arrival arrival = getArrival();
		StringBuilder sbOutput = new StringBuilder();
		sbOutput.append(CSV_LINE_CONSTANT_PREFIX_STATION);
		sbOutput.append(COMMA);
		
		List<SortableColor> outputColors = new ArrayList<>(25);
		for (int i = 0; i < 25; i++) {
			outputColors.add(SortableColor.OFF);
		}

		Station station;
		Map<Integer, Integer> stationIndexOffsets = new TreeMap<>();
		stationIndexOffsets.put(0, 9);
		stationIndexOffsets.put(1, 16);
		stationIndexOffsets.put(2, 19);

		for (int sindex = 0; sindex < 3; sindex++) {
			station = arrival.getStations().get(sindex);
			int stationIndexOffset = (int) stationIndexOffsets.get(sindex);
			for (int i = 0; i < station.getOutputSlots(); i++) {
				int i2 = i + stationIndexOffset;
				SortableColor c = station.getArrivals().get(i).getColor();
				if (i2 < (outputColors.size() - 1)) {
					outputColors.set(i2, c);
					log.debug("   set outputColors[{}]={}", i2, c);
				} else {
					log.debug("NO set outputColors[{}]={}", i2, c);

				}
			}
		}

		// set spacers
		outputColors.set(8, SortableColor.PURPLE);
		outputColors.set(15, SortableColor.PURPLE);
		outputColors.set(18, SortableColor.PURPLE);

		for (SortableColor sc : outputColors) {
			sbOutput.append(sc.toHex()).append(COMMA);
		}
		sbOutput.append("\n");
		return sbOutput.toString();
	}

	public Arrival getArrival() {
		Arrival result = new Arrival();
		List<Station> stations = obaApiStationService.findAll();
		for (Station s : stations) {
			final Integer outputSlots = ObjectUtils.defaultIfNull(s.getOutputSlots(), new Integer(4));
			final String stopId = s.getStopId();
			result.addStation(s);
			s.setArrivals(new LinkedList<ArrivalDeparture>());
			List<ArrivalDeparture> aadListForStation = this.arrivalsPerStation.get(stopId);
			for (ArrivalDeparture aad : aadListForStation) {
				final SortableColor color = timeCalculationService.getColor(aad, s);
				final Date myEstimatedBoardableTime = timeCalculationService.getEstimatedBoardableTime(aad, s);
				aad.setColor(color);
				aad.setMyEstimatedBoardableTime(myEstimatedBoardableTime);
			}
			aadListForStation.sort(sortByColorThenBoardableTimeComparator);

			for (int i = 0; i < outputSlots; i++) {
				if (i < aadListForStation.size()) {
					final ArrivalDeparture aad = aadListForStation.get(i);
					final SortableColor color = aad.getColor();
					final Date myEstimatedBoardableTime = aad.getMyEstimatedBoardableTime();
					log.debug("color={}\t myEstimatedBoardableTime={}\t routeId={}\t stopId={}", color, myEstimatedBoardableTime,
							aad.getRouteId(), s.getStopId());
					log.debug("\tStop  Page: {}", obaApiStationService.getUrlForStopId(stopId));
					s.getArrivals().add(aad);
				} else {
					ArrivalDeparture newAad = new ArrivalDeparture();
					newAad.setColor(SortableColor.OFF);
					s.getArrivals().add(newAad);
				}
			}
		}
		return result;
	}

	public static Comparator<ArrivalDeparture> sortByColorThenBoardableTimeComparator = new Comparator<ArrivalDeparture>() {
		@Override
		public int compare(ArrivalDeparture o1, ArrivalDeparture o2) {
			return ComparisonChain.start() //
					.compare(SortableColor.valueOf(StringUtils.upperCase(o1.getColor().toString())), //
							SortableColor.valueOf(StringUtils.upperCase(o2.getColor().toString()))) //
					.compare(o1.getMyEstimatedBoardableTime(), o2.getMyEstimatedBoardableTime(), Ordering.natural().nullsLast())//
					.result();
		}
	};

}
