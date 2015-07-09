package com.jjz.tsaca.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.jjz.tsaca.domain.Station;
import com.jjz.tsaca.repository.StationRepository;
import com.jjz.tsaca.service.OneBusAwayApiStopService;

/**
 * REST controller for managing Station.
 */
@RestController
@RequestMapping("/api")
public class StationResource {

    private final Logger log = LoggerFactory.getLogger(StationResource.class);

    @Inject
    private StationRepository stationRepository;

	@Inject
	private OneBusAwayApiStopService obaStopService;

    /**
     * POST  /stations -> Create a new station.
     */
    @RequestMapping(value = "/stations",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Station> create(@RequestBody Station station) throws URISyntaxException {
        log.debug("REST request to save Station : {}", station);
        if (station.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new station cannot already have an ID").body(null);
        }
		Station result = obaStopService.save(station);
        return ResponseEntity.created(new URI("/api/stations/" + station.getId())).body(result);
    }

    /**
     * PUT  /stations -> Updates an existing station.
     */
    @RequestMapping(value = "/stations",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Station> update(@RequestBody Station station) throws URISyntaxException {
        log.debug("REST request to update Station : {}", station);
        if (station.getId() == null) {
            return create(station);
        }
		Station result = obaStopService.save(station);
        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /stations -> get all the stations.
     */
    @RequestMapping(value = "/stations",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Station> getAll() {
        log.debug("REST request to get all Stations");
        return stationRepository.findAll();
    }

    /**
     * GET  /stations/:id -> get the "id" station.
     */
    @RequestMapping(value = "/stations/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Station> get(@PathVariable Long id) {
        log.debug("REST request to get Station : {}", id);
        return Optional.ofNullable(stationRepository.findOneWithEagerRelationships(id))
            .map(station -> new ResponseEntity<>(
                station,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /stations/:id -> delete the "id" station.
     */
    @RequestMapping(value = "/stations/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Station : {}", id);
        stationRepository.delete(id);
    }
}
