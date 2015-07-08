package com.jjz.tsaca.repository;

import com.jjz.tsaca.domain.Station;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Station entity.
 */
public interface StationRepository extends JpaRepository<Station,Long> {

    @Query("select station from Station station left join fetch station.routes where station.id =:id")
    Station findOneWithEagerRelationships(@Param("id") Long id);

}
