import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    public TaskTest() {
    }
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void Original() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
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
        assertEquals(task, retrievedTask, "Task should be retrievable by its ID");
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
        assertEquals(Statuc.IN_PROGRESS, retrievedTask.getTaskStatus(), "Task status should remain unchanged after addition");
    }

    @Test
    void testHistoryManagerKeepsTaskState() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Task", "Description", Statuc.NEW);
        task.setTaskId(1);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));
    }

    @Test
    void testTaskIsImmutableAfterAddingToManager() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Приготовить кофе", "добавить сливки", Statuc.NEW);
        taskManager.addTask(task);
        String expected = "[Task{taskId=1, taskName='Приготовить кофе', taskDescriptionl='добавить сливки', taskStatus=NEW}]";
        String actually = taskManager.getAllTasks().toString();
        assertEquals(expected, actually);
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

        assertEquals(taskManager.getAllTasks().size(), 4, "Количество задач должно быть 4");
    }

    @Test
    void testDeleteTaskById() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Task 1", "Description1", Statuc.NEW);
        taskManager.addTask(task);
        taskManager.deleteTaskById(task.getTaskId());
        Task fetchedTask = taskManager.getTaskId(task.getTaskId());
        assertNull(fetchedTask, "Задача не была удалена");
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
        assertEquals("Updated Task", updatedTask.getTaskName(), "Название задачи не обновилось");
        assertEquals("Updated Description", updatedTask.getTaskDescriptionl(), "Описание задачи не обновилось");
        assertEquals(Statuc.DONE, updatedTask.getTaskStatus(), "Статус задачи не обновился");
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

    // Новые тесты для HistoryManager
    @Test
    void testHistoryManagerShouldNotContainDuplicates() {
        Task task = new Task("Task 1", "Description1", Statuc.NEW);
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size(), "История содержит дубликаты");
    }

@Test
void testHistoryShouldPreserveTaskOrder() {
    Task task1 = new Task("First", "Desc", Statuc.NEW);
    task1.setTaskId(1);

    Task task2 = new Task("Second", "Desc", Statuc.NEW);
    task2.setTaskId(2);

    historyManager.add(task1);
    historyManager.add(task2);

    List<Task> history = historyManager.getHistory();
    assertEquals(2, history.size());
    assertEquals(1, history.get(0).getTaskId());  // Проверяем по ID
    assertEquals(2, history.get(1).getTaskId());
}
    // Тесты целостности данных
    @Test
    void testSubtaskShouldNotExistWithoutEpic() {
        Epic epic = new Epic("Task 1", "Epic 1");
        Subtask subtask = new Subtask(epic, "Subtask 1", "d", Statuc.NEW);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subtask);

        taskManager.deleteEpicTaskById(epic.getTaskId());
        assertNull(taskManager.getSubTaskById(subtask.getTaskId()), "Подзадача осталась после удаления эпика");
    }

    @Test
    void testTaskFieldsModificationShouldBeConsistent() {
        Task task = new Task("Task 1", "Description1", Statuc.NEW);
        taskManager.addTask(task);
        task.setTaskName("modified");
        assertEquals("modified", taskManager.getTaskId(1).getTaskName(),
                "Изменение имени задачи не отразилось в менеджере");
    }

    // Тесты для сеттеров
    @Test
    void testChangingTaskNameShouldUpdateInManager() {
        Task task1 = new Task("Task 1", "Description1", Statuc.NEW);
        taskManager.addTask(task1);
        task1.setTaskName("Updated Name");
        assertEquals("Updated Name", taskManager.getTaskId(1).getTaskName());
    }

    @Test
    void testShouldPreventDuplicateIds() {
        Task firstTask = new Task("First", "Desc", Statuc.NEW);
        firstTask.setTaskId(1);
        taskManager.addTask(firstTask);

        Task duplicateTask = new Task("Duplicate", "Desc", Statuc.NEW);
        duplicateTask.setTaskId(1);

        // Проверяем, что вторая задача не заменила первую
        taskManager.addTask(duplicateTask);
        Task storedTask = taskManager.getTaskId(1);

        assertNotEquals("Duplicate", storedTask.getTaskName(),
                "Дубликат не должен заменять оригинальную задачу");
        assertEquals("First", storedTask.getTaskName());
    }
}



