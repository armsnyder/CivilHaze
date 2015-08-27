package snyder.adam.states;

/**
 * @author Adam Snyder
 */
public enum State {

    GAME(0);

    private final int stateNumber;

    State(int stateNumber) {
        this.stateNumber = stateNumber;
    }

    public int getValue() {
        return stateNumber;
    }
}
