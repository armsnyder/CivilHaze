package snyder.adam;

/**
 * @author Adam Snyder
 */
public class Participant {
    private final int id;
    private long lastPing;

    public Participant(int participantId) {
        id = participantId;
        setLastPing();
    }

    public int getId() {
        return id;
    }

    public long getLastPing() {
        return lastPing;
    }

    public void setLastPing() {
        this.lastPing = System.currentTimeMillis();
    }
}
