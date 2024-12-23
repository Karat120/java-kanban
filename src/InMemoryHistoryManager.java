import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> taskHistory = new ArrayList();

    public InMemoryHistoryManager() {
    }

    public void add(Task task) {
        if (task != null) {
            this.taskHistory.add(task);
            if (this.taskHistory.size() > 10) {
                this.taskHistory.remove(0);
            }
        }

    }

    public List<Task> getHistory() {
        return new ArrayList(this.taskHistory);
    }
}
