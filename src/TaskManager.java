import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager { // блок управления
    private HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private  HashMap<Integer, Epic> epicHashMap = new HashMap<>();
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

    public Epic getEpicById(int id) {
        return epicHashMap.get(id);
    }

    public Task updateTask(Task task) {
        if(taskHashMap.containsKey(task.getTaskId())) {
            taskHashMap.replace(task.getTaskId(), task);
        }
        return  taskHashMap.get(task.getTaskId());
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

    public Epic updateEpic(Epic epic) {
        if(epicHashMap.containsKey(epic.getTaskId())) {
            epicHashMap.replace(epic.getTaskId(), epic);
        }
        return epicHashMap.get(epic.getTaskId());
    }

    private ArrayList<Subtask> getListSubTasksByEpicId(ArrayList<Integer> subTaskId) {
        ArrayList<Subtask> subtaskList = new ArrayList<>();

        for(Integer id : subTaskId) {
            subtaskList.add(subtaskHashMap.get(id));
        }
        return subtaskList;
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

    public void updateSubTask(Subtask subtask) {
        if(subtaskHashMap.containsKey(subtask.getTaskId())) {
            subtaskHashMap.replace(subtask.getTaskId(), subtask);
        }
        updateEpicStatus(epicHashMap.get(subtask.getEpicId()));
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

    public Subtask getSubTaskById(int id) {
        return subtaskHashMap.get(id);
    }

    public void clearEpicSubTask() {
        for (Epic epic : epicHashMap.values()) {
            epic.clearListSubTask();
        }
    }

    public void removeAllSubTasks() {
        clearEpicSubTask();
        subtaskHashMap.clear();
        for (Epic epic : epicHashMap.values()) {
            updateEpicStatus(epic);
            epic.clearListSubTask();
        }

    }

    public void removeSubTaskList(ArrayList<Integer> subTaskId) {
        for(Integer id : subTaskId) {
            subtaskHashMap.remove(id);
        }
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

    public void deleteTaskById(int id) {
        taskHashMap.remove(id);
    }

    public void removeAllTask() {
        taskHashMap.clear();
    }

    public void removeAllEpic() {
        epicHashMap.clear();
    }

}
