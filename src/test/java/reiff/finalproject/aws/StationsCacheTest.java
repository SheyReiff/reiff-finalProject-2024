package reiff.finalproject.aws;

import org.junit.jupiter.api.Test;
import reiff.finalproject.CitiBikeServiceFactory;
import reiff.finalproject.Station;
import reiff.finalproject.Stations;


import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StationsCacheTest {

    @Test
    void getStations() {
        //given
        StationsCache stationsCache = new StationsCache(new CitiBikeServiceFactory().getService());

        //when
        Stations response = stationsCache.getStations();

        //then
        assertNotNull(response.data.stations);
        Station firstStation = response.data.stations[0];
        assertNotNull(firstStation.name);
        assertTrue(firstStation.lat != 0);
        assertTrue(firstStation.lon != 0);
        assertNotNull(firstStation.station_id);
    }



    @Test
    void getStationsAfterCacheExpiry() {
        //given
        StationsCache stationsCache = new StationsCache(new CitiBikeServiceFactory().getService());
        Stations firstResponse = stationsCache.getStations();
        assertNotNull(firstResponse);
        stationsCache.lastModified = (Instant.now().minus(Duration.ofHours(3)));


        //when
        Stations responseLater = stationsCache.getStations();

        //then
        assertNotNull(responseLater.data.stations);
        Station firstStation = responseLater.data.stations[0];
        assertNotNull(firstStation.station_id);
        assertNotNull(firstStation.name);
        assertTrue(firstStation.lat != 0);
        assertTrue(firstStation.lon != 0);
    }


}
