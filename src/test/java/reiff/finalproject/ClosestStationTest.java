package reiff.finalproject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClosestStationTest {

    @Test
    public void findStationById() {
        //given
        ClosestStation closestStation = new ClosestStation();
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        Stations stationsResponse = service.StatusResponse().blockingGet();
        Station[] stations = stationsResponse.data.stations;
        String Id = stations[0].station_id;

        //when
        Station station = closestStation.findStationById(stations, Id);

        //then
        assertNotNull(station);
        assertEquals(Id, station.station_id);
    }

    @Test
    public void findClosestStationWithBikes() {
        //given
        ClosestStation closestStation = new ClosestStation();
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        Stations stations = service.StatusResponse().blockingGet();
        double lat = 40.748817; // Example: Latitude of NYC (Empire State Building)
        double lon = -73.985428; // Example: Longitude of NYC (Empire State Building)

        //when
        Station station = closestStation.findClosestStationWithBikes(stations.data.stations, lat, lon);

        //then
        assertNotNull(station);
        assertTrue(station.num_bikes_available > 0);
    }

    @Test
    public void findClosestStationWithSlots() {
        //given
        ClosestStation closestStation = new ClosestStation();
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        Stations stations = service.StatusResponse().blockingGet();
        double lat = 40.748817; // Example: Latitude of NYC (Empire State Building)
        double lon = -73.985428; // Example: Longitude of NYC (Empire State Building)

        //when
        Station station = closestStation.findClosestStationWithSlots(stations.data.stations, lat, lon);

        //then
        assertNotNull(station);
        assertTrue(station.num_docks_available > 0);
    }
}
