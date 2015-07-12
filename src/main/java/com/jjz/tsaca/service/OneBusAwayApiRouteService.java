package com.jjz.tsaca.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.ObjectUtils;
import org.onebusaway.model.OBAGetRouteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjz.tsaca.config.Constants;
import com.jjz.tsaca.domain.Route;
import com.jjz.tsaca.repository.RouteRepository;

@Service
@Transactional
@ConfigurationProperties(prefix = "onebusaway.routeService", ignoreUnknownFields = false)
public class OneBusAwayApiRouteService {

	private final Logger log = LoggerFactory.getLogger(OneBusAwayApiRouteService.class);

	@Inject
	private OneBusAwayApiService obaApiService;

	@Inject
	private RouteRepository routeRepository;
	private String uri;

	public String getUri() {
		return uri;
	}

	/**
	 * @see YAML property {@code onebusaway.routeService.uri}
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	@Cacheable(value = Constants.OBA_ROUTE_SERVICE_CACHE_NAME, key = "'findAll'")
	public List<Route> findAll() {
		return routeRepository.findAll();
	}
	
	@Cacheable(value = Constants.OBA_ROUTE_SERVICE_CACHE_NAME, key = "'findAllMap'")
	public Map<String, Route> findAllMap() {
		List<Route> routes = findAll();
		// example: http://stackoverflow.com/a/20363874/237225
		return routes.stream().collect(Collectors.toConcurrentMap(Route::getRouteId, Function.identity()));
	}

	@CacheEvict(Constants.OBA_ROUTE_SERVICE_CACHE_NAME)
	public Route save(Route route) {
		Map<String, Object> myMap = new HashMap<>();
		myMap.put("routeId", route.getRouteId());

		OBAGetRouteResponse response = obaApiService.getForObject(this.uri, OBAGetRouteResponse.class, myMap);

		if (response != null && response.getData() != null) {
			org.onebusaway.gtfs.model.Route obaRoute = response.getData().getEntry();
			if (obaRoute != null) {
				route.setLongName((String) ObjectUtils.defaultIfNull(obaRoute.getLongName(), obaRoute.getShortName()));
				route.setShortName(obaRoute.getShortName());
			}
		}

		return routeRepository.save(route);
	}
}
