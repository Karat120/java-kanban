import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertAll(() -> assertTrue(loadedManager.getAllTasks().isEmpty()), () -> assertTrue(loadedManager.getAllEpic().isEmpty()), () -> assertTrue(loadedManager.getAllSubtask().isEmpty()));
    }

    @Test
    void shouldSaveDifferentTaskTypes() {

        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        Task task = new Task("Task", "Description", Statuc.NEW);
        manager.addTask(task);

        // Создаем подзадачу только после добавления эпика
        Subtask subtask = new Subtask(epic, "Subtask", "Description", Statuc.DONE);
        manager.addSubTask(subtask);

        manager.save();
        assertTrue(tempFile.length() > 0);
    }

    @Test
    void shouldSaveMultipleTasks() {
        Task task1 = new Task("Task1", "Description1", Statuc.NEW);
        Task task2 = new Task("Task2", "Description2", Statuc.IN_PROGRESS);

        manager.addTask(task1);
        manager.addTask(task2);

        assertTrue(tempFile.length() > 0, "Файл должен содержать сохраненные данные");
    }


    @Test
    void shouldLoadMultipleTasksFromFile() throws IOException {
        String csvData = """
                id,type,name,status,description,epic
                1,TASK,Task A,NEW,Desc A,
                2,TASK,Task B,DONE,Desc B,
                """;

        Files.writeString(tempFile.toPath(), csvData);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertAll(() -> assertEquals(2, loaded.getAllTasks().size(), "Должно быть загружено 2 задачи"), () -> assertEquals("Task A", loaded.getTaskId(1).getTaskName()), () -> assertEquals("Task B", loaded.getTaskId(2).getTaskName()));
    }

}