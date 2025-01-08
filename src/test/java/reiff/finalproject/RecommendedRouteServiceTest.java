package reiff.finalproject;

import org.junit.jupiter.api.Test;
import reiff.finalproject.aws.Coordinate;
import reiff.finalproject.aws.Request;
import reiff.finalproject.aws.Response;
import reiff.finalproject.part3.RecommendedRouteService;
import reiff.finalproject.part3.RecommendedRouteServiceFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecommendedRouteServiceTest {
    @Test
    public void getStation() {

        //given
        RecommendedRouteService service = new RecommendedRouteServiceFactory().getService();
        Coordinate from = new Coordinate(40.8211, -73.9359);
        Coordinate to = new Coordinate(40.7190,  -73.9585);
        Request request = new Request(from, to);

        //when
        Response response = service.getStation(request).blockingGet();

        //then
        assertEquals(response.getStartStation().name, "Lenox Ave & W 146 St");
    }
}

