import java.util.LinkedList;

public class City {
    String cityName;
    LinkedList<TravelLink> links;

    public City(String cityName) {
        this.cityName = cityName;
        this.links = new LinkedList<>();
    }

    public void connectCity(String destination, int cost, int travelTime) {
        links.add(new TravelLink(destination, cost, travelTime));
    }
}
