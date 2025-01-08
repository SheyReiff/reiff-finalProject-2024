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

    public Coordinate getFrom() {
        return from;
    }

    public Station getStartStation() {
        return startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    public Coordinate getTo() {
        return to;
    }

}
