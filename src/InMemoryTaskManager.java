import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    HistoryManager histryManager = Managers.getDefaultHistory();
    private HashMap<Integer, Task> taskHashMap = new HashMap();
    private HashMap<Integer, Epic> epicHashMap = new HashMap();
    private HashMap<Integer, Subtask> subtaskHashMap = new HashMap();
    private int id = 1;

    public InMemoryTaskManager() {
    }

    public List<Task> getHistory() {
        return this.histryManager.getHistory();
    }

    public void addTask(Task task) {
        task.setTaskId(this.nextId());
        this.taskHashMap.put(task.getTaskId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setTaskId(this.nextId());
        this.epicHashMap.put(epic.getTaskId(), epic);
    }

    public void addSubTask(Subtask subtask) {
        subtask.setTaskId(this.nextId());
        Epic epic = this.getEpicById(subtask.getEpicId());
        if (epic != null) {
            epic.addSubTaskId(subtask);
            this.subtaskHashMap.put(subtask.getTaskId(), subtask);
            this.updateEpicStatus((Epic) this.epicHashMap.get(subtask.getEpicId()));
        }

    }

    public Task getTaskId(int id) {
        Task task = (Task) this.taskHashMap.get(id);
        this.histryManager.add(task);
        return task;
    }

    public Epic getEpicById(int id) {
        Epic epic = (Epic) this.epicHashMap.get(id);
        this.histryManager.add(epic);
        return epic;
    }

    public ArrayList<Subtask> getListSubTasksByEpicId(ArrayList<Integer> subTaskId) {
        ArrayList<Subtask> subtaskList = new ArrayList();
        Iterator var3 = subTaskId.iterator();

        while (var3.hasNext()) {
            Integer id = (Integer) var3.next();
            subtaskList.add((Subtask) this.subtaskHashMap.get(id));
        }

        return subtaskList;
    }

    public Subtask getSubTaskById(int id) {
        Subtask subtask = (Subtask) this.subtaskHashMap.get(id);
        this.histryManager.add(subtask);
        return subtask;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList();
        Iterator var2 = this.taskHashMap.values().iterator();

        while (var2.hasNext()) {
            Task task = (Task) var2.next();
            allTasks.add(task);
        }

        return allTasks;
    }

    public ArrayList<Epic> getAllEpic() {
        ArrayList<Epic> epics = new ArrayList();
        Iterator var2 = this.epicHashMap.values().iterator();

        while (var2.hasNext()) {
            Epic epic = (Epic) var2.next();
            epics.add(epic);
        }

        return epics;
    }

    public ArrayList<Subtask> getAllSubtask() {
        ArrayList<Subtask> subtasks = new ArrayList();
        Iterator var2 = this.subtaskHashMap.values().iterator();

        while (var2.hasNext()) {
            Subtask subtask = (Subtask) var2.next();
            subtasks.add(subtask);
        }

        return subtasks;
    }

    public void updateTask(Task task) {
        if (this.taskHashMap.containsKey(task.getTaskId())) {
            this.taskHashMap.replace(task.getTaskId(), task);
        }

    }

    public void updateEpic(Epic epic) {
        if (this.epicHashMap.containsKey(epic.getTaskId())) {
            this.epicHashMap.replace(epic.getTaskId(), epic);
            this.updateEpicStatus(epic);
        }

    }

    public void updateSubTask(Subtask subtask) {
        if (this.subtaskHashMap.containsKey(subtask.getTaskId())) {
            this.subtaskHashMap.replace(subtask.getTaskId(), subtask);
        }

        this.updateEpicStatus((Epic) this.epicHashMap.get(subtask.getEpicId()));
    }

    public void deleteTaskById(int id) {
        this.taskHashMap.remove(id);
    }

    public void deleteEpicTaskById(int id) {
        Epic epic = (Epic) this.epicHashMap.get(id);
        epic.removeSubTaskById(id);
        this.subtaskHashMap.remove(id);
        this.updateEpicStatus(epic);
    }

    public void deleteSubTaskById(int id) {
        Epic epic = (Epic) this.epicHashMap.get(((Subtask) this.subtaskHashMap.get(id)).getEpicId());
        epic.removeSubTaskById(id);
        this.subtaskHashMap.remove(id);
        this.updateEpicStatus(epic);
    }

    public void removeAllTask() {
        this.taskHashMap.clear();
    }

    public void removeAllEpic() {
        this.removeAllSubTasks();
        this.epicHashMap.clear();
    }

    public void removeAllSubTasks() {
        this.clearEpicSubTask();
        this.subtaskHashMap.clear();
        Iterator var1 = this.epicHashMap.values().iterator();

        while (var1.hasNext()) {
            Epic epic = (Epic) var1.next();
            this.updateEpicStatus(epic);
            epic.clearListSubTask();
        }

    }

    private int nextId() {
        return this.id++;
    }

    public void updateEpicStatus(Epic epic) {
        if (epic != null) {
            ArrayList<Integer> subTaskIds = epic.getListSubTaskId();
            if (epic.getListSubTaskId().isEmpty()) {
                epic.setTaskStatus(Statuc.NEW);
            } else {
                ArrayList<Subtask> subtasks = new ArrayList();
                Iterator var4 = subTaskIds.iterator();

                while (var4.hasNext()) {
                    Integer subtaskId = (Integer) var4.next();
                    Subtask subtask = (Subtask) this.subtaskHashMap.get(subtaskId);
                    if (subtask != null) {
                        subtasks.add(subtask);
                    }
                }

                if (subtasks.isEmpty()) {
                    epic.setTaskStatus(Statuc.NEW);
                } else {
                    boolean hasInProgress = true;
                    boolean allDone = true;
                    Iterator var10 = subtasks.iterator();

                    while (var10.hasNext()) {
                        Subtask subtask = (Subtask) var10.next();
                        if (subtask.getTaskStatus() != Statuc.NEW) {
                            hasInProgress = false;
                        }

                        if (subtask.getTaskStatus() != Statuc.DONE) {
                            allDone = false;
                        }
                    }

                    if (allDone) {
                        epic.setTaskStatus(Statuc.DONE);
                    } else if (hasInProgress) {
                        epic.setTaskStatus(Statuc.NEW);
                    } else {
                        epic.setTaskStatus(Statuc.IN_PROGRESS);
                    }

                }
            }
        }
    }

    public void clearEpicSubTask() {
        Iterator var1 = this.epicHashMap.values().iterator();

        while (var1.hasNext()) {
            Epic epic = (Epic) var1.next();
            epic.clearListSubTask();
        }

    }
}
