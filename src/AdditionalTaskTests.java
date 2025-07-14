import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdditionalTaskTests {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
    }

    // Тесты для временных интервалов
    @Test
    void testTaskTimeIntersectionDetection() {
        Task task1 = new Task("Task 1", "Desc", Statuc.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(2));

        Task task2 = new Task("Task 2", "Desc", Statuc.NEW,
                LocalDateTime.of(2023, 1, 1, 11, 0), Duration.ofHours(1));

        taskManager.addTask(task1);
        assertThrows(ManagerSaveException.class, () -> taskManager.addTask(task2),
                "Должно быть исключение при пересечении времени задач");
    }

    @Test
    void testNoIntersectionForTasksWithNullTime() {
        Task task1 = new Task("Task 1", "Desc", Statuc.NEW);
        Task task2 = new Task("Task 2", "Desc", Statuc.NEW,
                LocalDateTime.now(), Duration.ofHours(1));

        assertDoesNotThrow(() -> {
            taskManager.addTask(task1);
            taskManager.addTask(task2);
        }, "Не должно быть исключения для задач без времени");
    }

    // Тесты для Epic времени
    @Test
    void testEpicTimeCalculation() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);

        // Создаем подзадачи
        Subtask subtask1 = new Subtask(epic, "Sub 1", "Desc", Statuc.NEW);
        subtask1.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        subtask1.setDuration(Duration.ofHours(2));

        Subtask subtask2 = new Subtask(epic, "Sub 2", "Desc", Statuc.NEW);
        subtask2.setStartTime(LocalDateTime.of(2023, 1, 1, 13, 0));
        subtask2.setDuration(Duration.ofHours(1));

        // Добавляем подзадачи через менеджер
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);

        // Проверяем расчет времени эпика
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 0), epic.getStartTime(),
                "Неверное время начала эпика");
        assertEquals(LocalDateTime.of(2023, 1, 1, 14, 0), epic.getEndTime(),
                "Неверное время окончания эпика");
        assertEquals(Duration.ofHours(3), epic.getDuration(),
                "Неверная продолжительность эпика");
    }

    @Test
    void testEpicWithNoSubtasksHasNoTime() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);

        assertNull(epic.getStartTime(), "У эпика без подзадач не должно быть времени начала");
        assertNull(epic.getEndTime(), "У эпика без подзадач не должно быть времени окончания");
        assertNull(epic.getDuration(), "У эпика без подзадач не должно быть продолжительности");
    }


    @Test
    void testTasksWithoutTimeNotInPrioritizedList() {
        Task task1 = new Task("Task 1", "Desc", Statuc.NEW);
        Task task2 = new Task("Task 2", "Desc", Statuc.NEW,
                LocalDateTime.now(), Duration.ofHours(1));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(1, taskManager.getPrioritizedTasks().size(),
                "В списке должны быть только задачи с указанным временем");
    }

    // Тесты для HistoryManager
    @Test
    void testHistoryManagerRemovalFromMiddle() {
        Task task1 = new Task("Task 1", "Desc", Statuc.NEW);
        task1.setTaskId(1);
        Task task2 = new Task("Task 2", "Desc", Statuc.NEW);
        task2.setTaskId(2);
        Task task3 = new Task("Task 3", "Desc", Statuc.NEW);
        task3.setTaskId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(1, history.get(0).getTaskId());
        assertEquals(3, history.get(1).getTaskId());
    }

    @Test
    void testHistoryManagerRemovalFromEnd() {
        Task task1 = new Task("Task 1", "Desc", Statuc.NEW);
        task1.setTaskId(1);
        Task task2 = new Task("Task 2", "Desc", Statuc.NEW);
        task2.setTaskId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(1, history.get(0).getTaskId());
    }

    @Test
    void testHistoryManagerRemovalFromBeginning() {
        Task task1 = new Task("Task 1", "Desc", Statuc.NEW);
        task1.setTaskId(1);
        Task task2 = new Task("Task 2", "Desc", Statuc.NEW);
        task2.setTaskId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(2, history.get(0).getTaskId());
    }

    @Test
    void shouldSetEpicStatusNewWhenAllSubtasksAreNew() {
        Epic epic = new Epic("Epic NEW", "Desc");
        taskManager.addEpic(epic);

        Subtask s1 = new Subtask(epic, "Sub1", "Desc", Statuc.NEW);
        Subtask s2 = new Subtask(epic, "Sub2", "Desc", Statuc.NEW);
        taskManager.addSubTask(s1);
        taskManager.addSubTask(s2);

        Epic loadedEpic = taskManager.getEpicById(epic.getTaskId());
        assertEquals(Statuc.NEW, loadedEpic.getTaskStatus());
    }

    @Test
    void shouldSetEpicStatusDoneWhenAllSubtasksAreDone() {
        Epic epic = new Epic("Epic DONE", "Desc");
        taskManager.addEpic(epic);

        Subtask s1 = new Subtask(epic, "Sub1", "Desc", Statuc.DONE);
        Subtask s2 = new Subtask(epic, "Sub2", "Desc", Statuc.DONE);
        taskManager.addSubTask(s1);
        taskManager.addSubTask(s2);

        Epic loadedEpic = taskManager.getEpicById(epic.getTaskId());
        assertEquals(Statuc.DONE, loadedEpic.getTaskStatus());
    }

    @Test
    void shouldSetEpicStatusInProgressWhenSubtasksAreNewAndDone() {
        Epic epic = new Epic("Epic MIX NEW-DONE", "Desc");
        taskManager.addEpic(epic);

        Subtask s1 = new Subtask(epic, "Sub1", "Desc", Statuc.NEW);
        Subtask s2 = new Subtask(epic, "Sub2", "Desc", Statuc.DONE);
        taskManager.addSubTask(s1);
        taskManager.addSubTask(s2);

        Epic loadedEpic = taskManager.getEpicById(epic.getTaskId());
        assertEquals(Statuc.IN_PROGRESS, loadedEpic.getTaskStatus());
    }

    @Test
    void shouldSetEpicStatusInProgressWhenAnySubtaskIsInProgress() {
        Epic epic = new Epic("Epic IN_PROGRESS", "Desc");
        taskManager.addEpic(epic);

        Subtask s1 = new Subtask(epic, "Sub1", "Desc", Statuc.IN_PROGRESS);
        Subtask s2 = new Subtask(epic, "Sub2", "Desc", Statuc.DONE);
        taskManager.addSubTask(s1);
        taskManager.addSubTask(s2);

        Epic loadedEpic = taskManager.getEpicById(epic.getTaskId());
        assertEquals(Statuc.IN_PROGRESS, loadedEpic.getTaskStatus());
    }

}