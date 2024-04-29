package task.manager.service;

import task.manager.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;

    private Map<Integer, Node> history = new HashMap<>();

    public Node linklast(Task task) {

        Node newNode = new Node(task, null, null);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
        return newNode;
    }

    public List<Task> getTasks() {
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
        if (history.containsKey(task.getId())) {
            removeNode(task.getId());
        }
        history.put(task.getId(), linklast(task));
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
        Node node = this.history.get(id);
        if (node != null) {
            if (node.equals(this.head)) {
                this.head = node.next;
            } else if (node.equals(this.tail)) {
                this.tail = node.prev;
                this.tail.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            this.history.remove(id);
        }
    }
}