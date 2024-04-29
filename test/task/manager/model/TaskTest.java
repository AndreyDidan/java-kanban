package task.manager.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void taskEqualityTask () {
        Task task1 = new Task(0, "Задача 1", "Описание 1", StateTask.NEW);
        Task task2 = new Task (0, "Задача 2", "Описание 2", StateTask.NEW);
        task1.setId(0);
        task2.setId(0);

        Assertions.assertEquals(task1, task2);
    }
}