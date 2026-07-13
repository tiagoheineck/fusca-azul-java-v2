import java.util.List;

/**
 * Repository interface to manage Fusca storage. Implementations can use ArrayList (in-memory)
 * or be swapped for a database-backed implementation later.
 */
public interface FuscaRepository {
    void add(Fusca fusca);
    boolean removeById(String id);
    boolean update(Fusca fusca);
    Fusca findById(String id);
    List<Fusca> findAll();
    List<Fusca> findByColor(String color);
}
