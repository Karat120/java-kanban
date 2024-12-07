public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task taskOne = new Task("Учёба", "Выполнить задания в тренажере", Statuc.NEW);
        Task taskTwo = new Task("Отдых", "Выпить чай", Statuc.NEW);

        taskManager.addTask(taskOne);
        taskManager.addTask(taskTwo);

        System.out.println("Первая добавленная задача " + taskManager.getTaskId(taskOne.getTaskId()));
        System.out.println("Вторая добавленная задача " + taskManager.getTaskId(taskTwo.getTaskId()));

        taskOne.setTaskStatus(Statuc.IN_PROGRESS);
        taskTwo.setTaskStatus(Statuc.DONE);
        taskManager.updateTask(taskOne);
        taskManager.updateTask(taskTwo);
        System.out.println("\n\nИзмененый статус задач и вывод всех задач " + taskManager.getAllTasks());
        taskManager.deleteTaskById(taskOne.getTaskId());
        System.out.println("Удалена первая задача , вывод всех задач " + taskManager.getAllTasks());
        taskManager.removeAllTask();
        System.out.println("Удаление всех задач и попытка вывести " + taskManager.getAllTasks());

        Epic epicWithTwo = new Epic("Сходить в кино с девушкой", "Подготовиться");
        Epic epicWithOne = new Epic("Уборка", "Уборка квартиры");

        taskManager.addEpic(epicWithTwo);
        taskManager.addEpic(epicWithOne);

        Subtask subtaskFirst = new Subtask(epicWithTwo, "Купить цветы",
                "Зайти в цветочный у дома и купить розы", Statuc.NEW);
        Subtask subtaskSecond = new Subtask(epicWithTwo, "Помыться",
                "Купить мыло с приятным ароматом", Statuc.NEW);
        Subtask subtaskEpicWithOne = new Subtask(epicWithOne, "Покупка хоз. средств",
                "купить в магазине хоз.средства", Statuc.NEW);

        taskManager.addSubTask(subtaskFirst);
        taskManager.addSubTask(subtaskSecond);
        taskManager.addSubTask(subtaskEpicWithOne);

        System.out.println("\nПервый Эпик с двумя subtask " + taskManager.getEpicById(epicWithTwo.getTaskId()) + "\t"
                            + taskManager.getSubTaskById(subtaskFirst.getTaskId()) + "\t"
                            + taskManager.getSubTaskById(subtaskSecond.getTaskId()));
        System.out.println("\nВторой эпик с одной subtask " +taskManager.getEpicById(epicWithOne.getTaskId()) + "\t"
                            + taskManager.getSubTaskById(subtaskEpicWithOne.getTaskId()));

        subtaskFirst.setTaskStatus(Statuc.DONE);
        taskManager.updateSubTask(subtaskFirst);

        subtaskSecond.setTaskStatus(Statuc.DONE);
        taskManager.updateSubTask(subtaskSecond);

        subtaskEpicWithOne.setTaskStatus(Statuc.IN_PROGRESS);
        taskManager.updateSubTask(subtaskEpicWithOne);
        System.out.println("\nПроверка на изменение статусов подзадач и расчёта статуса эпика");
        System.out.println("Весь список подзадач" + taskManager.getAllSubtask());
        System.out.println("Весь список эпиков " + taskManager.getAllEpic());

        //удаление subtask and epic
        taskManager.deleteSubTaskById(subtaskFirst.getTaskId());
        System.out.println("\nСписок подзадач (удалена subtask name 'купить цветы') " + taskManager.getAllSubtask());

        epicWithTwo.setTaskName("Быть дома");
        taskManager.updateEpic(epicWithTwo);

        System.out.println("Проверка изменение названия epic 'Было сходить в кино с девушкой' изменено на 'быть дома '"
                            + taskManager.getEpicById(epicWithTwo.getTaskId()));

        //удаление всех subtask
        System.out.println("\nSubtask до удаления " + taskManager.getAllSubtask());
        taskManager.removeAllSubTasks();
        System.out.println("Subtask после удаления вызов метода getAllSubtask " + taskManager.getAllSubtask());
        System.out.println("Изменяется статус epic после удаления subtask, вывод эпиков " + taskManager.getAllEpic());

    }
}
