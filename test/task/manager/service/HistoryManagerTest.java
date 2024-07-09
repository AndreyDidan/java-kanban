package task.manager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import task.manager.model.Epic;
import task.manager.model.StateTask;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void getTaskAndshouldHistory() {

        Task newTask1 = new Task(1,"Задача 1", "Описание31", StateTask.NEW);
        historyManager.add(newTask1);
        Epic newEpic2 = new Epic("Эпик2", "Описание");
        historyManager.add(newEpic2);
        SubTask newSubTask3 = new SubTask("Подзадача3", "Описание15",StateTask.NEW, 2, 3);
        historyManager.add(newSubTask3);
        SubTask newSubTask4 = new SubTask("Подзадача4", "Описание16",StateTask.NEW, 2, 4);
        historyManager.add(newSubTask4);
        Task newTask5 = new Task(5, "Задача 5", "Описание51", StateTask.NEW);
        historyManager.add(newTask5);

        int actual = 5;
        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(actual, history.size());
        Assertions.assertEquals(newSubTask3, history.get(2));
    }

    @Test
    void getTaskAndRemoveTaskAndshouldHistory() {

        Task newTask1 = new Task(1,"Задача 1", "Описание31", StateTask.NEW);
        historyManager.add(newTask1);
        Epic newEpic2 = new Epic("Эпик2", "Описание");
        historyManager.add(newEpic2);
        SubTask newSubTask3 = new SubTask("Подзадача3", "Описание15",StateTask.NEW, 2, 3);
        historyManager.add(newSubTask3);
        SubTask newSubTask4 = new SubTask("Подзадача4", "Описание16",StateTask.NEW, 2, 4);
        historyManager.add(newSubTask4);
        Task newTask5 = new Task(5, "Задача 5", "Описание51", StateTask.NEW);
        historyManager.add(newTask5);
        historyManager.remove(1);

        int actual = 4;
        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(actual, history.size());
    }

    @Test
    void getDublicateTaskAndshouldHistory() {

        Task newTask1 = new Task(1,"Задача 1", "Описание31", StateTask.NEW);
        historyManager.add(newTask1);
        Epic newEpic2 = new Epic("Эпик2", "Описание");
        historyManager.add(newEpic2);
        SubTask newSubTask3 = new SubTask("Подзадача3", "Описание15",StateTask.NEW, 2, 3);
        historyManager.add(newSubTask3);
        SubTask newSubTask4 = new SubTask("Подзадача4", "Описание16",StateTask.NEW, 2, 4);
        historyManager.add(newSubTask4);
        Task newTask5 = new Task(5, "Задача 5", "Описание51", StateTask.NEW);
        historyManager.add(newTask5);
        historyManager.add(newSubTask4);

        int actual = 5;
        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(actual, history.size());
        Assertions.assertEquals(newSubTask3, history.get(2));
        Assertions.assertEquals(newSubTask4, history.getLast());
    }

    @Test
    void deleteTaskInBeginningAndMidleAndEnd() {

        Task newTask1 = new Task(1,"Задача 1", "Описание31", StateTask.NEW);
        historyManager.add(newTask1);
        Epic newEpic2 = new Epic("Эпик2", "Описание");
        historyManager.add(newEpic2);
        SubTask newSubTask3 = new SubTask("Подзадача3", "Описание15",StateTask.NEW, 2, 3);
        historyManager.add(newSubTask3);
        SubTask newSubTask4 = new SubTask("Подзадача4", "Описание16",StateTask.NEW, 2, 4);
        historyManager.add(newSubTask4);
        Task newTask5 = new Task(5, "Задача 5", "Описание51", StateTask.NEW);
        historyManager.add(newTask5);
        historyManager.add(newSubTask4);

        historyManager.remove(1);
        historyManager.remove(3);
        historyManager.remove(5);
        int actual = 2;
        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(newEpic2, history.get(0));
        Assertions.assertEquals(newSubTask4, history.get(1));
        Assertions.assertEquals(actual, history.size());
    }

    @Test
    void add_shouldAddElementToHistory() {
        Task newTask = new Task("Задача 1", "Описание 1");

        historyManager.add(newTask);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(newTask, history.getFirst());
    }

    @Test
    void add_shouldLinkInCorrectOrder() {
        Task task1 = new Task(1, "Задача 2", "Описание 2", StateTask.NEW);
        Task task2 = new Task(2, "Задача 3", "Описание 3", StateTask.NEW);
        Task task3 = new Task(3, "Задача 4", "Описание 4", StateTask.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(3, history.size());
        Assertions.assertEquals(task1, history.getFirst());
        Assertions.assertEquals(task2, history.get(1));
        Assertions.assertEquals(task3, history.get(2));
    }

    @Test
    void remove_shouldRemoveFromHistoryFirstElement() {
        Task task1 = new Task(1, "Задача 5", "Описание 5", StateTask.NEW);
        Task task2 = new Task(2,"Задача 6", "Описание 6", StateTask.NEW);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task2, history.getFirst());
    }

    @Test
    void remove_shouldRemoveFromHistoryMiddleElement() {
        Task task1 = new Task(1, "Задача 10", "Описание 10", StateTask.NEW,
                LocalDateTime.of(2024, 5, 5, 0, 0), Duration.ofMinutes(15));
        Task task2 = new Task(2, "Задача 11", "Описание 11", StateTask.NEW,
                LocalDateTime.of(2024, 5, 2, 0, 15), Duration.ofMinutes(15));
        Task task3 = new Task(3,"Задача 12", "Описание 12", StateTask.NEW,
                LocalDateTime.of(2024, 5, 2, 0, 30), Duration.ofMinutes(15));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task1, history.getFirst());
        Assertions.assertEquals(task3, history.getLast());
    }

    @Test
    void remove_shouldRemoveFromHistoryLastElement() {
        Task task1 = new Task(1, "Задача 13", "Описание 13", StateTask.NEW);
        Task task2 = new Task(2, "Задача 14", "Описание 14", StateTask.NEW);
        Task task3 = new Task(3, "Задача 15", "Описание 15", StateTask.NEW);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task1, history.getFirst());
        Assertions.assertEquals(task2, history.getLast());
    }

    @Test
    void add_shouldDeleteExistingTaskFromHistoryAndPutItInTheEnd() {
        Task task1 = new Task(1,"Задача 7", "Описание 7", StateTask.NEW);
        Task task2 = new Task(2, "Задача 8", "Описание 8", StateTask.NEW);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task1, history.getLast());
        Assertions.assertEquals(task2, history.getFirst());
    }

    @Test
    void add_shouldRewriteSameAddedTask() {
        Task task1 = new Task("Задача 9", "Описание 9");

        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task1, history.getFirst());
    }
}