import java.io.PrintStream;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task taskOne = new Task("Учёба", "Выполнить задания в тренажере", Statuc.NEW);
        Task taskTwo = new Task("Отдых", "Выпить чай", Statuc.NEW);
        taskManager.addTask(taskOne);
        taskManager.addTask(taskTwo);
        PrintStream var10000 = System.out;
        Task var10001 = taskManager.getTaskId(taskOne.getTaskId());
        var10000.println("Первая добавленная задача " + String.valueOf(var10001));
        var10000 = System.out;
        var10001 = taskManager.getTaskId(taskTwo.getTaskId());
        var10000.println("Вторая добавленная задача " + String.valueOf(var10001));
        taskOne.setTaskStatus(Statuc.IN_PROGRESS);
        taskTwo.setTaskStatus(Statuc.DONE);
        taskManager.updateTask(taskOne);
        taskManager.updateTask(taskTwo);
        System.out.println("\n\nИзмененый статус задач и вывод всех задач " + String.valueOf(taskManager.getAllTasks()));
        taskManager.deleteTaskById(taskOne.getTaskId());
        System.out.println("Удалена первая задача , вывод всех задач " + String.valueOf(taskManager.getAllTasks()));
        taskManager.removeAllTask();
        System.out.println("Удаление всех задач и попытка вывести " + String.valueOf(taskManager.getAllTasks()));
        Epic epicWithTwo = new Epic("Сходить в кино с девушкой", "Подготовиться");
        Epic epicWithOne = new Epic("Уборка", "Уборка квартиры");
        taskManager.addEpic(epicWithTwo);
        taskManager.addEpic(epicWithOne);
        Subtask subtaskFirst = new Subtask(epicWithTwo, "Купить цветы", "Зайти в цветочный у дома и купить розы", Statuc.NEW);
        Subtask subtaskSecond = new Subtask(epicWithTwo, "Помыться", "Купить мыло с приятным ароматом", Statuc.NEW);
        Subtask subtaskEpicWithOne = new Subtask(epicWithOne, "Покупка хоз. средств", "купить в магазине хоз.средства", Statuc.NEW);
        taskManager.addSubTask(subtaskFirst);
        taskManager.addSubTask(subtaskSecond);
        taskManager.addSubTask(subtaskEpicWithOne);
        var10000 = System.out;
        String var9 = String.valueOf(taskManager.getEpicById(epicWithTwo.getTaskId()));
        var10000.println("\nПервый Эпик с двумя subtask " + var9 + "\t" + String.valueOf(taskManager.getSubTaskById(subtaskFirst.getTaskId())) + "\t" + String.valueOf(taskManager.getSubTaskById(subtaskSecond.getTaskId())));
        var10000 = System.out;
        var9 = String.valueOf(taskManager.getEpicById(epicWithOne.getTaskId()));
        var10000.println("\nВторой эпик с одной subtask " + var9 + "\t" + String.valueOf(taskManager.getSubTaskById(subtaskEpicWithOne.getTaskId())));
        subtaskFirst.setTaskStatus(Statuc.DONE);
        taskManager.updateSubTask(subtaskFirst);
        subtaskSecond.setTaskStatus(Statuc.DONE);
        taskManager.updateSubTask(subtaskSecond);
        subtaskEpicWithOne.setTaskStatus(Statuc.IN_PROGRESS);
        taskManager.updateSubTask(subtaskEpicWithOne);
        System.out.println("\nПроверка на изменение статусов подзадач и расчёта статуса эпика");
        System.out.println("Весь список подзадач" + String.valueOf(taskManager.getAllSubtask()));
        System.out.println("Весь список эпиков " + String.valueOf(taskManager.getAllEpic()));
        taskManager.deleteSubTaskById(subtaskFirst.getTaskId());
        System.out.println("\nСписок подзадач (удалена subtask name 'купить цветы') " + String.valueOf(taskManager.getAllSubtask()));
        epicWithTwo.setTaskName("Быть дома");
        taskManager.updateEpic(epicWithTwo);
        var10000 = System.out;
        Epic var10 = taskManager.getEpicById(epicWithTwo.getTaskId());
        var10000.println("Проверка изменение названия epic 'Было сходить в кино с девушкой' изменено на 'быть дома '" + String.valueOf(var10));
        System.out.println("\nSubtask до удаления " + String.valueOf(taskManager.getAllSubtask()));
        taskManager.removeAllSubTasks();
        System.out.println("Subtask после удаления вызов метода getAllSubtask " + String.valueOf(taskManager.getAllSubtask()));
        System.out.println("Изменяется статус epic после удаления subtask, вывод эпиков " + String.valueOf(taskManager.getAllEpic()));
        System.out.println("История задач: " + String.valueOf(taskManager.getHistory()));


    }
}
