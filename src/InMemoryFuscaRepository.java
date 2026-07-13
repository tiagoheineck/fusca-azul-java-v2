import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple in-memory repository using ArrayList. This simulates a database and can be swapped
 * for a real DB implementation by implementing FuscaRepository.
 */
public class InMemoryFuscaRepository implements FuscaRepository {
    private final List<Fusca> storage = new ArrayList<>();

    @Override
    public void add(Fusca fusca) {
        storage.add(fusca);
    }

    @Override
    public boolean removeById(String id) {
        return storage.removeIf(f -> f.getId().equals(id));
    }

    @Override
    public boolean update(Fusca fusca) {
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).getId().equals(fusca.getId())) {
                storage.set(i, fusca);
                return true;
            }
        }
        return false;
    }

    @Override
    public Fusca findById(String id) {
        for (Fusca f : storage) {
            if (f.getId().equals(id)) return f;
        }
        return null;
    }

    @Override
    public List<Fusca> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(storage));
    }

    @Override
    public List<Fusca> findByColor(String color) {
        List<Fusca> result = new ArrayList<>();
        if (color == null) return result;
        for (Fusca f : storage) {
            if (color.equalsIgnoreCase(f.getColor())) {
                result.add(f);
            }
        }
        return result;
    }
}
