
import java.util.List;

public class Main {

	public static void main(String[] args) {
		// Setup repository and service (currently in-memory; easy to swap later)
		FuscaRepository repo = new InMemoryFuscaRepository();
		FuscaService service = new FuscaService(repo);

		// Create some people (demonstrates polymorphism: Player vs Person)
		Player alice = new Player("Alice");
		Person bob = new Person("Bob");

		// Add some fuscas
		Fusca f1 = service.createFusca("azul", "Carlos");
		Fusca f2 = service.createFusca("red", "Maria");
		Fusca f3 = service.createFusca("Blue", "Joao");

		System.out.println("Initial fuscas stored:");
		for (Fusca f : service.listAll()) {
			System.out.println("  " + f);
		}

		// Demonstrate punching: only blue fuscas allow punches
		System.out.println();
		System.out.println("Actions:");
		service.seeFuscaAndPunch(f1.getId(), alice, bob); // alice is Player -> +2
		service.seeFuscaAndPunch(f2.getId(), bob, alice);  // bob is Person -> non-blue, no points
		service.seeFuscaAndPunch(f3.getId(), bob, alice);  // f3 is blue -> bob gets +1

		System.out.println();
		System.out.println("Scores after actions:");
		System.out.println("  " + alice.getName() + " = " + alice.getScore());
		System.out.println("  " + bob.getName() + " = " + bob.getScore());

		// Search by color
		System.out.println();
		System.out.println("Fuscas with color 'azul':");
		List<Fusca> azuis = service.findByColor("azul");
		for (Fusca f : azuis) System.out.println("  " + f);

		// Edit a fusca (change color of f2 to azul)
		System.out.println();
		System.out.println("Editing fusca " + f2.getId() + " to color 'azul'");
		f2.setColor("azul");
		service.updateFusca(f2);

		System.out.println("Fuscas now:");
		for (Fusca f : service.listAll()) System.out.println("  " + f);

		// Remove a fusca
		System.out.println();
		System.out.println("Removing fusca " + f1.getId());
		service.removeFusca(f1.getId());

		System.out.println("Final fuscas:");
		for (Fusca f : service.listAll()) System.out.println("  " + f);

		System.out.println();
		System.out.println("Demo complete.");
	}

}