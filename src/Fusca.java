/**
 * Fusca class (a specific Car). Demonstrates specialization of a superclass.
 */
public class Fusca extends Car {
    private String ownerName; // composition: a Fusca can have an owner name (simple composition)

    public Fusca(String id, String color, String ownerName) {
        super(id, color, "Fusca");
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public String toString() {
        return "Fusca{" + "id='" + getId() + '\'' + ", color='" + getColor() + '\'' + ", owner='" + ownerName + '\'' + '}';
    }
}
