public class IdGenerator {
    private static int sequence = 0;
    public static int generateNewId() {
        return sequence++;
    }
}
