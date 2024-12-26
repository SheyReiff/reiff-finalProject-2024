package reiff.finalproject;

public class ClosestStation {

    public Station findStationById(Station[] stations, String stationId) {
        for (Station station : stations) {
            if (station.station_id.equals(stationId)) {
                return station;
            }
        }
        return null;
    }

    public Station findClosestStationWithBikes(Station[] stations, double userLat, double userLon) {
        Station closestStation = null;
        double minDistance = Double.MAX_VALUE;

        for (Station station : stations) {
            if (station.num_bikes_available > 0) {
                double distance = calculateDistance(userLat, userLon, station.lat, station.lon);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestStation = station;
                }
            }
        }
        return closestStation;
    }

    public Station findClosestStationWithSlots(Station[] stations, double userLat, double userLon) {
        Station closestStation = null;
        double minDistance = Double.MAX_VALUE;

        for (Station station : stations) {
            if (station.num_docks_available > 0) {
                double distance = calculateDistance(userLat, userLon, station.lat, station.lon);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestStation = station;
                }
            }
        }
        return closestStation;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0;
        // if we want to do with miles: 
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}
