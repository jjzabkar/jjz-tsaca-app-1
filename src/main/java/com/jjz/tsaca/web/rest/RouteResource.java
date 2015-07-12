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
import com.jjz.tsaca.domain.Route;
import com.jjz.tsaca.repository.RouteRepository;
import com.jjz.tsaca.service.OneBusAwayApiRouteService;

/**
 * REST controller for managing Route.
 */
@RestController
@RequestMapping("/api")
public class RouteResource {

    private final Logger log = LoggerFactory.getLogger(RouteResource.class);

    @Inject
    private RouteRepository routeRepository;

	@Inject
	private OneBusAwayApiRouteService obaRouteService;

    /**
     * POST  /routes -> Create a new route.
     */
    @RequestMapping(value = "/routes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Route> create(@RequestBody Route route) throws URISyntaxException {
        log.debug("REST request to save Route : {}", route);
        if (route.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new route cannot already have an ID").body(null);
        }
        Route result = obaRouteService.save(route);
        return ResponseEntity.created(new URI("/api/routes/" + route.getId())).body(result);
    }

    /**
     * PUT  /routes -> Updates an existing route.
     */
    @RequestMapping(value = "/routes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Route> update(@RequestBody Route route) throws URISyntaxException {
        log.debug("REST request to update Route : {}", route);
        if (route.getId() == null) {
            return create(route);
        }
		Route result = routeRepository.save(route);
        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /routes -> get all the routes.
     */
    @RequestMapping(value = "/routes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Route> getAll() {
        log.debug("REST request to get all Routes");
        return routeRepository.findAll();
    }

    /**
     * GET  /routes/:id -> get the "id" route.
     */
    @RequestMapping(value = "/routes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Route> get(@PathVariable Long id) {
        log.debug("REST request to get Route : {}", id);
        return Optional.ofNullable(routeRepository.findOne(id))
            .map(route -> new ResponseEntity<>(
                route,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /routes/:id -> delete the "id" route.
     */
    @RequestMapping(value = "/routes/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Route : {}", id);
        routeRepository.delete(id);
    }
}
