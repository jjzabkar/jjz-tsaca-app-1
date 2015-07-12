package com.jjz.tsaca.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jjz.tsaca.domain.Station;

import fr.dudie.onebusaway.model.ArrivalAndDeparture;

@Service
public class TimeCalculationService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	// Keys kept in insertion order
	private LinkedHashMap<Long, String> colorMap;

	public TimeCalculationService() {
		colorMap = new LinkedHashMap<>();
		// AFTT= accounting for travel time

		// there will be an arrival in 7 minutes or less, AFTT.
		colorMap.put((60L * 7L), "green");

		// there will be an arrival in 7-to-15 minutes, AFTT.
		colorMap.put((60L * 15L), "yellow");

		// there will be an arrival in 15-to-30-minutes, AFTT.
		colorMap.put((60L * 30L), "red");

		// there will be an arrival in 30+ minutes, AFTT.
		colorMap.put(Long.MAX_VALUE, "off");
	}

	public String getColor(ArrivalAndDeparture aad, Station s) {

		log.trace("calculate time for route='{}'({})", aad.getRouteLongName(), aad.getRouteId());
		// long travelTimeFromHomeToStationInSeconds = s.getTravelTimeFromHomeToStationInSeconds();
		long travelTimeFromHomeToStationInMilliSeconds = s.getTravelTimeFromHomeToStationInSeconds() * 1000L;

		Date now = new Date();
		Date scheduledArrivalTime = aad.getScheduledArrivalTime();
		Date scheduledDepartureTime = aad.getScheduledDepartureTime();
		outputDate("now", now, now);
		outputDate("scheduledArrivalTime", scheduledArrivalTime, now);
		outputDate("scheduledDepartureTime", scheduledDepartureTime, now);

		// we care about scheduledDepartureTime: "Can i make it there in time?"

		// 1. If i depart now, it will take me until time X to get there:
		long myEstimatedArrivalTimeAtStation = now.getTime() + travelTimeFromHomeToStationInMilliSeconds;
		Date myEstimatedArrivalDateAtStation = new Date(myEstimatedArrivalTimeAtStation);
		outputDate("myEstimatedArrivalDateAtStation", myEstimatedArrivalDateAtStation, now);

		// 2. What's the diff between 'myEstimatedArrivalTimeAtStation' and 'scheduledDepartureTime'?
		long diffInSeconds = (scheduledDepartureTime.getTime() - myEstimatedArrivalTimeAtStation) / 1000L;
		log.trace("\t\t diffInSeconds={}", (diffInSeconds));
		if (diffInSeconds < 0L) {
			return "off";
		}
		for (Entry<Long, String> entry : colorMap.entrySet()) {
			Long l = entry.getKey();
			if (diffInSeconds <= l) {
				return entry.getValue();
			}
		}

		return "purple";
	}

	private void outputDate(String s, Date d, Date now) {
		long diff = d.getTime() - now.getTime();
		long diffInSeconds = diff / 1000L;
		log.trace("\t\t '{}'\t'{}'\t (diffInSeconds={})\t {}", d, d.getTime(), diffInSeconds, s);
	};

}
