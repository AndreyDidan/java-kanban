package task.manager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

        Task expected1 = new Task(10,"Задача 10", "Описание10", StateTask.NEW);
        SubTask expexted2 = new SubTask("Подзадача4", "Описание16", StateTask.NEW, 2, 4);
        Task newTask = new Task(0,"Задача 0", "Описание3", StateTask.NEW);
        historyManager.add(newTask);
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
        Task newTask6 = new Task(6,"Задача 6", "Описание61", StateTask.NEW);
        historyManager.add(newTask6);
        Task newTask7 = new Task(7,"Задача 7", "Описание71", StateTask.NEW);
        historyManager.add(newTask7);
        Task newTask8 = new Task(8,"Задача 8", "Описание81", StateTask.NEW);
        historyManager.add(newTask8);
        Task newTask9 = new Task(9,"Задача 9", "Описание91", StateTask.NEW);
        historyManager.add(newTask9);
        Task newTask10 = new Task(10,"Задача 10", "Описание10", StateTask.NEW);
        historyManager.add(newTask10);
        Task newTask11 = new Task(11, "Задача 11", "Описание11", StateTask.NEW);
        historyManager.add(newTask11);

        int actual = 10;
        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(actual, history.size());
        Assertions.assertEquals(expected1, history.get(8));
        Assertions.assertEquals(expexted2, history.get(2));
    }
}