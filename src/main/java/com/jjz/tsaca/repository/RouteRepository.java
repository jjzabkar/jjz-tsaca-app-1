package com.jjz.tsaca.repository;

import com.jjz.tsaca.domain.Route;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Route entity.
 */
public interface RouteRepository extends JpaRepository<Route,Long> {

}
