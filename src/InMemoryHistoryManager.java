import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        if (task == null) return;

        // Удаляем старый узел, если задача уже есть в истории
        remove(task.getTaskId());

        // Добавляем задачу в конец списка
        Node<Task> newNode = new Node<>(task);
        linkLast(newNode);
        nodeMap.put(task.getTaskId(), newNode);
    }


    @Override
    public void remove(int id) {
        Node<Task> node = nodeMap.get(id);
        if (node != null) {
            removeNode(node);
            nodeMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }


    private void linkLast(Node<Task> node) {
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
            node.last = tail;
        }
        tail = node;
    }

    private void removeNode(Node<Task> node) {
        if (node.last != null) {
            node.last.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.last = node.last;
        } else {
            tail = node.last;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasks.add(current.data);
            current = current.next;
        }
        return tasks;
    }
    // Узел двусвязного списка
    private static class Node<T> {
        T data;
        Node<T> last;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }
}


