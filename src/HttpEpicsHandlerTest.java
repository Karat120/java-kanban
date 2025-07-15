import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HttpEpicsHandlerTest extends HttpTaskServerTestBase {
    private final Gson gson = BaseHttpHandler.gson;

    @Test
    public void shouldAddEpic() throws Exception {
        Epic epic = new Epic("Epic 1", "Epic description");
        String json = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epics = manager.getAllEpic();
        assertEquals(1, epics.size());
        assertEquals("Epic 1", epics.get(0).getTaskName());
    }

    @Test
    public void shouldReturnAllEpics() throws Exception {
        Epic epic1 = new Epic("Epic 1", "Desc 1");
        Epic epic2 = new Epic("Epic 2", "Desc 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertEquals(2, epics.length);
    }

    @Test
    public void shouldReturnEpicById() throws Exception {
        Epic epic = new Epic("Epic 1", "Desc");
        manager.addEpic(epic);

        URI uri = URI.create("http://localhost:8080/epics?id=" + epic.getTaskId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic result = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic.getTaskId(), result.getTaskId());
    }

    @Test
    public void shouldUpdateEpic() throws Exception {
        Epic epic = new Epic("Original Epic", "Desc");
        manager.addEpic(epic);

        epic.setTaskName("Updated Epic");
        String json = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic updated = manager.getEpicById(epic.getTaskId());
        assertEquals("Updated Epic", updated.getTaskName());
    }

    @Test
    public void shouldDeleteAllEpics() throws Exception {
        manager.addEpic(new Epic("Epic 1", "Desc 1"));
        manager.addEpic(new Epic("Epic 2", "Desc 2"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .DELETE()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllEpic().isEmpty());
    }

    @Test
    public void shouldReturn404ForNonexistentEpic() throws Exception {
        URI uri = URI.create("http://localhost:8080/epics?id=9999");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}
