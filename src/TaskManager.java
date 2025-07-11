import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    public void addEpic(Epic epic);

    public void addSubTask(Subtask subtask);

    public Task getTaskId(int id);

    public Epic getEpicById(int id);

    public List<Subtask> getListSubTasksByEpicId(List<Integer> subTaskId);

    public Subtask getSubTaskById(int id);

    public List<Task> getHistory();

    public List<Task> getAllTasks();

    public List<Epic> getAllEpic();

    public List<Subtask> getAllSubtask();

    public void updateTask(Task task);

    public void updateEpic(Epic epic);

    public void updateSubTask(Subtask subtask);

    public void deleteTaskById(int id);

    public void deleteEpicTaskById(int id);

    public void deleteSubTaskById(int id);

    public void removeAllTask();

    public void removeAllEpic();

    public void removeAllSubTasks();

    public void updateEpicStatus(Epic epic);

    public void clearEpicSubTask();

    public List<Task> getPrioritizedTasks();

    boolean isTasksIntersect(Task task1, Task task2);

    boolean hasTimeIntersections(Task newTask);

}
