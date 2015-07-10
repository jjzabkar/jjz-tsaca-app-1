package com.jjz.tsaca.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.model.OBAGetStationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjz.tsaca.config.Constants;
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

	@Cacheable(value = Constants.OBA_STOP_SERVICE_CACHENAME, key = "'findAll'")
	public List<Station> findAll() {
		return stationRepository.findAll();
	}

	@Cacheable(value = Constants.OBA_STOP_SERVICE_CACHENAME, key = "'findAllMap'")
	public Map<String, Station> findAllMap() {
		List<Station> stations = findAll();
		// example: http://stackoverflow.com/a/20363874/237225
		return stations.stream().collect(Collectors.toConcurrentMap(Station::getStopId, Function.identity()));
	}

	@CacheEvict(value = Constants.OBA_STOP_SERVICE_CACHENAME)
	public Station save(Station station) {
		Map<String, Object> myMap = new HashMap<>();
		myMap.put("stopId", station.getStopId());

		OBAGetStationResponse response = obaApiService.getForObject(this.uri, OBAGetStationResponse.class, myMap);

		if (response != null && response.getData() != null) {
			Stop obaStop = response.getData().getEntry();
			if (obaStop != null) {
				station.setName(obaStop.getName());
			}
		}

		return stationRepository.save(station);
	}

}