package snyder.adam;

/**
 * @author Adam Snyder
 */
public interface DialogueListener {
    /**
     * Fired when the dialogue script indicates the character is experiencing a new emotion
     * @param emotion e.g. happy, sad, smug
     */
    void emotionUpdate(String emotion);

    /**
     * Fired when the character begins speaking
     */
    void beginSpeaking();

    /**
     * Fired when the character has stopped speaking
     */
    void endSpeaking();

    /**
     * Fired each time the visible dialogue text changes
     */
    void textUpdate(String text);
}
