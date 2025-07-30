import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerFullTest extends HttpTaskServerTestBase {

    @Test
    public void shouldAddTask() throws Exception {
        Task task = new Task("Test task", "desc", Statuc.NEW,
                LocalDateTime.now(), Duration.ofMinutes(15));
        String json = BaseHttpHandler.gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        assertEquals(1, manager.getAllTasks().size());
        assertEquals("Test task", manager.getAllTasks().get(0).getTaskName());
    }

    @Test
    public void shouldReturnAllTasks() throws Exception {
        Task task1 = new Task("Task1", "desc1", Statuc.NEW,
                LocalDateTime.of(2025, 7, 15, 10, 0),
                Duration.ofMinutes(10));
        Task task2 = new Task("Task2", "desc2", Statuc.NEW,
                LocalDateTime.of(2025, 7, 15, 10, 15),
                Duration.ofMinutes(20));

        manager.addTask(task1);
        manager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] tasks = BaseHttpHandler.gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
    }

    @Test
    public void shouldDeleteTaskById() throws Exception {
        Task task = new Task("To Delete", "desc", Statuc.NEW,
                LocalDateTime.now(), Duration.ofMinutes(15));
        manager.addTask(task);

        URI uri = URI.create("http://localhost:8080/tasks?id=" + task.getTaskId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getTaskId(task.getTaskId()));
    }


    @Test
    public void shouldAddEpic() throws Exception {
        Epic epic = new Epic("Epic 1", "Epic desc");
        String json = BaseHttpHandler.gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        assertEquals(1, manager.getAllEpic().size());
        assertEquals("Epic 1", manager.getAllEpic().get(0).getTaskName());
    }

    @Test
    public void shouldAddSubtask() throws Exception {
        Epic epic = new Epic("Epic for Subtask", "Desc");
        manager.addEpic(epic);

        // Создаем Subtask через конструктор с Epic
        Subtask subtask = new Subtask(epic, "Subtask 1", "Desc", Statuc.NEW);
        String json = BaseHttpHandler.gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        assertEquals(1, manager.getAllSubtask().size());
        assertEquals("Subtask 1", manager.getAllSubtask().get(0).getTaskName());
    }

    @Test
    public void shouldDeleteSubtaskById() throws Exception {
        Epic epic = new Epic("Epic for Subtask Delete", "Desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask(epic, "Subtask to Delete", "Desc", Statuc.NEW);
        manager.addSubTask(subtask);

        URI uri = URI.create("http://localhost:8080/subtasks?id=" + subtask.getTaskId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getSubTaskById(subtask.getTaskId()));
    }

    @Test
    public void shouldReturnHistory() throws Exception {
        Task task1 = new Task("Task 1", "desc", Statuc.NEW,
                LocalDateTime.of(2025, 7, 15, 9, 0),
                Duration.ofMinutes(10));
        Task task2 = new Task("Task 2", "desc", Statuc.NEW,
                LocalDateTime.of(2025, 7, 15, 9, 15),
                Duration.ofMinutes(15));
        manager.addTask(task1);
        manager.addTask(task2);

        manager.getTaskId(task1.getTaskId());
        manager.getTaskId(task2.getTaskId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] historyTasks = BaseHttpHandler.gson.fromJson(response.body(), Task[].class);
        assertEquals(2, historyTasks.length);
        assertEquals(task1.getTaskId(), historyTasks[0].getTaskId());
        assertEquals(task2.getTaskId(), historyTasks[1].getTaskId());
    }

    @Test
    public void shouldReturnPrioritizedTasks() throws Exception {
        Task task1 = new Task("Task 1", "desc", Statuc.NEW,
                LocalDateTime.of(2025, 7, 15, 9, 0),
                Duration.ofMinutes(10));   // 09:00-09:10

        Task task2 = new Task("Task 2", "desc", Statuc.NEW,
                LocalDateTime.of(2025, 7, 15, 9, 20),
                Duration.ofMinutes(15));   // 09:20-09:35

        Task task3 = new Task("Task 3", "desc", Statuc.NEW,
                LocalDateTime.of(2025, 7, 15, 9, 40),
                Duration.ofMinutes(5));    // 09:40-09:45

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] prioritizedTasks = BaseHttpHandler.gson.fromJson(response.body(), Task[].class);
        assertEquals(3, prioritizedTasks.length);

        assertEquals(task1.getTaskId(), prioritizedTasks[0].getTaskId());
        assertEquals(task2.getTaskId(), prioritizedTasks[1].getTaskId());
        assertEquals(task3.getTaskId(), prioritizedTasks[2].getTaskId());
    }

}
