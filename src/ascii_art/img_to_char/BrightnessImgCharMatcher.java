package ascii_art.img_to_char;
import image.Image;
import java.awt.*;
import java.util.*;


/**
 * In this class I
 * implement all the logic that converts a color image to an ASCII image.
 */
public class BrightnessImgCharMatcher {
    private static final int PIXELS = 16; // pixel resolution in ex4
    private final Image img; // the given image
    private final String fontName; // the given font
    private final HashMap<Image, Double> cache = new HashMap<>();
    //Maintain the brightness values calculated on the first run on picture to the next time,Optimizes (runtime).

    /**
     *  Constructor
     * @param img - Image object to work on.
     * @param fontName - the name of the font that will be used.
     */
    public BrightnessImgCharMatcher(Image img, String fontName){
        this.img = img;
        this.fontName = fontName;
    }

    /**
     * method that returns A two-dimensional array of ASCII characters representing the image from constructor.
     * @param numCharsInRow -  Is the number of characters for each line in the ASCII image.
     * @param charSet - Is an array that contains the characters
     *  from which the ASCII image can be assembled.
     * @return A two-dimensional array of ASCII characters representing an image
     * (the same image obtained in the constructor).
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet){
        if (charSet.length == 0){
            return new char[0][0];
        }
        //remove identical Characters:
        charSet = new HashSet<Character>(Arrays.asList(charSet)).toArray(Character[]::new);
        //I convert it back into array cause need to save order of this array and the brightnessLevelArray.
        double[] brightnessLevelArray = doLinearStretching(calculateTheBrightnessLevelOfCharacters(charSet));
        // init Map Brightness To Character:
        //If there  are identical brightness level after linear stretch only one will be entered the map.
        Map<Double,Character> mapBrightnessToCharacter = new HashMap<>();
        for (int i = 0; i < charSet.length; i++) {
            if(!mapBrightnessToCharacter.containsKey(brightnessLevelArray[i]))
            mapBrightnessToCharacter.put(brightnessLevelArray[i],charSet[i]);
        }
        quickSort(brightnessLevelArray,0, brightnessLevelArray.length - 1);
        //brightnessLevelArray is sorted
        return this.convertImageToAscii(numCharsInRow, brightnessLevelArray, mapBrightnessToCharacter);
    }

    /**
     * Calculate The Brightness Level Of Characters
     * @param charSet -Is an array that contains the characters
     *  from which the ASCII image can be assembled.
     * @return Array with char level of brightness.
     */
    private double[] calculateTheBrightnessLevelOfCharacters(Character[] charSet){
        double[] brightnessLevelArray = new double[charSet.length];
        for (int indexCharacter = 0; indexCharacter < charSet.length; indexCharacter++) {
            boolean[][] img = CharRenderer.getImg(charSet[indexCharacter], PIXELS, this.fontName);
            int num_of_pixels = 0 ;
            int num_of_white_pixels = 0;
            for (int i = 0; i < img.length; i++) {
                for (int j = 0; j < img[i].length; j++) {
                    num_of_pixels += 1;
                    if (img[i][j]){
                        //white pixel
                        num_of_white_pixels += 1;
                    }
                }
            }
            double brightnessLevel = (double)num_of_white_pixels / (double)num_of_pixels;
            brightnessLevelArray[indexCharacter] = brightnessLevel;
        }
        return brightnessLevelArray;
    }

    /**
     * method that do Linear Stretching
     * @param brightnessLevelArray Array with char level of brightness.
     * @return brightnessLevelArray after  Linear Stretching
     */
    private double[] doLinearStretching(double[] brightnessLevelArray){
        if (brightnessLevelArray.length == 0){
            return new double[0];
        }
        double minBrightness = findMinValue(brightnessLevelArray);
        double maxBrightness = findMaxValue(brightnessLevelArray);
        double[] newBrightnessLevelArray = new double[brightnessLevelArray.length];
        if (maxBrightness == minBrightness){
            for (int i = 0; i < brightnessLevelArray.length; i++) {
                newBrightnessLevelArray[i] = 1;
            }
        }
        else{
            for (int i = 0; i < brightnessLevelArray.length; i++) {
                double newCharBrightness = (brightnessLevelArray[i] - minBrightness) / (maxBrightness - minBrightness);
                newBrightnessLevelArray[i] = newCharBrightness;
            }
        }
        return newBrightnessLevelArray;
    }

    /**
     * calculates Average Value Of Pixel Brightness.
     * @param img -  the image.
     * @return the Average Value Of Pixel Brightness.
     */
    private double calculateAverageValueOfPixelBrightness(Image img){
        int num_of_pixels = 0;
        double sumOfPixelBrightness = 0;
        for (Color pixel : img.pixels()) {
            double greyPixel =
                    (pixel.getRed() * 0.2126 + pixel.getGreen() * 0.7152 + pixel.getBlue() * 0.0722) / 255;
            sumOfPixelBrightness += greyPixel;
            num_of_pixels += 1;
        }
        cache.put(img, sumOfPixelBrightness/num_of_pixels);
        return sumOfPixelBrightness/num_of_pixels;
    }

    /**
     * method that convert Image To Ascii
     * //brightnessLevelArray is sorted!!! is not
     * @param numCharsInRow - Is the number of characters for each line in the ASCII image.
     * @param brightnessLevelArray - sorted brightness Level Array of characters
     *                             that can be used in the program.
     * @param mapBrightnessToCharacter - a map between double and characters that maps
     *                                 between the brightness Level to its character.
     * @return A two-dimensional array of ASCII characters representing an image
     *      (the same image obtained in the constructor).
     */
    private char[][] convertImageToAscii(int numCharsInRow, double[] brightnessLevelArray,
                                        Map<Double,Character> mapBrightnessToCharacter) {
        if (brightnessLevelArray.length == 0){
            return new char[0][0];
        }
        int pixels = img.getWidth() / numCharsInRow;
        char[][] asciiArt = new char[img.getHeight()/pixels][img.getWidth()/pixels];
        int row = 0;
        int col = 0;
        for(Image subImage : img.squareSubImagesOfSize(pixels)) {
            double averageValueOfPixelBrightness;
            if (cache.get(subImage) != null){
                averageValueOfPixelBrightness = cache.get(subImage);
            }
            else{
                averageValueOfPixelBrightness =  calculateAverageValueOfPixelBrightness(subImage);
            }
            //brightnessLevelArray is sorted
            char asciiChar = findClosetCharToBrightnessLevel(averageValueOfPixelBrightness, brightnessLevelArray,
                    mapBrightnessToCharacter);
            asciiArt[row][col] = asciiChar;
            col += 1;
            if (col >= img.getWidth()/pixels) {
                col = 0;
                row += 1;
            }
        }
        return asciiArt;
    }

    private char findClosetCharToBrightnessLevel(double brightnessLevel, double[] brightnessLevelArray ,
                                                 Map<Double,Character> mapBrightnessToCharacter) {
        //brightnessLevelArray is sorted
       double closetBrightnessLevel =  findClosest(brightnessLevelArray, brightnessLevel);
       return mapBrightnessToCharacter.get(closetBrightnessLevel);
    }


     ///////////////////////////////////////
    //on this part there are static function which are helpers
    // and not related to the specific instance:
    /////////////////////////////////

    /**
     * method finds maximum value in array.
     * assume array is not empty.
     * @param arr - an array of doubles
     * @return the max value in the array of doubles
     */
    private static double findMaxValue(double[] arr) {
        double max = arr[0];
        for (int currIndex = 0; currIndex < arr.length; currIndex++) {
            if (arr[currIndex] > max) {
                max = arr[currIndex];
            }
        }
        return max;
    }

    /**
     * method finds minimum value in array.
     * assume array is not empty
     * @param arr - an array of doubles
     * @return the min value in the array of doubles
     */
    private static double findMinValue(double[] arr) {
        double min = arr[0];
        for (int currIndex = 0; currIndex < arr.length; currIndex++) {
            if (arr[currIndex] < min) {
                min = arr[currIndex];
            }
        }
        return min;
    }

    /**
     *  partition- a helper function for the double quickSort
     * @param arrayTOSort - array TO Sort.
     * @param beginIndex - begin index
     * @param endIndex - end index
     * @return counter
     */
    private static int partition(double[] arrayTOSort, int beginIndex, int endIndex) {
        int pivot = endIndex;

        int counter = beginIndex;
        for (int i = beginIndex; i < endIndex; i++) {
            if (arrayTOSort[i] < arrayTOSort[pivot]) {
                double temp = arrayTOSort[counter];
                arrayTOSort[counter] = arrayTOSort[i];
                arrayTOSort[i] = temp;
                counter++;
            }
        }
        double temp = arrayTOSort[pivot];
        arrayTOSort[pivot] = arrayTOSort[counter];
        arrayTOSort[counter] = temp;
        return counter;
    }

    /**
     *  function for the double quickSort
     * @param arrayTOSort - array TO Sort.
     * @param beginIndex - begin index
     * @param endIndex - end index
     */
    private static void quickSort(double[] arrayTOSort, int beginIndex, int endIndex) {
        if (endIndex <= beginIndex) return;
        int pivot = partition(arrayTOSort, beginIndex, endIndex);
        quickSort(arrayTOSort, beginIndex, pivot-1);
        quickSort(arrayTOSort, pivot+1, endIndex);
    }

    /**
     * find Closest to target double in array.
     * assume arrayToFindIn not empty!!!
     * @param arrayToFindIn array To Find In Closest to target.
     * @param target - the double we look for his closet in array.
     * @return Closest double in the given array  to target.
     */
    private static double findClosest(double[] arrayToFindIn, double target)
    {
        int n = arrayToFindIn.length;

        // Corner cases
        if (target <= arrayToFindIn[0])
            return arrayToFindIn[0];
        if (target >= arrayToFindIn[n - 1])
            return arrayToFindIn[n - 1];

        // using binary search
        int i = 0, j = n, mid = 0;
        while (i < j) {
            mid = (i + j) / 2;

            if (arrayToFindIn[mid] == target)
                return arrayToFindIn[mid];

            if (target < arrayToFindIn[mid]) {
                if (mid > 0 && target > arrayToFindIn[mid - 1])
                    return getClosest(arrayToFindIn[mid - 1],
                            arrayToFindIn[mid], target);
                j = mid;
            }
            else {
                if (mid < n-1 && target < arrayToFindIn[mid + 1])
                    return getClosest(arrayToFindIn[mid],
                            arrayToFindIn[mid + 1], target);
                i = mid + 1;
            }
        }
        return arrayToFindIn[mid];
    }

    /**
     * method helps to findClosest
     * returns the Closest double from {firstVal, secondVal} to target.
     * @param firstVal - first value to check
     * @param secondVal - second value to check
     * @param target - the double we look for his closet in array.
     * @return Closest double from {firstVal, secondVal} to target.
     */
    private static double getClosest(double firstVal, double secondVal,
                                 double target)
    {
        if (target - firstVal >= secondVal - target)
            return secondVal;
        else
            return firstVal;
    }
}
