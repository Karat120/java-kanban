import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) continue;

                String[] fields = line.split(",");
                TaskType type = TaskType.valueOf(fields[1]);

                // Предзагрузка эпиков
                if (type == TaskType.EPIC) {
                    Epic epic = (Epic) fromString(line, epicById);
                    manager.addEpic(epic);
                    epicById.put(epic.getTaskId(), epic);
                }
            }


            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) continue;

                String[] fields = line.split(",");
                TaskType type = TaskType.valueOf(fields[1]);

                if (type == TaskType.TASK) {
                    Task task = (Task) fromString(line, epicById);
                    manager.addTask(task);
                } else if (type == TaskType.SUBTASK) {
                    Subtask subtask = (Subtask) fromString(line, epicById);
                    manager.addSubTask(subtask);
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

    private static Task fromString(String line, Map<Integer, Epic> epicById) {
        String[] fields = line.split(",");

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Statuc status = Statuc.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setTaskId(id);
                return task;

            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setTaskId(id);
                return epic;

            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                Epic epicRef = epicById.get(epicId);
                if (epicRef == null) {
                    throw new IllegalArgumentException("Эпик с id=" + epicId + " не найден для подзадачи");
                }
                Subtask subtask = new Subtask(epicRef, name, description, status);
                subtask.setTaskId(id);
                return subtask;

            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    private String toString(Task task) {
        TaskType type;
        if (task instanceof Epic) {
            type = TaskType.EPIC;
        } else if (task instanceof Subtask) {
            type = TaskType.SUBTASK;
        } else {
            type = TaskType.TASK;
        }

        String epicId = "";
        if (task instanceof Subtask) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        return String.join(",",
                String.valueOf(task.getTaskId()),
                type.name(),
                task.getTaskName(),
                task.getTaskStatus().name(),
                task.getTaskDescriptionl(),
                epicId);
    }
}