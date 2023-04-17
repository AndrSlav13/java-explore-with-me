package ru.practicum.explorewithme.event.model;

import ru.practicum.explorewithme.event.dto.LocationDTO;

import java.util.Objects;

import static java.lang.Math.abs;

public class Location {
    public float lat;
    public float lon;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("lat=");
        sb.append(LocationDTO.df.format(lat));
        sb.append(" lon=");
        sb.append(LocationDTO.df.format(lon));
        return sb.toString();
    }

    public static Location toLocation(String str) {
        String[] sMas = str.split(" ");
        Location location = new Location();
        location.lat = Float.parseFloat(sMas[0].substring(4).replaceAll(",", "."));
        location.lon = Float.parseFloat(sMas[1].substring(4).replaceAll(",", "."));
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;
        if (this == o || (abs(((Location) o).lat - lat) < LocationDTO.eps) && (abs(((Location) o).lon - lon) < LocationDTO.eps))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
