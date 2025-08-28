package com.Ankit.Kaarya.Repo;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepo extends JpaRepository<Industry, Long> {

    // Query method: internal, not meant to be called directly
    @Query(value = """
    SELECT * FROM (
        SELECT i.*, (
            6371000 * acos(LEAST(1,
                cos(radians(:lat)) *
                cos(radians(i.latitude)) *
                cos(radians(:lng) - radians(i.longitude)) +
                sin(radians(:lat)) *
                sin(radians(i.latitude))
            ))
        ) AS distance
        FROM industry i
    ) AS subquery
    WHERE distance < :radius
    ORDER BY distance
    """, nativeQuery = true)
    List<Industry> findNearbyIndustriesRaw(@Param("lat") double lat,
                                           @Param("lng") double lng,
                                           @Param("radius") double radius);

    // Default method: exposed for service usage
    default List<Industry> findNearbyIndustriesByLocation(Location location, double radiusKm) {
        return findNearbyIndustriesRaw(
                location.getLatitude(),
                location.getLongitude(),
                radiusKm * 1000
        );
    }

}