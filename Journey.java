import java.util.LinkedList;

public class Journey {
    LinkedList<String> stops;
    int totalFare;
    int totalDuration;

    public Journey() {
        stops = new LinkedList<>();
        totalFare = 0;
        totalDuration = 0;
    }

    public Journey(Journey other) {
        this.stops = new LinkedList<>(other.stops);
        this.totalFare = other.totalFare;
        this.totalDuration = other.totalDuration;
    }

    public void addStop(String city, int fare, int duration) {
        stops.add(city);
        totalFare += fare;
        totalDuration += duration;
    }

    @Override
    public String toString() {
        return String.join(" -> ", stops) + ". Total Duration: " + totalDuration + " minutes, Total Fare: $" + totalFare;
    }
}
