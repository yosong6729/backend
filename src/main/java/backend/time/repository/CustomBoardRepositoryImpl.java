package backend.time.repository;

import backend.time.dto.BoardDistanceDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomBoardRepositoryImpl implements CustomBoardRepository {


    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<BoardDistanceDto> findNearbyOrUnspecifiedLocationBoardsWithDistance(double userLongitude, double userLatitude) {
        System.out.println("findNearbyOrUnspecifiedLocationBoardsWithDistance 들어감");
//        String sql = "SELECT id, ROUND(IFNULL(ST_Distance_Sphere(point(longitude, latitude), point(?, ?)) / 1000, NULL), 1) AS distance " +
//                "FROM board " +
//                "WHERE longitude IS NULL AND latitude IS NULL " +
//                "OR ST_Distance_Sphere(point(longitude, latitude), point(?, ?)) / 1000 <= 10 " +
//                "ORDER BY created_date DESC";

//        String sql = "SELECT id, ROUND(IFNULL(ST_Distance_Sphere(POINT(longitude, latitude), POINT(?, ?)) / 1000, NULL), 1) AS distance " +
//                "FROM board " +
//                "WHERE (longitude IS NOT NULL AND latitude IS NOT NULL AND ST_Distance_Sphere(POINT(longitude, latitude), POINT(?, ?)) / 1000 <= 10)" +
//                "OR (longitude IS NULL AND latitude IS NULL)" +
//                "ORDER BY created_date DESC";

//        String sql = "SELECT id, " +
//                "ROUND(IFNULL((6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) " +
//                "+ sin(radians(?)) * sin(radians(latitude)))), NULL), 1) AS distance " +
//                "FROM board " +
//                "WHERE (longitude IS NOT NULL AND latitude IS NOT NULL " +
//                "AND (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) " +
//                "+ sin(radians(?)) * sin(radians(latitude)))) <= 10) " +
//                "OR (longitude IS NULL AND latitude IS NULL) " +
//                "ORDER BY created_date DESC";

        String sql = "SELECT id, " +
                "ROUND(IFNULL((6371 * acos(cos(radians(?)) * cos(radians(IFNULL(latitude, ?))) * cos(radians(IFNULL(longitude, ?)) - radians(?)) " +
                "+ sin(radians(?)) * sin(radians(IFNULL(latitude, ?))))), 0), 1) AS distance " +
                "FROM board " +
                "WHERE ((longitude IS NOT NULL AND latitude IS NOT NULL " +
                "AND (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) " +
                "+ sin(radians(?)) * sin(radians(latitude)))) <= 10) " +
                "OR (longitude IS NULL AND latitude IS NULL)) " +
                "ORDER BY created_date DESC";


        Query query = entityManager.createNativeQuery(sql);

        query.setParameter(1, userLatitude);
        query.setParameter(2, userLatitude);
        query.setParameter(3, userLongitude);
        query.setParameter(4, userLongitude);
        query.setParameter(5, userLatitude);
        query.setParameter(6, userLatitude);
        query.setParameter(7, userLatitude);
        query.setParameter(8, userLongitude);
        query.setParameter(9, userLatitude);

//        String sql = "SELECT id, " +
//                "ROUND(IFNULL((6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) " +
//                "+ sin(radians(?)) * sin(radians(latitude)))), 0), 1) AS distance " +
//                "FROM board " +
//                "WHERE (longitude IS NOT NULL AND latitude IS NOT NULL " +
//                "AND (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) " +
//                "+ sin(radians(?)) * sin(radians(latitude)))) <= 10) " +
//                "OR (longitude IS NULL AND latitude IS NULL) " +
//                "ORDER BY created_date DESC";
//
//        Query query = entityManager.createNativeQuery(sql);
//
//        query.setParameter(1, userLatitude);
//        query.setParameter(2, userLongitude);
//        query.setParameter(3, userLatitude);
//        query.setParameter(4, userLatitude);
//        query.setParameter(5, userLongitude);
//        query.setParameter(6, userLatitude);

//        query.setParameter("userLongitude", userLongitude);
//        query.setParameter("userLatitude", userLatitude);

        List<Object[]> results = query.getResultList();
        results.forEach(result -> System.out.println("ID: " + result[0] + ", Distance: " + result[1]));
        return results.stream()
                .map(result -> new BoardDistanceDto(
                        ((Number) result[0]).longValue(),
                        (result[1] != null) ? ((Number) result[1]).doubleValue() : null))
                .collect(Collectors.toList());
    }
}
