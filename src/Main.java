import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // Создаём эпик
        Epic epic1 = new Epic("Переезд", "Организация переезда в новый офис");
        manager.addEpic(epic1);

        // Добавляем подзадачи к эпику с временем
        Subtask subtask1 = new Subtask(epic1, "Упаковать вещи", "Упаковать компьютеры и документы", Statuc.NEW);
        subtask1.setStartTime(LocalDateTime.of(2023, 6, 1, 10, 0));
        subtask1.setDuration(Duration.ofHours(3));
        manager.addSubTask(subtask1);

        Subtask subtask2 = new Subtask(epic1, "Нанять грузчиков", "Найти через Avito", Statuc.NEW);
        subtask2.setStartTime(LocalDateTime.of(2023, 6, 1, 14, 0));
        subtask2.setDuration(Duration.ofHours(2));
        manager.addSubTask(subtask2);

        // Создаём обычные задачи с временем
        Task task1 = new Task("Помыть посуду", "Помыть всю посуду вечером", Statuc.NEW);
        task1.setStartTime(LocalDateTime.of(2023, 6, 1, 18, 0));
        task1.setDuration(Duration.ofMinutes(45));
        manager.addTask(task1);

        Task task2 = new Task("Купить продукты", "Купить продукты на неделю", Statuc.NEW);
        task2.setStartTime(LocalDateTime.of(2023, 6, 2, 9, 0));
        task2.setDuration(Duration.ofHours(1));
        manager.addTask(task2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        // Выводим обычные задачи
        System.out.println("Обычные задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.printf("ID: %d, Название: %s, Статус: %s, Начало: %s, Конец: %s, Длительность: %s%n",
                    task.getTaskId(),
                    task.getTaskName(),
                    task.getTaskStatus(),
                    formatDate(task.getStartTime(), formatter),
                    formatDate(task.getEndTime(), formatter),
                    formatDuration(task.getDuration()));
        }

        // Выводим эпики и их подзадачи
        System.out.println("\nЭпики:");
        for (Epic epic : manager.getAllEpic()) {
            System.out.printf("ID: %d, Название: %s, Статус: %s, Начало: %s, Конец: %s, Длительность: %s%n",
                    epic.getTaskId(),
                    epic.getTaskName(),
                    epic.getTaskStatus(),
                    formatDate(epic.getStartTime(), formatter),
                    formatDate(epic.getEndTime(), formatter),
                    formatDuration(epic.getDuration()));

            System.out.println("  Подзадачи:");
            for (Integer subtaskId : epic.getListSubTaskId()) {
                Subtask subtask = manager.getSubTaskById(subtaskId);
                System.out.printf("    ID: %d, Название: %s, Статус: %s, Начало: %s, Конец: %s, Длительность: %s%n",
                        subtask.getTaskId(),
                        subtask.getTaskName(),
                        subtask.getTaskStatus(),
                        formatDate(subtask.getStartTime(), formatter),
                        formatDate(subtask.getEndTime(), formatter),
                        formatDuration(subtask.getDuration()));
            }
        }
    }

    private static String formatDate(java.time.LocalDateTime dateTime, DateTimeFormatter formatter) {
        return dateTime != null ? dateTime.format(formatter) : "(не задано)";
    }

    private static String formatDuration(java.time.Duration duration) {
        if (duration == null) return "(не задано)";
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%d ч %d мин", hours, minutes);
    }
}
