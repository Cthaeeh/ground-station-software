package data;

import main.Main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by Kai on 03.06.2017.
 */
public class IOUtility {

    /**
     * Reads the text from a file.
     * @param file the file you want to read from.
     * @return the file interpreted as a string.
     * @throws IOException
     */
    public static String readFile(File file) throws IOException {
        String content = null;
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            Main.programLogger.log(Level.WARNING,"Failed to connect to read file: " + file.getName());
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return content;
    }

}
