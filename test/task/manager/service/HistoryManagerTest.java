package task.manager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import task.manager.model.Epic;
import task.manager.model.StateTask;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void getTaskAndShouldHistory() {

        Task newTask1 = new Task(1,"Задача 31", "Описание31", StateTask.NEW);
        historyManager.add(newTask1);
        Epic newEpic2 = new Epic("Эпик2", "Описание эпика 2");
        historyManager.add(newEpic2);
        SubTask newSubTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", StateTask.NEW, 2, 3);
        historyManager.add(newSubTask1);
        SubTask newSubTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", StateTask.NEW, 2, 4);
        historyManager.add(newSubTask2);
        SubTask newSubTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", StateTask.NEW, 2, 5);
        historyManager.add(newSubTask3);
        historyManager.add(newTask1);


        List<Task> history = historyManager.getHistory();
        int size = 5;


        Assertions.assertEquals(newSubTask2, history.get(2));
        Assertions.assertEquals(size, history.size());
    }

    @Test
    void getTaskAndRemoveTaskAndShouldHistory() {

        Task newTask1 = new Task(1,"Задача 31", "Описание31", StateTask.NEW);
        historyManager.add(newTask1);
        Epic newEpic2 = new Epic("Эпик2", "Описание эпика 2");
        historyManager.add(newEpic2);
        SubTask newSubTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", StateTask.NEW, 2, 3);
        historyManager.add(newSubTask1);
        SubTask newSubTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", StateTask.NEW, 2, 4);
        historyManager.add(newSubTask2);
        SubTask newSubTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", StateTask.NEW, 2, 5);
        historyManager.add(newSubTask3);
        historyManager.add(newTask1);
        historyManager.remove(5);


        List<Task> history = historyManager.getHistory();
        int size = 4;


        Assertions.assertEquals(newSubTask2, history.get(2));
        Assertions.assertEquals(size, history.size());
    }

}