package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    public void getDefaultTest() {
        Assertions.assertNotNull(Managers.getDefault());
    }
    @Test
    public void getDefaultHistoryTest() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}