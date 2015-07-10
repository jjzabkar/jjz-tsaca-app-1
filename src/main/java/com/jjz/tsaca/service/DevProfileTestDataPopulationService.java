package com.jjz.tsaca.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.jjz.tsaca.domain.Route;
import com.jjz.tsaca.domain.Station;

@Service
@Profile("dev")
public class DevProfileTestDataPopulationService {

	@Inject
	private OneBusAwayApiRouteService routeService;
	@Inject
	private OneBusAwayApiStopService stopService;
	@Inject
	private OneBusAwayApiArrivalsAndDeparturesService poll;

	@PostConstruct
	public void postConstruct() {
		String[] routeIds = { "1_100175", "29_413", "29_435" };
		for (String routeId : routeIds) {
			Route r = new Route();
			r.setRouteId(routeId);
			routeService.save(r);
		}
		String[] stopIds = { "29_2765", "1_75730" };
		for (String stopId : stopIds) {
			Station s = new Station();
			s.setStopId(stopId);
			stopService.save(s);
		}

		poll.fetch("29_2765");

	}
}
