package task.manager.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.manager.service.Managers;
import task.manager.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

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