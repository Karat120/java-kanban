import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTasksHandlerTest extends HttpTaskServerTestBase {
    private final Gson gson = BaseHttpHandler.gson;

    @Test
    public void shouldAddTask() throws Exception {
        Task task = new Task("Test task", "desc", Statuc.NEW,
                LocalDateTime.now(), Duration.ofMinutes(15));
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test task", tasks.get(0).getTaskName());
    }

    @Test
    public void shouldReturnAllTasks() throws Exception {
        Task task1 = new Task(
                "Task1",
                "desc1",
                Statuc.NEW,
                LocalDateTime.of(2025, 7, 15, 10, 0),
                Duration.ofMinutes(10)
        );

        Task task2 = new Task(
                "Task2",
                "desc2",
                Statuc.NEW,
                LocalDateTime.of(2025, 7, 15, 10, 15),
                Duration.ofMinutes(20)
        );

        manager.addTask(task1);
        manager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
    }


    @Test
    public void shouldReturnTaskById() throws Exception {
        Task task = new Task("Task by ID", "desc", Statuc.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.addTask(task);

        URI uri = URI.create("http://localhost:8080/tasks?id=" + task.getTaskId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task result = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getTaskId(), result.getTaskId());
    }

    @Test
    public void shouldUpdateTask() throws Exception {
        Task task = new Task("Original", "desc", Statuc.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        manager.addTask(task);

        task.setTaskName("Updated");
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task updated = manager.getTaskId(task.getTaskId());
        assertEquals("Updated", updated.getTaskName());
    }

    @Test
    public void shouldDeleteTaskById() throws Exception {
        Task task = new Task("To Delete", "desc", Statuc.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        manager.addTask(task);

        URI uri = URI.create("http://localhost:8080/tasks?id=" + task.getTaskId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getTaskId(task.getTaskId()));
    }

    @Test
    public void shouldDeleteAllTasks() throws Exception {
        manager.addTask(new Task("Task1", "desc", Statuc.NEW,
                LocalDateTime.of(2025, 7, 15, 10, 0),
                Duration.ofMinutes(10)));

        manager.addTask(new Task("Task2", "desc", Statuc.NEW,
                LocalDateTime.of(2025, 7, 15, 11, 0),
                Duration.ofMinutes(10)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode());

        // Проверяем, что задачи удалены
        assertTrue(manager.getAllTasks().isEmpty());
    }


    @Test
    public void shouldReturn404ForNonexistentTask() throws Exception {
        URI uri = URI.create("http://localhost:8080/tasks?id=9999");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldReturn405ForUnsupportedMethod() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .method("PUT", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
    }
}
