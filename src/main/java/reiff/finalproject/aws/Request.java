package reiff.finalproject.aws;

public class Request {

    private Coordinate from;
    private Coordinate to;

    public Request(Coordinate from, Coordinate to){

        this.from = from;
        this.to = to;
    }

    public Coordinate getFrom() {
        return from;
    }

    public Coordinate getTo() {
        return to;
    }
}
