import java.util.HashMap;

public class TaskManager { // блок управления
    private HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private static int id = 1;

    private static int nextId() {
        return  id++;
    }

    public void addTask(Task task) {
        task.setTaskId(nextId());
        taskHashMap.put(task.getTaskId(), task);
    }

    public Task getTaskId(int id) {
        return taskHashMap.get(id);
    }

    public void addEpic(Epic epic) {
        epic.setTaskId(nextId());
        epicHashMap.put(epic.getTaskId(), epic);
    }

    public Epic getEpicId(int id) {
        return epicHashMap.get(id);
    }

    






}
