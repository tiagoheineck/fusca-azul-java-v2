import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple JSON-backed implementation of {@link FuscaRepository} that stores all Fuscas
 * in a single JSON file. This implementation is intentionally minimal and aimed for
 * educational purposes: it demonstrates file-based persistence, atomic writes and
 * a very small, focused JSON serializer/parser for the Fusca fields used here.
 */
public class FileFuscaRepository implements FuscaRepository {
    private final Path file;
    private final ConcurrentMap<String, Fusca> storage = new ConcurrentHashMap<>();

    // Regex patterns for extracting simple JSON string fields like "id":"value"
    private static final Pattern FIELD_PATTERN = Pattern.compile("\"(\\w+)\"\s*:\s*\"([^\"]*)\"");

    public FileFuscaRepository(String filePath) {
        this.file = Paths.get(filePath);
        try {
            // ensure parent directory exists
            Path parent = this.file.getParent();
            if (parent != null && !Files.exists(parent, LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectories(parent);
            }
            loadFromFile();
        } catch (IOException e) {
            // For an educational example we print the stack trace; production code should use a logger
            e.printStackTrace();
        }
    }

    private synchronized void loadFromFile() throws IOException {
        storage.clear();
        if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) return;
        byte[] bytes = Files.readAllBytes(file);
        String content = new String(bytes, StandardCharsets.UTF_8).trim();
        if (content.isEmpty()) return;

        // Expecting a JSON array of objects: [ {...}, {...} ]
        // Very small parser: split top-level objects by finding braces.
        int idx = 0;
        while (idx < content.length()) {
            int open = content.indexOf('{', idx);
            if (open == -1) break;
            int close = findMatchingBrace(content, open);
            if (close == -1) break;
            String obj = content.substring(open, close + 1);
            Map<String, String> map = parseObject(obj);
            String id = map.get("id");
            String color = map.get("color");
            String ownerName = map.get("ownerName");
            if (id != null) {
                Fusca f = new Fusca(id, color, ownerName);
                storage.put(id, f);
            }
            idx = close + 1;
        }
    }

    private int findMatchingBrace(String s, int openIndex) {
        int depth = 0;
        for (int i = openIndex; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private Map<String, String> parseObject(String obj) {
        java.util.HashMap<String, String> map = new java.util.HashMap<>();
        Matcher m = FIELD_PATTERN.matcher(obj);
        while (m.find()) {
            String key = m.group(1);
            String val = unescapeJson(m.group(2));
            map.put(key, val);
        }
        return map;
    }

    private String unescapeJson(String s) {
        // Minimal unescape: handle common escapes (\" and \\ and \n and \r and \t)
        return s.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private synchronized void persist() throws IOException {
        // Build JSON array
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean first = true;
        for (Fusca f : storage.values()) {
            if (!first) sb.append(',');
            sb.append('{');
            sb.append("\"id\":\"").append(escapeJson(f.getId())).append("\"");
            sb.append(',');
            sb.append("\"color\":\"").append(escapeJson(f.getColor())).append("\"");
            sb.append(',');
            sb.append("\"ownerName\":\"").append(escapeJson(f.getOwnerName())).append("\"");
            sb.append('}');
            first = false;
        }
        sb.append(']');

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        Path tmp = file.resolveSibling(file.getFileName().toString() + ".tmp");
        Files.write(tmp, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        try {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ex) {
            // If atomic move not supported, fallback to non-atomic replace
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public void add(Fusca fusca) {
        storage.put(fusca.getId(), fusca);
        try {
            persist();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeById(String id) {
        Fusca removed = storage.remove(id);
        if (removed == null) return false;
        try {
            persist();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean update(Fusca fusca) {
        if (!storage.containsKey(fusca.getId())) return false;
        storage.put(fusca.getId(), fusca);
        try {
            persist();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Fusca findById(String id) {
        return storage.get(id);
    }

    @Override
    public List<Fusca> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Fusca> findByColor(String color) {
        ArrayList<Fusca> list = new ArrayList<>();
        if (color == null) return list;
        for (Fusca f : storage.values()) {
            String c = f.getColor();
            if (c != null && c.equalsIgnoreCase(color)) list.add(f);
        }
        return list;
    }
}
