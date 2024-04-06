package backend.time.repository;

import backend.time.dto.BoardDistanceDto;

import java.util.List;

public interface CustomBoardRepository {
    List<BoardDistanceDto> findNearbyOrUnspecifiedLocationBoardsWithDistance(double userLongitude, double userLatitude);
}

