import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    public void addEpic(Epic epic);

    public void addSubTask(Subtask subtask);

    public Task getTaskId(int id);

    public Epic getEpicById(int id);

    public ArrayList<Subtask> getListSubTasksByEpicId(ArrayList<Integer> subTaskId);

    public Subtask getSubTaskById(int id);
    public List<Task> getHistory();

    public ArrayList<Task> getAllTasks();
    public ArrayList<Epic> getAllEpic();

    public ArrayList<Subtask> getAllSubtask();

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


}
