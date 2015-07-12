package com.jjz.tsaca.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.jjz.tsaca.domain.Route;
import com.jjz.tsaca.domain.Station;

@Service
@Profile({ "dev" })
public class DevProfileTestDataPopulationService {

	@Inject
	private OneBusAwayApiRouteService routeService;
	@Inject
	private OneBusAwayApiStopService stopService;

	@PostConstruct
	public void postConstruct() {
		String[] routeIds = { //
				"1_102576", // C-line, for busy stop test (3rd & Pike)
				"1_100019", // 120, for busy stop test (3rd & Pike)
				"1_102615", // E-line
				"1_100175", // 301
				"29_413", // 413
				"29_435", // 435
				"40_SNDR_N" //
				};
		for (String routeId : routeIds) {
			List<Route> existingRoutes = routeService.findAll();
			boolean found = false;
			for (Route r : existingRoutes) {
				if (routeId.equals(r.getRouteId())) {
					found = true;
				}
			}
			if (!found) {
				Route r = new Route();
				r.setRouteId(routeId);
				routeService.save(r);
			}
		}
		String[] stopIds = { //
				"1_431", // 3rd & Pike (a busy stop)
				"1_75730", // Shoreline P&R
				"29_2765", // Mountlake Terrace P&R
				"40_S_ED" // Edmonds Station
				};
		for (String stopId : stopIds) {
			if (!stopService.findAllMap().containsKey(stopId)) {
				Station s = new Station();
				s.setStopId(stopId);
				s.setOutputSlots(8);
				s.setTravelTimeFromHomeToStationInSeconds(7L * 60L);
				stopService.save(s);
			}
		}

	}
}
