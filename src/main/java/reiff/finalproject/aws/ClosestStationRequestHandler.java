package reiff.finalproject.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import reiff.finalproject.CitiBikeService;
import reiff.finalproject.CitiBikeServiceFactory;
import reiff.finalproject.ClosestStation;
import reiff.finalproject.Station;

public class ClosestStationRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, ClosestStationRequestHandler.Response> {

    @Override
    public Response handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        // Parse the request body
        String body = event.getBody();
        Gson gson = new Gson();
        Request request = gson.fromJson(body, Request.class);

        ClosestStation closestStation = new ClosestStation();
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        Station[] stationsInfo = service.stationsResponse().blockingGet().data.stations;
        Station[] statusInfo = service.statusResponse().blockingGet().data.stations;
        Station[] stations = closestStation.mergeStations(stationsInfo, statusInfo);

        Station startStation = closestStation.findClosestStationWithBikes(stations, request.from.lat, request.from.lon);
        Station endStation = closestStation.findClosestStationWithSlots(stations, request.to.lat, request.to.lon);

        return new Response(
                request.from,
                startStation,
                endStation,
                request.to
                //return the string?
        );
    }


    record Request(
            Coordinates from,
            Coordinates to
    ) {
    }

    record Response(
            Coordinates from,
            Station startStation,
            Station endStation,
            Coordinates to
    ) {
        public String toJsonString() {
            return "{\n" +
                    "  \"from\": {\n" +
                    "    \"lat\": " + from.lat + ",\n" +
                    "    \"lon\": " + from.lon + "\n" +
                    "  },\n" +
                    "  \"start\": {\n" +
                    "    \"lat\": " + startStation.lat + ",\n" +
                    "    \"lon\": " + startStation.lon + ",\n" +
                    "    \"name\": \"" + startStation.name + "\",\n" +
                    "    \"station_id\": \"" + startStation.station_id + "\"\n" +
                    "  },\n" +
                    "  \"end\": {\n" +
                    "    \"lat\": " + endStation.lat + ",\n" +
                    "    \"lon\": " + endStation.lon + ",\n" +
                    "    \"name\": \"" + endStation.name + "\",\n" +
                    "    \"station_id\": \"" + endStation.station_id + "\"\n" +
                    "  },\n" +
                    "  \"to\": {\n" +
                    "    \"lat\": " + to.lat + ",\n" +
                    "    \"lon\": " + to.lon + "\n" +
                    "  }\n" +
                    "}";
        }
    }

    public record Coordinates(double lat, double lon) {
    }
}
