import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    HistoryManager histryManager = Managers.getDefaultHistory();
    private HashMap<Integer, Task> taskHashMap = new HashMap();
    private HashMap<Integer, Epic> epicHashMap = new HashMap();
    private HashMap<Integer, Subtask> subtaskHashMap = new HashMap();
    private int id = 1;

    public InMemoryTaskManager() {
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean isTasksIntersect(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null
                || task1.getDuration() == null || task2.getDuration() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !(end1.isBefore(start2) && !(end2.isBefore(start1)));
    }

    @Override
    public boolean hasTimeIntersections(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .filter(task -> task.getTaskId() != newTask.getTaskId()) // Исключаем саму задачу при обновлении
                .anyMatch(existingTask -> isTasksIntersect(existingTask, newTask));
    }


    public void addTask(Task task) {
        if (hasTimeIntersections(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей");
        }
        task.setTaskId(this.nextId());
        this.taskHashMap.put(task.getTaskId(), task);
        addToPrioritized(task);
    }

    public void updateTask(Task task) {
        if (hasTimeIntersections(task)) {
            throw new ManagerSaveException("Обновленная задача пересекается по времени с существующей");
        }
        if (this.taskHashMap.containsKey(task.getTaskId())) {
            removeFromPrioritized(taskHashMap.get(task.getTaskId()));
            this.taskHashMap.replace(task.getTaskId(), task);
            addToPrioritized(task);
        }
    }

    private void removeFromPrioritized(Task task) {
        prioritizedTasks.remove(task);
    }

    public void addSubTask(Subtask subtask) {
        // Проверка пересечений по времени
        if (hasTimeIntersections(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующими задачами");
        }

        subtask.setTaskId(this.nextId());
        Epic epic = this.getEpicById(subtask.getEpicId());

        if (epic != null) {
            epic.addSubTaskId(subtask);
            this.subtaskHashMap.put(subtask.getTaskId(), subtask);
            addToPrioritized(subtask); // Добавляем в отсортированный список
            this.updateEpicStatus(epic);
            epic.updateTime(this); // Обновляем время эпика
        }
    }

    public void updateSubTask(Subtask subtask) {
        // Проверка пересечений по времени (исключая текущую задачу)
        if (hasTimeIntersections(subtask)) {
            throw new ManagerSaveException("Обновленная подзадача пересекается по времени с другими задачами");
        }

        if (this.subtaskHashMap.containsKey(subtask.getTaskId())) {
            Subtask oldSubtask = this.subtaskHashMap.get(subtask.getTaskId());
            removeFromPrioritized(oldSubtask); // Удаляем старую версию
            this.subtaskHashMap.replace(subtask.getTaskId(), subtask);
            addToPrioritized(subtask); // Добавляем обновленную версию

            Epic epic = this.getEpicById(subtask.getEpicId());
            if (epic != null) {
                this.updateEpicStatus(epic);
                epic.updateTime(this); // Обновляем время эпика
            }
        }
    }


    public List<Task> getHistory() {
        return this.histryManager.getHistory();
    }

    public void addEpic(Epic epic) {
        epic.setTaskId(this.nextId());
        this.epicHashMap.put(epic.getTaskId(), epic);
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

    public List<Subtask> getListSubTasksByEpicId(List<Integer> subTaskId) {
        return subTaskId.stream()
                .map(subtaskHashMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Subtask getSubTaskById(int id) {
        Subtask subtask = (Subtask) this.subtaskHashMap.get(id);
        this.histryManager.add(subtask);
        return subtask;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(taskHashMap.values());
    }

    public List<Epic> getAllEpic() {
        return new ArrayList<>(epicHashMap.values());
    }


    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskHashMap.values());
    }

    public void updateEpic(Epic epic) {
        if (this.epicHashMap.containsKey(epic.getTaskId())) {
            this.epicHashMap.replace(epic.getTaskId(), epic);
            this.updateEpicStatus(epic);
        }

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
        epicHashMap.values().forEach(epic -> {
            this.updateEpicStatus(epic);
            epic.clearListSubTask();
        });
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
        epicHashMap.values().forEach(Epic::clearListSubTask);
    }

    private int nextId() {
        return this.id++;
    }

    private void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
    );


}
