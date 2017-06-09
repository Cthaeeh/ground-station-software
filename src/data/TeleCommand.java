package data;

/**
 * Created by Kai on 09.06.2017.
 */
public class TeleCommand {

    private byte[] command;
    private String name;
    private String description;

    public byte[] getBytes() {
        return command;
    }

    public String getName() {
        return name;
    }


}
