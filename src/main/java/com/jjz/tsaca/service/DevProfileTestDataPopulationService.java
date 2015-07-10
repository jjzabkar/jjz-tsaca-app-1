package com.jjz.tsaca.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.jjz.tsaca.domain.Route;
import com.jjz.tsaca.domain.Station;

@Service
@Profile({ "dev", "prod" })
public class DevProfileTestDataPopulationService {

	@Inject
	private OneBusAwayApiRouteService routeService;
	@Inject
	private OneBusAwayApiStopService stopService;

	@PostConstruct
	public void postConstruct() {
		String[] routeIds = { //
				"1_102615", // E-line
				"1_100175", // 301
				"29_413", // 413
				"29_435", // 435
				"40_SNDR_N" //
				};
		for (String routeId : routeIds) {
			Route r = new Route();
			r.setRouteId(routeId);
			routeService.save(r);
		}
		String[] stopIds = { //
				"1_75730", // Shoreline P&R
				"29_2765", // Mountlake Terrace P&R
				"40_S_ED" // Edmonds Station
				};
		for (String stopId : stopIds) {
			Station s = new Station();
			s.setStopId(stopId);
			stopService.save(s);
		}


	}
}
