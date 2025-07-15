import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();

            switch (method) {
                case "GET":
                    handleGet(exchange, query);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, query);
                    break;
                default:
                    sendMethodNotAllowed(exchange);
            }
        } catch (IllegalArgumentException e) {
            sendBadRequest(exchange, e.getMessage());
        } catch (Exception e) {
            sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            List<Epic> epics = taskManager.getAllEpic();
            sendJson(exchange, epics, 200);
        } else {
            int id = parseId(query);
            Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                sendJson(exchange, epic, 200);
            } else {
                sendNotFound(exchange, "Эпик не найден");
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Epic epic = readRequestBody(exchange, Epic.class);
        if (epic.getTaskId() != 0 && taskManager.getEpicById(epic.getTaskId()) != null) {
            taskManager.updateEpic(epic);
            sendText(exchange, "Эпик обновлен", 200);
        } else {
            taskManager.addEpic(epic);
            sendCreated(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            taskManager.removeAllEpic();
            sendText(exchange, "Все эпики удалены", 200);
        } else {
            int id = parseId(query);
            taskManager.deleteEpicTaskById(id);
            sendText(exchange, "Эпик удален", 200);
        }
    }
}