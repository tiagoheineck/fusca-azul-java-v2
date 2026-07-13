/**
 * Player is a specialized Person that gets more points when punching (demonstrates polymorphism).
 */
public class Player extends Person {

    public Player(String name) {
        super(name);
    }

    @Override
    public void attack(Person target, Fusca fusca) {
        if (fusca == null) {
            System.out.println(name + " tried to attack but no fusca was provided.");
            return;
        }
        if (fusca.isBlue()) {
            // Players are more skilled and get 2 points per successful punch
            addScore(2);
            System.out.println(name + " (player) punched " + target.getName() + " because fusca is blue! (+2)");
        } else {
            System.out.println(name + " (player) saw a non-blue fusca (" + fusca.getColor() + ") and did nothing.");
        }
    }
}
