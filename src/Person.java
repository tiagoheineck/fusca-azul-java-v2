/**
 * Person represents an actor in the game. It holds a name and accumulated score.
 * This class will be used to demonstrate polymorphism via the attack method.
 */
public class Person {
    protected String name;
    protected int score;

    public Person(String name) {
        this.name = name;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    /**
     * Attack (punch) behavior. Default implementation: if the fusca is blue, attacker gets 1 point.
     * Subclasses may override to change scoring (polymorphism).
     */
    public void attack(Person target, Fusca fusca) {
        if (fusca == null) {
            System.out.println(name + " tried to attack but no fusca was provided.");
            return;
        }
        if (fusca.isBlue()) {
            addScore(1);
            System.out.println(name + " punched " + target.getName() + " because fusca is blue! (+1)");
        } else {
            System.out.println(name + " saw a non-blue fusca (" + fusca.getColor() + ") and did nothing.");
        }
    }

    @Override
    public String toString() {
        return "Person{" + "name='" + name + '\'' + ", score=" + score + '}';
    }
}
