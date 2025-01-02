package reiff.finalproject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClosestStationTest {

    @Test
    public void findStationById() {
        //given
        ClosestStation closestStation = new ClosestStation();
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        Stations stationsResponse = service.statusResponse().blockingGet();
        Station[] stations = stationsResponse.data.stations;
        String id = stations[0].station_id;

        //when
        Station station = closestStation.findStationById(stations, id);

        //then
        assertNotNull(station);
        assertEquals(id, station.station_id);
    }

    @Test
    public void findClosestStationWithBikes() {
        //given
        ClosestStation closestStation = new ClosestStation();
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        Station[] stationsInfo = service.stationsResponse().blockingGet().data.stations;
        Station[] statusInfo = service.statusResponse().blockingGet().data.stations;
        Station[] stations = closestStation.mergeStations(stationsInfo, statusInfo);
        double lat = 40.748817; // Example: Latitude of NYC (Empire State Building)
        double lon = -73.985428; // Example: Longitude of NYC (Empire State Building)

        //when
        Station station = closestStation.findClosestStationWithBikes(stations, lat, lon);

        //then
        assertNotNull(station);
        assertTrue(station.num_bikes_available > 0);
        assertEquals("E 33 St & 5 Ave", station.name);
    }

    @Test
    public void findClosestStationWithSlots() {
        //given
        ClosestStation closestStation = new ClosestStation();
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        Station[] stationsInfo = service.stationsResponse().blockingGet().data.stations;
        Station[] statusInfo = service.statusResponse().blockingGet().data.stations;
        Station[] stations = closestStation.mergeStations(stationsInfo, statusInfo);
        double lat = 40.748817; // Example: Latitude of NYC (Empire State Building)
        double lon = -73.985428; // Example: Longitude of NYC (Empire State Building)

        //when
        Station station = closestStation.findClosestStationWithSlots(stations, lat, lon);

        //then
        assertNotNull(station);
        assertTrue(station.num_docks_available > 0);
        assertEquals("E 33 St & 5 Ave", station.name);
    }
}
