package com.jjz.tsaca.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.ObjectUtils;
import org.onebusaway.model.OBAGetRouteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjz.tsaca.domain.Route;
import com.jjz.tsaca.repository.RouteRepository;

@Service
@Transactional
public class OneBusAwayApiRouteService implements EnvironmentAware {

	private final Logger log = LoggerFactory.getLogger(OneBusAwayApiRouteService.class);

	@Inject
	private OneBusAwayApiService obaApiService;

	@Inject
	private RouteRepository routeRepository;
	private String uri;

	/** @see YAML property {@code onebusaway.routeService.routeId} */
	@Override
	public void setEnvironment(Environment env) {
		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "onebusaway.routeService.");
		this.uri = propertyResolver.getProperty("uri");
		log.info("onebusaway.routeService.routeId={}", this.uri);
	}

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
