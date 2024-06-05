package task.manager.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubTaskTest {

    @Test
    void SubTaskEqualitySubTask () {
        Epic newEpic = new Epic("Эпик1", "Описание эпика");
        SubTask expected = new SubTask("Задача 2", "Описание", StateTask.NEW, 0, 1);
        SubTask newSubTask = new SubTask("Задача 3", "Описание1",StateTask.IN_PROGRESS, 0, 1);
        expected.setId(0);
        newSubTask.setId(0);

        Assertions.assertEquals(expected, newSubTask);
    }
}