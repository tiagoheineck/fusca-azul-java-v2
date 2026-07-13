import java.util.List;
import java.util.UUID;

/**
 * Service layer that uses a FuscaRepository to perform operations. This keeps business logic
 * separate from storage and makes it easy to swap implementations later.
 */
public class FuscaService {
    private final FuscaRepository repository;

    public FuscaService(FuscaRepository repository) {
        this.repository = repository;
    }

    public Fusca createFusca(String color, String ownerName) {
        String id = UUID.randomUUID().toString();
        Fusca f = new Fusca(id, color, ownerName);
        repository.add(f);
        return f;
    }

    public boolean removeFusca(String id) {
        return repository.removeById(id);
    }

    public boolean updateFusca(Fusca fusca) {
        return repository.update(fusca);
    }

    public Fusca findById(String id) {
        return repository.findById(id);
    }

    public List<Fusca> listAll() {
        return repository.findAll();
    }

    public List<Fusca> findByColor(String color) {
        return repository.findByColor(color);
    }

    /**
     * See a fusca and attempt a punch. Returns true if attack was attempted (fusca existed),
     * false if no fusca found.
     */
    public boolean seeFuscaAndPunch(String fuscaId, Person attacker, Person target) {
        Fusca f = repository.findById(fuscaId);
        if (f == null) return false;
        attacker.attack(target, f);
        return true;
    }
}
