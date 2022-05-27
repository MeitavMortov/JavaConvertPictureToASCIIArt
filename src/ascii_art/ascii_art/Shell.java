package ascii_art;
import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

/**
 * class contains the run method that responsible on the contact with the user and excuting commands
 */
public class Shell {

    //constant:
    /**
     * INITIAL_CHARS_IN_ROW - num of chars in row- default.
     */
    private static final int INITIAL_CHARS_IN_ROW = 64;
    /**
     * MIN_PIXELS_PER_CHAR - represents the minimum num of pixels per char.
     */
    private static final int MIN_PIXELS_PER_CHAR = 2;

    /**
     * INITIAL_CHARS_IN_ROW - chars in char set- default.
     */
    private static final String INITIAL_CHARS_RANGE = "0-9";
    /**
     * FONT_NAME - default font name.
     */
    private static final String FONT_NAME = "Courier New";
    /**
     * OUTPUT_FILENAME - name of output file name.
     */
    private static final String OUTPUT_FILENAME = "out.html";
    /**
     * CMD_EXIT - represents the command "exit"
     */
    private static final String CMD_EXIT = "exit";
    /**
     * CMD_CHARS - represents the command "chars"
     */
    private static final String CMD_CHARS = "chars";
    /**
     * CMD_ADD - represents the command "add"
     */
    private static final String CMD_ADD = "add";
    /**
     * CMD_REMOVE - represents the command "remove"
     */
    private static final String CMD_REMOVE = "remove";
    /**
     * CMD_RES - represents the command "res"
     */
    private static final String CMD_RES = "res";
    /**
     * CMD_CONSOLE - represents the command "console"
     */
    private static final String CMD_CONSOLE = "console";
    /**
     * CMD_RENDER - represents the command "render"
     */
    private static final String CMD_RENDER = "render";
    /**
     * CMD_ADD_SPACE - represents the command "space" for the add command.
     */
    private static final String CMD_ADD_SPACE = "space";
    /**
     * CMD_ADD_All - represents the command "all" for the add command.
     */
    private static final String CMD_ADD_All = "all";
    /**
     * CMD_RES_UP - represents the command "up" for the res command.
     */
    private static final String CMD_RES_UP = "up";
    /**
     * CMD_RES_DOWN - represents the command "down" for the res command.
     */
    private static final String CMD_RES_DOWN = "down";
    /**
     * CMD_RES_DOWN - represents the integer multiply the res for the res up
     * divide on it if res down command.
     */
    private static final int CMD_RES_FACTOR = 2;

    //fields:
    private final Image img;
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private final BrightnessImgCharMatcher charMatcher;
    private AsciiOutput output;
    private final Set<Character> charSet = new HashSet<>();
    private boolean isConsoleFormat;

    /**
     * Constructor
     * @param img - img to work on
     */
    public Shell(Image img){
        this.img = img;
        addChars(INITIAL_CHARS_RANGE);
        minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
        output = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
        isConsoleFormat = false;
    }

    /**
     * the run metod is thehe metod that runs the program and deals with commands from user.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(">>> ");
        String cmd = scanner.nextLine().trim();
        String[] words = cmd.split("\\s+");
        while (!words[0].equals(CMD_EXIT)) {
            if (!words[0].equals("")) {
                String param = "";
                if (words.length > 1) {
                    param = words[1];
                }
                switch (words[0]) {
                    case CMD_CHARS:
                        charsCmdExecute(words);
                        break;
                    case CMD_ADD:
                        addCmdExecute(words, param);
                        break;
                    case CMD_REMOVE:
                        removeCmdExecute(words, param);
                        break;
                    case CMD_RES:
                        resCmdExecute(words,param);
                        break;
                    case CMD_CONSOLE:
                        consoleCmdExecute(words);
                        break;
                    case CMD_RENDER:
                        renderCmdExecute(words);
                        break;
                    default:
                        System.out.println("ERROR: the command is not valid!");
                        break;
                }
            }
            System.out.print(">>> ");
            cmd = scanner.nextLine().trim();
            words = cmd.split("\\s+");
        }
    }

    /**
     * method Execute the chars Command , check validity and if input is invalid prints message
     * @param words - input from user.
     */
    private void charsCmdExecute(String[] words){
        if (words.length > 1){
            System.out.println("ERROR: there are too many arguments for the chars command!");
        }
        else{
            showChars();
        }
    }
    /**
     * method Execute the add Command , check validity and if input is invalid prints message
     * @param words - input from user.
     * @param param - param to the command.
     */    private void addCmdExecute(String[] words,String param){
        if (words.length > 2){
            System.out.println("ERROR: there are too many arguments for the add command!");
        }
        else{
            addChars(param);
        }
    }
    /**
     * method Execute the remove Command , check validity and if input is invalid prints message
     * @param words - input from user.
     * @param param - param to the command.
     */
    private void removeCmdExecute(String[] words,String param){
        if (words.length > 2){
            System.out.println("ERROR: there are too many arguments for the add command!");
        }
        else{
            removeChars(param);
        }
    }
    /**
     * method Execute the res Command , check validity and if input is invalid prints message
     * @param words - input from user.
     * @param param - param to the command.
     */
    private void resCmdExecute(String[] words,String param){
        if (words.length > 2){
            System.out.println("ERROR: there are too many arguments for the add command!");
        }
        else{
            resChange(param);
        }
    }

    /**
     * method Execute the console Command , check validity and if input is invalid prints message
     * @param words - input from user.
     */
    private void consoleCmdExecute(String[] words){
        if (words.length > 1){
            System.out.println("ERROR: there are too many arguments for the chars command!");
        }
        else{
            console();
        }
    }
    /**
     * method Execute the render Command , check validity and if input is invalid prints message
     * @param words - input from user.
     */
    private void renderCmdExecute(String[] words){
        if (words.length > 1){
            System.out.println("ERROR: there are too many arguments for the chars command!");
        }
        else{
            render();
        }
    }

    /**
     * Show the chars to the user by printing them to the console.
     */
    private void showChars(){
        charSet.stream().sorted().forEach(eachChar-> System.out.print(eachChar + " "));
        System.out.println();
    }

    /**
     * method that parse Char Range that add or remove commands got.
     * @param param - - param to the command.
     * @return return the range on an array of two strings represent the upper and smaller bounds.
     */
    private static char[] parseCharRange(String param){
        char[] returnedArray = null;
        //if len param == 1 --> returnedArray = {param,param}
        if (param.length() == 1){
            //one letter
            returnedArray = new char[]{param.charAt(0), param.charAt(0)};
        }
        // else if param.equals("space") --> returnedArray = {' ',' '}
        else if (param.equals(CMD_ADD_SPACE)){
            returnedArray = new char[]{' ', ' '};
        }
        // else if param.equals("space") --> returnedArray = {'~', ' '}
        else if (param.equals(CMD_ADD_All)){
            returnedArray = new char[] {' ', '~'} ;
        }
        else if (param.length() == 3){
            if(param.charAt(1) == '-'){
                int firstAscii = param.charAt(0);
                int secondAscii = param.charAt(2);
                int compareResult = secondAscii - firstAscii;
                if (compareResult >= 0){
                    returnedArray = new char[] {param.charAt(0), param.charAt(2)} ;
                }
                else {
                    returnedArray = new char[] {param.charAt(2), param.charAt(0)} ;
                }
            }
        }
        return returnedArray;
    }

    /**
     * add chars to the char set.
     * @param s - represent the bounds of the range.
     */
    private void addChars(String s) {
        char[] range = parseCharRange(s);
        if(range != null){
            Stream.iterate(range[0], c -> c <= range[1], c -> (char)((int)c+1)).forEach(charSet::add);
        }
        else {
            System.out.println("ERROR: the argument for the add command is not valid!");
        }
    }
    /**
     * remove chars from the char set.
     * @param s - represent the bounds of the range.
     */    private void removeChars(String s){
        char[] range = parseCharRange(s);
        if(range != null){
            Stream.iterate(range[0], c -> c <= range[1], c -> (char)((int)c+1)).forEach(charSet::remove);
        }
        else {
            System.out.println("ERROR: the argument for the remove command is not valid!");
        }
    }

    /**
     * change the res according to the param s-
     * if param is up multiply res by CMD_RES_FACTOR
     * if param is down divide res by CMD_RES_FACTOR
     * @param s represents the type of res that should be done- up OR down
     */
    private void resChange(String s){
        if (s.equals(CMD_RES_UP)){
            if(charsInRow * CMD_RES_FACTOR > maxCharsInRow){
                System.out.println("ERROR: the num of chars in row is maximal!");
                charsInRow = maxCharsInRow;
            }
            else{
                charsInRow = charsInRow * CMD_RES_FACTOR;
                System.out.println("Width set to " + charsInRow);
            }
            return;
        }
        if (s.equals(CMD_RES_DOWN)){
            if(charsInRow / CMD_RES_FACTOR < minCharsInRow){
                System.out.println("ERROR: the num of chars in row is minimal!");
                charsInRow = minCharsInRow;
            }
            else{
                charsInRow = charsInRow / CMD_RES_FACTOR;
                System.out.println("Width set to " + charsInRow);
            }
            return;
        }
        System.out.println("ERROR: the argument for the res command is not up or down!");
    }

    /**
     * represent the console command- that next time of renderer will render to the console.
     */
    private void console() {
        if (!isConsoleFormat){
            isConsoleFormat = true;
            output = new ConsoleAsciiOutput();
        }
    }

    /**
     * render the image according to the state - to the console or as hml file.
     */
    private void render(){
        if (charSet.isEmpty() | charsInRow <= 0){
            return;
        }
        output.output(charMatcher.chooseChars(charsInRow, convertCharacterSetToArray()));
    }

    /**
     * Method that convert the  Character set To Array.
     * @return array of the character set.
     */
    private Character[] convertCharacterSetToArray(){
        int arrayLength = charSet.size();
        Character[] arr = new Character[arrayLength];
        int i = 0;
        for (Character c : charSet){
            arr[i] = c;
            i++;
        }
        return arr;
    }
}
