package task.manager.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {

    @Test
    void epicEqualityEpic () {
        Epic epic = new Epic("Эпик", "Описание");
        Epic epic1 = new Epic("Эпик1", "Описание1");
        epic.setId(0);
        epic1.setId(0);

        Assertions.assertEquals(epic, epic1);
    }
}