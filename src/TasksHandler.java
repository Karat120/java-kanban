import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
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

    // Обработка GET-запросов
    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            List<Task> tasks = taskManager.getAllTasks();
            sendJson(exchange, tasks, 200);
        } else {
            int id = parseId(query);
            Task task = taskManager.getTaskId(id);
            if (task != null) {
                sendJson(exchange, task, 200);
            } else {
                sendNotFound(exchange, "Задача не найдена");
            }
        }
    }

    // Обработка POST-запросов
    private void handlePost(HttpExchange exchange) throws IOException {
        Task task = readRequestBody(exchange, Task.class);
        if (task.getTaskId() != 0 && taskManager.getTaskId(task.getTaskId()) != null) {
            taskManager.updateTask(task);
            sendText(exchange, "Задача обновлена", 200);
        } else {
            taskManager.addTask(task);
            sendCreated(exchange);
        }
    }

    // Обработка DELETE-запросов
    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            taskManager.removeAllTask();
            sendText(exchange, "Все задачи удалены", 200);
        } else {
            int id = parseId(query);
            taskManager.deleteTaskById(id);
            sendText(exchange, "Задача удалена", 200);
        }
    }
}