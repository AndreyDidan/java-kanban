package task.manager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import task.manager.model.Epic;
import task.manager.model.StateTask;
import task.manager.model.SubTask;
import task.manager.model.Task;

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
    void deleteTaskInBeginning() {

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

        int actual = 4;
        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(actual, history.size());
    }

    @Test
    void deleteTaskInMiddle() {

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
        historyManager.remove(3);

        int actual = 4;
        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(actual, history.size());
    }

    @Test
    void deleteTaskInEnd() {

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
        historyManager.remove(5);

        int actual = 4;
        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(actual, history.size());
    }
}