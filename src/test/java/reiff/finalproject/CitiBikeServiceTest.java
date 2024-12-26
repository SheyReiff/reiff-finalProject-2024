package reiff.finalproject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CitiBikeServiceTest {
    @Test
    public void stationsResponse() {
        //given
        CitiBikeService service = new CitiBikeServiceFactory().getService();

        //when
        Stations stations = service.stationsResponse().blockingGet();
        Station station = stations.data.stations[0];

        //then
        assertNotNull(station.station_id);
        assertNotNull(station.name);
        assertNotEquals(0, station.lat);
        assertNotEquals(0, station.lon);
    }

    @Test
    public void statusResponse() {
        //given
        CitiBikeService service = new CitiBikeServiceFactory().getService();

        //when
        Stations stations = service.statusResponse().blockingGet();
        Station station = stations.data.stations[0];


        //then
        assertNotNull(station.station_id);
        assertTrue(station.num_bikes_available >= 0);
        assertTrue(station.num_docks_available >= 0);

    }
}
