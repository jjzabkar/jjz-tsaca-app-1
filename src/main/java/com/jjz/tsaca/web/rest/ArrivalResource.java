package com.jjz.tsaca.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.jjz.tsaca.domain.Arrival;
import com.jjz.tsaca.repository.ArrivalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Arrival.
 */
@RestController
@RequestMapping("/api")
public class ArrivalResource {

    private final Logger log = LoggerFactory.getLogger(ArrivalResource.class);

    @Inject
    private ArrivalRepository arrivalRepository;

    /**
     * POST  /arrivals -> Create a new arrival.
     */
    @RequestMapping(value = "/arrivals",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Arrival> create(@RequestBody Arrival arrival) throws URISyntaxException {
        log.debug("REST request to save Arrival : {}", arrival);
        if (arrival.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new arrival cannot already have an ID").body(null);
        }
        Arrival result = arrivalRepository.save(arrival);
        return ResponseEntity.created(new URI("/api/arrivals/" + arrival.getId())).body(result);
    }

    /**
     * PUT  /arrivals -> Updates an existing arrival.
     */
    @RequestMapping(value = "/arrivals",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Arrival> update(@RequestBody Arrival arrival) throws URISyntaxException {
        log.debug("REST request to update Arrival : {}", arrival);
        if (arrival.getId() == null) {
            return create(arrival);
        }
        Arrival result = arrivalRepository.save(arrival);
        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /arrivals -> get all the arrivals.
     */
    @RequestMapping(value = "/arrivals",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Arrival> getAll() {
        log.debug("REST request to get all Arrivals");
        return arrivalRepository.findAll();
    }

    /**
     * GET  /arrivals/:id -> get the "id" arrival.
     */
    @RequestMapping(value = "/arrivals/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Arrival> get(@PathVariable Long id) {
        log.debug("REST request to get Arrival : {}", id);
        return Optional.ofNullable(arrivalRepository.findOne(id))
            .map(arrival -> new ResponseEntity<>(
                arrival,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /arrivals/:id -> delete the "id" arrival.
     */
    @RequestMapping(value = "/arrivals/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Arrival : {}", id);
        arrivalRepository.delete(id);
    }
}
