import java.io.*;
import java.util.*;

class City {
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

class TravelLink {
    String targetCity;
    int fare;
    int travelDuration;

    public TravelLink(String targetCity, int fare, int travelDuration) {
        this.targetCity = targetCity;
        this.fare = fare;
        this.travelDuration = travelDuration;
    }
}

class Journey {
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

public class JourneyPlanner {
    private static List<City> cities = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Missing input and output file names.");
            return;
        }

        String mapFile = args[0];
        String requestsFile = args[1];
        String resultsFile = args[2];

        loadCityMap(mapFile);
        try (FileWriter writer = new FileWriter(resultsFile)) {
            handleRequests(requestsFile, writer);
        }
    }

    private static void loadCityMap(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 4) continue;

                String origin = parts[0].trim();
                String destination = parts[1].trim();
                int fare = Integer.parseInt(parts[2].trim());
                int duration = Integer.parseInt(parts[3].trim());

                getOrAddCity(origin).connectCity(destination, fare, duration);
            }
        }
    }

    private static City getOrAddCity(String cityName) {
        for (City city : cities) {
            if (city.cityName.equals(cityName)) {
                return city;
            }
        }
        City newCity = new City(cityName);
        cities.add(newCity);
        return newCity;
    }

    private static void handleRequests(String fileName, FileWriter writer) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int requestId = 1;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split("\\|");
                if (details.length < 3) continue;

                String origin = details[0].trim();
                String destination = details[1].trim();
                char preference = details[2].trim().charAt(0);

                List<Journey> options = searchRoutesUsingStack(origin, destination);
                if (options.isEmpty()) {
                    writer.write("No available journeys from " + origin + " to " + destination + ".\n");
                } else {
                    Comparator<Journey> comparator = (preference == 'T') ?
                        Comparator.comparingInt(j -> j.totalDuration) :
                        Comparator.comparingInt(j -> j.totalFare);

                    options.sort(comparator);

                    writer.write(String.format("Request %d: %s to %s, Preference: %s\n", requestId, origin, destination, preference == 'T' ? "Duration" : "Fare"));
                    for (int i = 0; i < Math.min(options.size(), 3); i++) {
                        writer.write(String.format("Option %d: %s\n", i + 1, options.get(i)));
                    }
                    writer.write("\n");
                }
                requestId++;
            }
        }
    }

    private static List<Journey> searchRoutesUsingStack(String start, String end) {
        List<Journey> journeys = new LinkedList<>();
        Stack<Journey> stack = new Stack<>();
        Journey initialJourney = new Journey();
        initialJourney.addStop(start, 0, 0);

        stack.push(initialJourney);

        while (!stack.isEmpty()) {
            Journey currentJourney = stack.pop();
            String lastStop = currentJourney.stops.getLast();

            if (lastStop.equals(end)) {
                journeys.add(new Journey(currentJourney));
                continue;
            }

            City city = findCity(lastStop);
            if (city != null) {
                for (TravelLink link : city.links) {
                    if (!currentJourney.stops.contains(link.targetCity)) {
                        Journey newJourney = new Journey(currentJourney);
                        newJourney.addStop(link.targetCity, link.fare, link.travelDuration);
                        stack.push(newJourney);
                    }
                }
            }
        }
        return journeys;
    }

    private static City findCity(String cityName) {
        for (City city : cities) {
            if (city.cityName.equals(cityName)) {
                return city;
            }
        }
        return null;
    }
}
