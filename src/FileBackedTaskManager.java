import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Map<Integer, Epic> epicById = new HashMap<>();

        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            if (lines.size() < 2) return manager;

            // Загружаем эпики
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isBlank()) continue;
                String[] fields = line.split(",");
                if (fields.length < 2) continue;

                if (fields[1].equals("EPIC")) {
                    Task task = fromString(line, epicById);
                    if (task instanceof Epic epic) {
                        manager.addEpic(epic);
                        epicById.put(epic.getTaskId(), epic);
                        System.out.println("Загружен эпик: ID=" + epic.getTaskId() + ", имя=" + epic.getTaskName());
                    }
                }
            }

            // Загружаем задачи и подзадачи
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isBlank()) continue;
                String[] fields = line.split(",");
                if (fields.length < 2) continue;

                String type = fields[1];
                if (type.equals("TASK")) {
                    Task task = fromString(line, epicById);
                    if (task != null) {
                        manager.addTask(task);
                    }
                } else if (type.equals("SUBTASK")) {
                    Task task = fromString(line, epicById);
                    if (task instanceof Subtask subtask) {
                        if (epicById.containsKey(subtask.getEpicId())) {
                            manager.addSubTask(subtask);
                        } else {
                            System.err.println("Эпик не найден для подзадачи ID=" + subtask.getTaskId());
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }

        return manager;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(Subtask subtask) {
        super.addSubTask(subtask);
        save();
    }


    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicTaskById(int id) {
        super.deleteEpicTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void clearEpicSubTask() {
        super.clearEpicSubTask();
        save();
    }

    protected void save() {
        try {
            StringBuilder sb = new StringBuilder();
            // Добавляем заголовок
            sb.append("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                sb.append(toString(task)).append("\n");
            }

            for (Epic epic : getAllEpic()) {
                sb.append(toString(epic)).append("\n");
            }

            for (Subtask subtask : getAllSubtask()) {
                sb.append(toString(subtask)).append("\n");
            }

            Files.writeString(file.toPath(), sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

private String toString(Task task) {
    String type;
    String epicId = "";

    if (task instanceof Epic) {
        type = "EPIC";
    } else if (task instanceof Subtask) {
        type = "SUBTASK";
        epicId = String.valueOf(((Subtask) task).getEpicId());
    } else {
        type = "TASK";
    }

    return String.join(",",
            String.valueOf(task.getTaskId()),
            type,
            task.getTaskName(),
            task.getTaskStatus().name(),
            task.getTaskDescriptionl(),
            task.getStartTime() != null ? task.getStartTime().toString() : "",
            task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "",
            epicId
    );
}

    private static Task fromString(String line, Map<Integer, Epic> epicById) {
        String[] fields = line.split(",");

        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        Statuc status = Statuc.valueOf(fields[3]);
        String description = fields[4];

        LocalDateTime startTime = fields.length > 5 && !fields[5].isEmpty()
                ? LocalDateTime.parse(fields[5])
                : null;

        Duration duration = fields.length > 6 && !fields[6].isEmpty()
                ? Duration.ofMinutes(Long.parseLong(fields[6]))
                : null;

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status);
                task.setTaskId(id);
                task.setStartTime(startTime);
                task.setDuration(duration);
                return task;

            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setTaskId(id);
                return epic;

            case "SUBTASK":
                int epicId = fields.length > 7 ? Integer.parseInt(fields[7]) : -1;
                Epic epicRef = epicById.get(epicId);
                if (epicRef == null) {
                    System.err.println("Предупреждение: Эпик не найден для подзадачи " + id);
                    return null;
                }
                Subtask subtask = new Subtask(epicRef, name, description, status);
                subtask.setTaskId(id);
                subtask.setStartTime(startTime);
                subtask.setDuration(duration);
                return subtask;

            default:
                throw new ManagerSaveException("Неизвестный тип задачи: " + type);
        }
    }

}