package reiff.finalproject.part3;

import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import reiff.finalproject.aws.Coordinate;
import reiff.finalproject.aws.Request;
import reiff.finalproject.aws.Response;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CitiBikeController {

    List<GeoPosition> track = Arrays.asList();
    Set<Waypoint> waypoints = new HashSet<>(Arrays.asList());
    boolean isFirstClick = true;
    boolean waypointsLocked = false;
    GeoPosition from;
    GeoPosition to;

    public void handleFirstClick(GeoPosition clickedPosition) {
        from = clickedPosition;
        waypoints.add(new DefaultWaypoint(from));
        isFirstClick = false;
    }

    public void handleSecondClick(GeoPosition clickedPosition) {
        to = clickedPosition;
        waypoints.add(new DefaultWaypoint(to));
        isFirstClick = true;
        waypoints.add(new DefaultWaypoint(to));
        waypointsLocked = true;
    }


    public void resetSelectionsController() {
        waypoints.clear();
        track.clear();
        waypointsLocked = false;
    }


    public void calculateRoute(GeoPosition from, GeoPosition to, CitiBikeFrame frame) {

        Coordinate fromCoordinate = new Coordinate(from.getLatitude(), from.getLongitude());
        Coordinate toCoordinate = new Coordinate(to.getLatitude(), to.getLongitude());

        Request request = new Request(fromCoordinate, toCoordinate);
        RecommendedRouteService service = new RecommendedRouteServiceFactory().getService();
        Single<Response> responseSingle = service.getStation(request);


        Disposable disposable = responseSingle
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .subscribe(
                        response -> {
                            GeoPosition startStation = new GeoPosition(response.getStartStation().lat,
                                    response.getStartStation().lon);
                            GeoPosition endStation = new GeoPosition(response.getEndStation().lat,
                                    response.getEndStation().lon);

                            List<GeoPosition> track = Arrays.asList(from, startStation, endStation, to);
                            waypoints.add(new DefaultWaypoint(startStation));
                            waypoints.add(new DefaultWaypoint(endStation));

                            frame.updateMap(track, waypoints, from, startStation, endStation, to);
                            frame.hideProgressBar();
                        },
                        throwable -> {
                            frame.showCalculatingError("Error calculating route: " + throwable.getMessage());
                            frame.hideProgressBar();
                        }
                );
    }
}



