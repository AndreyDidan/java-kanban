package task.manager.service;

import task.manager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Task data, Node next, Node prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private Node head;
    private Node tail;

    private Map<Integer, Node> history = new HashMap<>();

    private Node linklast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(task, null, oldTail);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            history.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return history;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.containsKey(task.getId())) {
                removeNode(task.getId());
            }
            history.put(task.getId(), linklast(task));
        }
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void removeNode(int id) {
        Node node = history.get(id);

        if (node != null) {
            if (node.prev == null && node.next == null) {
                head = null;
                tail = null;
            } else if (node.next == null) {
                tail = node.prev;
                node.prev.next = null;
            } else if (node.prev == null) {
                head = node.next;
                node.next.prev = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            history.remove(id);
        }
    }
}