import java.util.Objects;

/**
 * Base class to demonstrate inheritance. A Vehicle has an id and a color.
 */
public abstract class Vehicle {
    private String id;
    private String color;

    public Vehicle(String id, String color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isBlue() {
        if (color == null) return false;
        return "blue".equalsIgnoreCase(color) || "azul".equalsIgnoreCase(color);
    }

    @Override
    public String toString() {
        return "Vehicle{id='" + id + '\'' + ", color='" + color + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
