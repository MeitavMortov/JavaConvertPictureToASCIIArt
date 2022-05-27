package ascii_art;
import image.Image;
import java.util.logging.Logger;

/**
 * Driver class that drives all the program
 */
public class Driver {
    /**
     * the main function
     * @param args - cmd arguments
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("USAGE: java asciiArt ");
            return;
        }
        Image img = Image.fromFile(args[0]);
        if (img == null) {
            Logger.getGlobal().severe("Failed to open image file " +
                    args[0]);
            return;
        }
        new Shell(img).run();
    }
}