package serial;

/**
 * Created by Kai on 24.06.2017.
 */
public interface MessageListener {
    void processMessage(byte[] message);
}
