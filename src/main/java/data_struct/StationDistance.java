package data_struct;


public class StationDistance {
    public Station station;
    public double distance;
    String line;

    public StationDistance(Station station, double distance, String line) {
        this.station = station;
        this.distance = distance;
        this.line = line;
    }

    @Override
    public String toString() {
        return "<" + station.getName() + ", " + line + ", " + distance + ">";
    }
}