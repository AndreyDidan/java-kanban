package task.manager.service;

import org.junit.jupiter.api.Assertions;
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