package com.jjz.tsaca.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.jjz.tsaca.Application;
import com.jjz.tsaca.domain.Route;
import com.jjz.tsaca.repository.RouteRepository;
import com.jjz.tsaca.service.OneBusAwayApiRouteService;


/**
 * Test class for the RouteResource REST controller.
 *
 * @see RouteResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class RouteResourceTest {

    private static final String DEFAULT_ROUTE_ID = "SAMPLE_TEXT";
    private static final String UPDATED_ROUTE_ID = "UPDATED_TEXT";
    private static final String DEFAULT_LONG_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_LONG_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_SHORT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_SHORT_NAME = "UPDATED_TEXT";

    @Inject
    private RouteRepository routeRepository;

	@Mock
	private OneBusAwayApiRouteService obaRouteService;

    private MockMvc restRouteMockMvc;

    private Route route;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);

        RouteResource routeResource = new RouteResource();
        ReflectionTestUtils.setField(routeResource, "routeRepository", routeRepository);
		ReflectionTestUtils.setField(routeResource, "obaRouteService", obaRouteService);
		when(obaRouteService.save(any(Route.class))).thenReturn(route);
        this.restRouteMockMvc = MockMvcBuilders.standaloneSetup(routeResource).build();
    }

    @Before
    public void initTest() {
        route = new Route();
        route.setRouteId(DEFAULT_ROUTE_ID);
        route.setLongName(DEFAULT_LONG_NAME);
        route.setShortName(DEFAULT_SHORT_NAME);
    }

	/**
	 * Remove non-unit test validation after adding OBA service layer
	 * 
	 * @throws Exception
	 */
    @Test
	@Ignore
    @Transactional
    public void createRoute() throws Exception {
        int databaseSizeBeforeCreate = routeRepository.findAll().size();

        // Create the Route
        restRouteMockMvc.perform(post("/api/routes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(route)))
                .andExpect(status().isCreated());

        // Validate the Route in the database
        List<Route> routes = routeRepository.findAll();
        assertThat(routes).hasSize(databaseSizeBeforeCreate + 1);
        Route testRoute = routes.get(routes.size() - 1);
        assertThat(testRoute.getRouteId()).isEqualTo(DEFAULT_ROUTE_ID);
        assertThat(testRoute.getLongName()).isEqualTo(DEFAULT_LONG_NAME);
        assertThat(testRoute.getShortName()).isEqualTo(DEFAULT_SHORT_NAME);
    }

    @Test
    @Transactional
    public void getAllRoutes() throws Exception {
        // Initialize the database
        routeRepository.saveAndFlush(route);

        // Get all the routes
        restRouteMockMvc.perform(get("/api/routes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(route.getId().intValue())))
                .andExpect(jsonPath("$.[*].routeId").value(hasItem(DEFAULT_ROUTE_ID.toString())))
                .andExpect(jsonPath("$.[*].longName").value(hasItem(DEFAULT_LONG_NAME.toString())))
                .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getRoute() throws Exception {
        // Initialize the database
        routeRepository.saveAndFlush(route);

        // Get the route
        restRouteMockMvc.perform(get("/api/routes/{id}", route.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(route.getId().intValue()))
            .andExpect(jsonPath("$.routeId").value(DEFAULT_ROUTE_ID.toString()))
            .andExpect(jsonPath("$.longName").value(DEFAULT_LONG_NAME.toString()))
            .andExpect(jsonPath("$.shortName").value(DEFAULT_SHORT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRoute() throws Exception {
        // Get the route
        restRouteMockMvc.perform(get("/api/routes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRoute() throws Exception {
        // Initialize the database
        routeRepository.saveAndFlush(route);

		int databaseSizeBeforeUpdate = routeRepository.findAll().size();

        // Update the route
        route.setRouteId(UPDATED_ROUTE_ID);
        route.setLongName(UPDATED_LONG_NAME);
        route.setShortName(UPDATED_SHORT_NAME);
        restRouteMockMvc.perform(put("/api/routes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(route)))
                .andExpect(status().isOk());

        // Validate the Route in the database
        List<Route> routes = routeRepository.findAll();
        assertThat(routes).hasSize(databaseSizeBeforeUpdate);
        Route testRoute = routes.get(routes.size() - 1);
        assertThat(testRoute.getRouteId()).isEqualTo(UPDATED_ROUTE_ID);
        assertThat(testRoute.getLongName()).isEqualTo(UPDATED_LONG_NAME);
        assertThat(testRoute.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    public void deleteRoute() throws Exception {
        // Initialize the database
        routeRepository.saveAndFlush(route);

		int databaseSizeBeforeDelete = routeRepository.findAll().size();

        // Get the route
        restRouteMockMvc.perform(delete("/api/routes/{id}", route.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Route> routes = routeRepository.findAll();
        assertThat(routes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
