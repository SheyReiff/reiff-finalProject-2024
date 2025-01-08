package reiff.finalproject.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import reiff.finalproject.*;


public class ClosestStationRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, Response> {

    @Override
    public Response handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        // Parse the request body
        String body = event.getBody();

        System.out.println("Event Body: " + body);
        if (body == null || body.isEmpty()) {
            throw new IllegalArgumentException("Request body is null or empty");
        }


        Gson gson = new Gson();
        Request request = gson.fromJson(body, Request.class);

        if (request == null || request.getFrom() == null || request.getTo() == null) {
            throw new IllegalArgumentException("Invalid request: from/to coordinates are null");
        }

        ClosestStation closestStation = new ClosestStation();
        CitiBikeService service = new CitiBikeServiceFactory().getService();
        StationsCache stationsCache = new StationsCache(service);

        Station[] stationsInfo = stationsCache.getStationsResponse().data.stations;

        Station[] statusInfo = service.statusResponse().blockingGet().data.stations;


        Station[] stations = closestStation.mergeStations(stationsInfo, statusInfo);

        Station startStation = closestStation.findClosestStationWithBikes(stations,
                request.getFrom().getLat(), request.getFrom().getLon());
        Station endStation = closestStation.findClosestStationWithSlots(stations,
                request.getTo().getLat(), request.getTo().getLon());

        return new Response(
                request.getFrom(),
                startStation,
                endStation,
                request.getTo()
        );
    }
}
