import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();

            switch (method) {
                case "GET":
                    if (query == null) {
                        List<Subtask> subtasks = taskManager.getAllSubtask();
                        sendJson(exchange, subtasks, 200);
                    } else {
                        int id = parseId(query); //
                        Subtask subtask = taskManager.getSubTaskById(id);
                        if (subtask != null) {
                            sendJson(exchange, subtask, 200);
                        } else {
                            sendNotFound(exchange, "Подзадача не найдена");
                        }
                    }
                    break;

                case "POST":
                    Subtask subtask = readRequestBody(exchange, Subtask.class);
                    if (taskManager.getSubTaskById(subtask.getTaskId()) != null) {
                        taskManager.updateSubTask(subtask);
                        sendText(exchange, "Подзадача обновлена", 200);
                    } else {
                        taskManager.addSubTask(subtask);
                        sendCreated(exchange);
                    }
                    break;

                case "DELETE":
                    if (query == null) {
                        taskManager.removeAllSubTasks();
                        sendText(exchange, "Все подзадачи удалены", 200);
                    } else {
                        int id = parseId(query);
                        taskManager.deleteSubTaskById(id);
                        sendText(exchange, "Подзадача удалена", 200);
                    }
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
}