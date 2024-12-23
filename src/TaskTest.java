import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {
    public TaskTest() {
    }

    @Test
    void testTasksEqualIfIdsEqual() {
        Task task1 = new Task("Task1", "Description1", Statuc.NEW);
        Task task2 = new Task("Task2", "Description2", Statuc.NEW);
        task1.setTaskId(1);
        task2.setTaskId(1);
        boolean actually = task1.equals(task2);
        Assertions.assertTrue(actually);
    }

    @Test
    void testSubtasksEqualIfIdsEqual() {
        Epic epic = new Epic("TaskName", "TaskDescription");
        Subtask subtask1 = new Subtask(epic, "Subtask1", "Description1", Statuc.NEW);
        Subtask subtask2 = new Subtask(epic, "Subtask2", "Description2", Statuc.NEW);
        subtask1.setTaskId(1);
        subtask2.setTaskId(1);
        boolean actually = subtask1.equals(subtask2);
        Assertions.assertTrue(actually);
    }

    @Test
    void testEpicsEqualIfIdsEqual() {
        Epic epic1 = new Epic("TaskName", "TaskDescription");
        Epic epic2 = new Epic("TaskName1", "TaskDescription2");
        epic1.setTaskId(1);
        epic2.setTaskId(1);
        boolean actually = epic1.equals(epic2);
        Assertions.assertTrue(actually);
    }

    @Test
    void testManagersReturnInitializedInstances() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(taskManager, "TaskManager should be initialized");
        Assertions.assertNotNull(historyManager, "HistoryManager should be initialized");
    }

    @Test
    void testAddAndRetrieveTask() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Task", "Description", Statuc.NEW);
        taskManager.addTask(task);
        Task retrievedTask = taskManager.getTaskId(task.getTaskId());
        Assertions.assertEquals(task, retrievedTask, "Task should be retrievable by its ID");
    }

    @Test
    void testAddingTaskWithGeneratedId() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Task", "Description", Statuc.NEW);
        taskManager.addTask(task);
        Assertions.assertNotEquals(0, task.getTaskId(), "Generated task ID should not be 0");
    }

    @Test
    void testTaskImmutableAfterAddition() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Task", "Description", Statuc.NEW);
        task.setTaskStatus(Statuc.IN_PROGRESS);
        taskManager.addTask(task);
        Task retrievedTask = taskManager.getTaskId(task.getTaskId());
        Assertions.assertEquals(Statuc.IN_PROGRESS, retrievedTask.getTaskStatus(), "Task status should remain unchanged after addition");
    }

    @Test
    void testHistoryManagerKeepsTaskState() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Task", "Description", Statuc.NEW);
        task.setTaskId(1);
        historyManager.add(task);
        Assertions.assertEquals(1, historyManager.getHistory().size());
        Assertions.assertEquals(task, historyManager.getHistory().get(0));
    }

    @Test
    void testTaskIsImmutableAfterAddingToManager() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Приготовить кофе", "добавить сливки", Statuc.NEW);
        taskManager.addTask(task);
        String expected = "[Task{taskId=1, taskName='Приготовить кофе', taskDescriptionl='добавить сливки', taskStatus=NEW}]";
        String actually = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testTasksWithGivenAndGeneratedIdsDoNotConflict() {
        TaskManager taskManager = Managers.getDefault();
        Task taskWithGivenId = new Task("Task 1", "Description1", Statuc.NEW);
        taskWithGivenId.setTaskId(2);
        taskManager.addTask(taskWithGivenId);
        Task taskWithGeneratedId = new Task("Task 2", "Description2", Statuc.NEW);
        Task taskWithGeneratedId1 = new Task("Task 3", "Description2", Statuc.NEW);
        Task taskWithGeneratedId2 = new Task("Task 4", "Description3", Statuc.NEW);
        taskManager.addTask(taskWithGeneratedId);
        taskManager.addTask(taskWithGeneratedId1);
        taskManager.addTask(taskWithGeneratedId2);
        Set<Integer> taskIds = new HashSet();
        Iterator var7 = taskManager.getAllTasks().iterator();

        while(var7.hasNext()) {
            Task task = (Task)var7.next();
            Assertions.assertTrue(taskIds.add(task.getTaskId()), "ID задачи должен быть уникальным: " + task.getTaskId());
        }

        Assertions.assertEquals(taskManager.getAllTasks().size(), 4, "Количество задач должно быть 4");
    }

    @Test
    void testDeleteTaskById() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Task 1", "Description1", Statuc.NEW);
        taskManager.addTask(task);
        taskManager.deleteTaskById(task.getTaskId());
        Task fetchedTask = taskManager.getTaskId(task.getTaskId());
        Assertions.assertNull(fetchedTask, "Задача не была удалена");
    }

    @Test
    void testUpdateTask() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Task 1", "Description1", Statuc.NEW);
        taskManager.addTask(task);
        task.setTaskName("Updated Task");
        task.setTaskDescriptionl("Updated Description");
        task.setTaskStatus(Statuc.DONE);
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTaskId(task.getTaskId());
        Assertions.assertEquals("Updated Task", updatedTask.getTaskName(), "Название задачи не обновилось");
        Assertions.assertEquals("Updated Description", updatedTask.getTaskDescriptionl(), "Описание задачи не обновилось");
        Assertions.assertEquals(Statuc.DONE, updatedTask.getTaskStatus(), "Статус задачи не обновился");
    }

    @Test
    void testRemoveAllTasks() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Task 1", "Description1", Statuc.NEW);
        Task task2 = new Task("Task 2", "Description2", Statuc.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.removeAllTask();
        Assertions.assertTrue(taskManager.getAllTasks().isEmpty(), "Не все задачи были удалены");
    }


}
