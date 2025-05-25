import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");

        System.out.println("=== Тест 1: Создание и сохранение задач ===");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Epic epic = new Epic("Переезд", "Организация переезда в новый офис");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(epic, "Упаковать вещи", "Упаковать компьютеры и документы", Statuc.NEW);
        Subtask subtask2 = new Subtask(epic, "Нанять грузчиков", "Найти через Avito", Statuc.NEW);
        manager.addSubTask(subtask1);
        manager.addSubTask(subtask2);

        Task task = new Task("Помыть посуду", "Помыть всю посуду вечером", Statuc.NEW);
        manager.addTask(task);

        printAllTasks(manager);

        System.out.println("\n=== Тест 2: Загрузка из файла ===");
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        printAllTasks(loadedManager);


    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\nВсе задачи:");
        System.out.println("Обычные: " + manager.getAllTasks());
        System.out.println("Эпики: " + manager.getAllEpic());
        System.out.println("Подзадачи: " + manager.getAllSubtask());

        System.out.println("\nПодробный вывод:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        for (Epic epic : manager.getAllEpic()) {
            System.out.println(epic);
            for (Integer subtaskId : epic.getListSubTaskId()) {
                System.out.println("  " + manager.getSubTaskById(subtaskId));
            }
        }
    }
}