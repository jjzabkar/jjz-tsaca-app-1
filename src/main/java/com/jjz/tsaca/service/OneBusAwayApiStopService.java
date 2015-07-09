package com.jjz.tsaca.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.model.OBAGetStationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjz.tsaca.domain.Station;
import com.jjz.tsaca.repository.StationRepository;

@Service
@Transactional
public class OneBusAwayApiStopService implements EnvironmentAware {

	private final Logger log = LoggerFactory.getLogger(OneBusAwayApiStopService.class);

	@Inject
	private StationRepository stationRepository;

	@Inject
	private OneBusAwayApiService obaApiService;
	private String uri;

	/** @see YAML property {@code onebusaway.routeService.routeId} */
	@Override
	public void setEnvironment(Environment env) {
		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "onebusaway.stopService.");
		this.uri = propertyResolver.getProperty("uri");
		log.info("onebusaway.stopService.uri={}", this.uri);
	}

	public Station save(Station station) {
		Map<String, Object> myMap = new HashMap<>();
		myMap.put("stopId", station.getStopId());

		OBAGetStationResponse response = obaApiService.getForObject(this.uri, OBAGetStationResponse.class, myMap);

		if (response != null && response.getData() != null) {
			Stop obaStop = response.getData().getEntry();
			if (obaStop != null) {
				station.setName(obaStop.getName());
				if (obaStop.getId() != null) {
					station.setStopId(obaStop.getId().getId());
				}
			}
		}

		return stationRepository.save(station);
	}

}