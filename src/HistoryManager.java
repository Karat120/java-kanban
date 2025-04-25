import java.util.List;

public interface HistoryManager {
    void add(Task var1);

    void remove(int id);

    List<Task> getHistory();
}

