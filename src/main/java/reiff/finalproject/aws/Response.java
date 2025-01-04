package reiff.finalproject.aws;

import reiff.finalproject.Station;

public class Response {

    private Coordinate from;
    private Station startStation;
    private Station endStation;
    private Coordinate to;

    public Response(Coordinate from, Station startStation, Station endStation, Coordinate to) {

        this.from = from;
        this.startStation = startStation;
        this.endStation = endStation;
        this.to = to;
    }

    public Station getStartStation() {
        return startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    public String toJsonString() {
        return "{\n"
                +
                "  \"from\": {\n"
                +
                "    \"lat\": " + from.getLat() + ",\n"
                +
                "    \"lon\": " + from.getLon() + "\n"
                +
                "  },\n"
                +
                "  \"start\": {\n"
                +
                "    \"lat\": " + startStation.lat + ",\n"
                +
                "    \"lon\": " + startStation.lon + ",\n"
                +
                "    \"name\": \"" + startStation.name + "\",\n"
                +
                "    \"station_id\": \"" + startStation.station_id + "\"\n"
                +
                "  },\n"
                +
                "  \"end\": {\n"
                +
                "    \"lat\": " + endStation.lat + ",\n"
                +
                "    \"lon\": " + endStation.lon + ",\n"
                +
                "    \"name\": \"" + endStation.name + "\",\n"
                +
                "    \"station_id\": \"" + endStation.station_id + "\"\n"
                +
                "  },\n"
                +
                "  \"to\": {\n"
                +
                "    \"lat\": " + to.getLat() + ",\n"
                +
                "    \"lon\": " + to.getLon() + "\n"
                +
                "  }\n"
                +
                "}";
    }

}
