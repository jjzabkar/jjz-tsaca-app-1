package com.jjz.tsaca.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.onebusaway.model.OBAGetArrivalsAndDeparturesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OneBusAwayApiArrivalsAndDeparturesService implements EnvironmentAware {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private OneBusAwayApiService obaApiService;
	private String uri;

	/** @see YAML property {@code onebusaway.routeService.routeId} */
	@Override
	public void setEnvironment(Environment env) {
		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "onebusaway.arrivalsAndDeparturesService.");
		this.uri = propertyResolver.getProperty("uri");
		log.info("onebusaway.arrivalsAndDeparturesService.uri={}", this.uri);
	}

	public void fetch(String stopId) {
		Map<String, Object> myMap = new HashMap<>();
		myMap.put("stopId", stopId);

		log.debug("fetch for stopId={}", stopId);
		OBAGetArrivalsAndDeparturesResponse response = obaApiService.getForObject(this.uri, OBAGetArrivalsAndDeparturesResponse.class,
				myMap);
		log.info("response={}", response);
		log.info("response={}", response);

	}

	// @Scheduled(fixedDelay = 60000L)
	public void fixedDelay() {
		fetch("1_75730"); // verified; works fine
	}

}
