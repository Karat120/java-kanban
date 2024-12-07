import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager { // блок управления
    private HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private  HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private int id = 1;

    public void addTask(Task task) {
        task.setTaskId(nextId());
        taskHashMap.put(task.getTaskId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setTaskId(nextId());
        epicHashMap.put(epic.getTaskId(), epic);

    }

    public void addSubTask(Subtask subtask) {
        subtask.setTaskId(nextId());
        Epic epic = getEpicById(subtask.getEpicId());

        if(epic != null) {
            epic.addSubTaskId(subtask);

            subtaskHashMap.put(subtask.getTaskId(), subtask);
            updateEpicStatus(epicHashMap.get(subtask.getEpicId()));
        }
    }
    public Task getTaskId(int id) {
        return taskHashMap.get(id);
    }
    public Epic getEpicById(int id) {
        return epicHashMap.get(id);
    }

    public ArrayList<Subtask> getListSubTasksByEpicId(ArrayList<Integer> subTaskId) {
        ArrayList<Subtask> subtaskList = new ArrayList<>();

        for(Integer id : subTaskId) {
            subtaskList.add(subtaskHashMap.get(id));
        }
        return subtaskList;
    }

    public Subtask getSubTaskById(int id) {
        return subtaskHashMap.get(id);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();

        for(Task task : taskHashMap.values()) {
            allTasks.add(task);
        }
        return  allTasks;
    }

    public ArrayList<Epic> getAllEpic() {
        ArrayList<Epic> epics = new ArrayList<>();
        for (Epic epic : epicHashMap.values()) {
            epics.add(epic);
        }
        return epics;
    }

    public ArrayList<Subtask> getAllSubtask() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for(Subtask subtask : subtaskHashMap.values()) {
            subtasks.add(subtask);
        }
        return subtasks;
    }

    public void updateTask(Task task) {
        if(taskHashMap.containsKey(task.getTaskId())) {
            taskHashMap.replace(task.getTaskId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if(epicHashMap.containsKey(epic.getTaskId())) {
            epicHashMap.replace(epic.getTaskId(), epic);
            updateEpicStatus(epic);
        }
    }

    public void updateSubTask(Subtask subtask) {
        if(subtaskHashMap.containsKey(subtask.getTaskId())) {
            subtaskHashMap.replace(subtask.getTaskId(), subtask);
        }
        updateEpicStatus(epicHashMap.get(subtask.getEpicId()));
    }

    public void deleteTaskById(int id) {
        taskHashMap.remove(id);
    }

    public void deleteEpicTaskById(int id) {
        Epic epic = epicHashMap.get(id);
        epic.removeSubTaskById(id);
        subtaskHashMap.remove(id);
        updateEpicStatus(epic);
    }

    public void deleteSubTaskById(int id) {
        Epic epic = epicHashMap.get(subtaskHashMap.get(id).getEpicId());
        epic.removeSubTaskById(id);
        subtaskHashMap.remove(id);
        updateEpicStatus(epic);
    }

    public void removeAllTask() {
        taskHashMap.clear();
    }

    public void removeAllEpic() {
        removeAllSubTasks();
        epicHashMap.clear();
    }
    public void removeAllSubTasks() {
        clearEpicSubTask();
        subtaskHashMap.clear();
        for (Epic epic : epicHashMap.values()) {
            updateEpicStatus(epic);
            epic.clearListSubTask();
        }
    }

    private int nextId() {
        return  id++;
    }

    private void updateEpicStatus(Epic epic) {
        if(epic == null) {
            return;
        }
        ArrayList<Integer> subTaskIds = epic.getListSubTaskId();
        if (epic.getListSubTaskId().isEmpty()) {
            epic.setTaskStatus(Statuc.NEW);
            return;
        }

        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Integer subtaskId : subTaskIds) {
            Subtask subtask = subtaskHashMap.get(subtaskId);
            if (subtask != null) {
                subtasks.add(subtask);
            }
        }

        if(subtasks.isEmpty()) {
            epic.setTaskStatus(Statuc.NEW);
            return;
        }

        boolean hasInProgress = true;
        boolean allDone = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getTaskStatus() != Statuc.NEW) {
                hasInProgress = false;
            } if (subtask.getTaskStatus() != Statuc.DONE) {
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

    private void clearEpicSubTask() {
        for (Epic epic : epicHashMap.values()) {
            epic.clearListSubTask();
        }
    }

}
