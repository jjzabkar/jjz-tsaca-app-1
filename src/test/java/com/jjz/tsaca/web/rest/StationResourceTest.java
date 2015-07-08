package com.jjz.tsaca.web.rest;

import com.jjz.tsaca.Application;
import com.jjz.tsaca.domain.Station;
import com.jjz.tsaca.repository.StationRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the StationResource REST controller.
 *
 * @see StationResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class StationResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final Integer DEFAULT_OUTPUT_SLOTS = 0;
    private static final Integer UPDATED_OUTPUT_SLOTS = 1;
    private static final String DEFAULT_STOP_ID = "SAMPLE_TEXT";
    private static final String UPDATED_STOP_ID = "UPDATED_TEXT";

    private static final Long DEFAULT_TRAVEL_TIME_FROM_HOME_TO_STATION_IN_SECONDS = 0L;
    private static final Long UPDATED_TRAVEL_TIME_FROM_HOME_TO_STATION_IN_SECONDS = 1L;

    @Inject
    private StationRepository stationRepository;

    private MockMvc restStationMockMvc;

    private Station station;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StationResource stationResource = new StationResource();
        ReflectionTestUtils.setField(stationResource, "stationRepository", stationRepository);
        this.restStationMockMvc = MockMvcBuilders.standaloneSetup(stationResource).build();
    }

    @Before
    public void initTest() {
        station = new Station();
        station.setName(DEFAULT_NAME);
        station.setOutputSlots(DEFAULT_OUTPUT_SLOTS);
        station.setStopId(DEFAULT_STOP_ID);
        station.setTravelTimeFromHomeToStationInSeconds(DEFAULT_TRAVEL_TIME_FROM_HOME_TO_STATION_IN_SECONDS);
    }

    @Test
    @Transactional
    public void createStation() throws Exception {
        int databaseSizeBeforeCreate = stationRepository.findAll().size();

        // Create the Station
        restStationMockMvc.perform(post("/api/stations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(station)))
                .andExpect(status().isCreated());

        // Validate the Station in the database
        List<Station> stations = stationRepository.findAll();
        assertThat(stations).hasSize(databaseSizeBeforeCreate + 1);
        Station testStation = stations.get(stations.size() - 1);
        assertThat(testStation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStation.getOutputSlots()).isEqualTo(DEFAULT_OUTPUT_SLOTS);
        assertThat(testStation.getStopId()).isEqualTo(DEFAULT_STOP_ID);
        assertThat(testStation.getTravelTimeFromHomeToStationInSeconds()).isEqualTo(DEFAULT_TRAVEL_TIME_FROM_HOME_TO_STATION_IN_SECONDS);
    }

    @Test
    @Transactional
    public void getAllStations() throws Exception {
        // Initialize the database
        stationRepository.saveAndFlush(station);

        // Get all the stations
        restStationMockMvc.perform(get("/api/stations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(station.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].outputSlots").value(hasItem(DEFAULT_OUTPUT_SLOTS)))
                .andExpect(jsonPath("$.[*].stopId").value(hasItem(DEFAULT_STOP_ID.toString())))
                .andExpect(jsonPath("$.[*].travelTimeFromHomeToStationInSeconds").value(hasItem(DEFAULT_TRAVEL_TIME_FROM_HOME_TO_STATION_IN_SECONDS.intValue())));
    }

    @Test
    @Transactional
    public void getStation() throws Exception {
        // Initialize the database
        stationRepository.saveAndFlush(station);

        // Get the station
        restStationMockMvc.perform(get("/api/stations/{id}", station.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(station.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.outputSlots").value(DEFAULT_OUTPUT_SLOTS))
            .andExpect(jsonPath("$.stopId").value(DEFAULT_STOP_ID.toString()))
            .andExpect(jsonPath("$.travelTimeFromHomeToStationInSeconds").value(DEFAULT_TRAVEL_TIME_FROM_HOME_TO_STATION_IN_SECONDS.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingStation() throws Exception {
        // Get the station
        restStationMockMvc.perform(get("/api/stations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStation() throws Exception {
        // Initialize the database
        stationRepository.saveAndFlush(station);

		int databaseSizeBeforeUpdate = stationRepository.findAll().size();

        // Update the station
        station.setName(UPDATED_NAME);
        station.setOutputSlots(UPDATED_OUTPUT_SLOTS);
        station.setStopId(UPDATED_STOP_ID);
        station.setTravelTimeFromHomeToStationInSeconds(UPDATED_TRAVEL_TIME_FROM_HOME_TO_STATION_IN_SECONDS);
        restStationMockMvc.perform(put("/api/stations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(station)))
                .andExpect(status().isOk());

        // Validate the Station in the database
        List<Station> stations = stationRepository.findAll();
        assertThat(stations).hasSize(databaseSizeBeforeUpdate);
        Station testStation = stations.get(stations.size() - 1);
        assertThat(testStation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStation.getOutputSlots()).isEqualTo(UPDATED_OUTPUT_SLOTS);
        assertThat(testStation.getStopId()).isEqualTo(UPDATED_STOP_ID);
        assertThat(testStation.getTravelTimeFromHomeToStationInSeconds()).isEqualTo(UPDATED_TRAVEL_TIME_FROM_HOME_TO_STATION_IN_SECONDS);
    }

    @Test
    @Transactional
    public void deleteStation() throws Exception {
        // Initialize the database
        stationRepository.saveAndFlush(station);

		int databaseSizeBeforeDelete = stationRepository.findAll().size();

        // Get the station
        restStationMockMvc.perform(delete("/api/stations/{id}", station.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Station> stations = stationRepository.findAll();
        assertThat(stations).hasSize(databaseSizeBeforeDelete - 1);
    }
}
