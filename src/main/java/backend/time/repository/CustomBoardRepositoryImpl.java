package backend.time.repository;

import backend.time.dto.BoardDistanceDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

public class CustomBoardRepositoryImpl implements CustomBoardRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<BoardDistanceDto> findNearbyOrUnspecifiedLocationBoardsWithDistance(double userLongitude, double userLatitude) {
        String sql = "SELECT id, ROUND(IFNULL(ST_Distance_Sphere(point(longitude, latitude), point(:userLongitude, :userLatitude)) / 1000, NULL), 1) AS distance " +
                "FROM board " +
                "WHERE longitude IS NULL AND latitude IS NULL " +
                "OR ST_Distance_Sphere(point(longitude, latitude), point(:userLongitude, :userLatitude)) / 1000 <= 10 " +
                "ORDER BY created_at DESC";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userLongitude", userLongitude);
        query.setParameter("userLatitude", userLatitude);

        List<Object[]> results = query.getResultList();
        return results.stream()
                .map(result -> new BoardDistanceDto(
                        ((Number) result[0]).longValue(),
                        (result[1] != null) ? ((Number) result[1]).doubleValue() : null))
                .collect(Collectors.toList());
    }
}
