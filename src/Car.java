/**
 * Simple Car class that extends Vehicle. Demonstrates inheritance.
 */
public class Car extends Vehicle {
    private String model;

    public Car(String id, String color, String model) {
        super(id, color);
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Car{" + "id='" + getId() + '\'' + ", color='" + getColor() + '\'' + ", model='" + model + '\'' + '}';
    }
}
