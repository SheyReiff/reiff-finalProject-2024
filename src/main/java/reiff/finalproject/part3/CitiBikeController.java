package reiff.finalproject.part3;

import org.jxmapviewer.viewer.GeoPosition;
import reiff.finalproject.aws.Coordinate;
import reiff.finalproject.aws.Request;
import reiff.finalproject.aws.Response;

public class CitiBikeController {

    public Response getRecommendedStations(GeoPosition from, GeoPosition to) {

        Coordinate fromCoordinate = new Coordinate(from.getLatitude(), from.getLongitude());
        Coordinate toCoordinate = new Coordinate(to.getLatitude(), to.getLongitude());

        RecommendedRouteService service = new RecommendedRouteServiceFactory().getService();
        Request request = new Request(fromCoordinate, toCoordinate);

        return
                service.getStation(request).blockingGet();

    }
}


