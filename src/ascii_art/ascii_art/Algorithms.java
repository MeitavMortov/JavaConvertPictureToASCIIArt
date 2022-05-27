package ascii_art;
import java.util.HashSet;
import java.util.Set;

/**
 * the class that contains my solutions for the algo part.
 */
public class Algorithms {

    private static final char ASCI_OF_FIRST_ENGLISH_LETTER = 97;

    /**
     *  considers the given array as a sort of linked list
     *  (possible because of the fact each integer is between 1 and n).
     *  I claim that a duplicate exists when a cycle does exist.
     *  Moreover, the duplicate is the entry point of the cycle
     *  so my aim in the algorithm is to find the entry point of the cycle
     *  inspired by : Floyd’s cycle-finding algorithm
     *  1. Initiate two pointers slowPointerValue and fastPointerValue
     *  2. For each step, slowPointerValue is moving one step at a time with:
     *  slowPointerValue = array[slow],
     *  whereas fastPointerValue is moving two steps at a time with
     *  fastPointerValue = array[array[fastPointerValue]]
     *  3. When slowPointerValue == fastPointerValue, we are in a cycle
     *  4.The entry point of this cycle will be the duplicate.
     *  Therefore: reset slow and move both pointers step by step until they are equals again.
     *  O(1) place complexity,couse there is no pace we use beside to pinters=== O(1)
     *  O(n) time complexity, couse in worst case we pass the arrey 2 times: O(2n) ===> O(n)
     * @param numList - numList an integers between 1-n and contains n+1 elements.
     * @return the duplicate integer
     */
    public static int findDuplicate(int[] numList) {
        //if there is no chance for duplicates:
        if (numList.length <= 1)
            return -1;
        //Initiate two pointers slowPointerValue and fastPointerValue:
        int slowPointerValue = numList[0];
        int fastPointerValue = numList[numList[0]];

        //For each step, slowPointerValue is moving one step at a time with:
        // slowPointerValue = array[slow],
        //whereas fastPointerValue is moving two steps at a time with
        // fastPointerValue = array[array[fastPointerValue]]
        while (fastPointerValue != slowPointerValue) { //While didn't find a cycle
            slowPointerValue = numList[slowPointerValue];
            fastPointerValue = numList[numList[fastPointerValue]];
        }
        // reset slow
        slowPointerValue = 0;
        // move both pointers step by step until they are equals again
        while (fastPointerValue != slowPointerValue) {
            slowPointerValue = numList[slowPointerValue];
            fastPointerValue = numList[fastPointerValue];
        }
        return slowPointerValue;
    }



    /**
     * function that find unique Morse Representations:
     * Given a list of English words, the function will return how many unique Morse
     * codes there are in this list.
     * (i.e. if two words are translated into Morse in the same way,
     * their code will be counted once One).
     * @param words - list of English words.
     * @return how many unique Morse codes there are in this list.
     */
    public static int uniqueMorseRepresentations(String[] words){
        String[] morseTableForEnglishLetters =
                new String[] {".-","-...","-.-.","-..",
                        ".","..-.","--.","....","..",".---",
                        "-.-",".-..","--","-.","---",".--.",
                        "--.-",".-.","...","-","..-","...-",
                        ".--","-..-","-.--","--.."};
        //init a set of unique codes:
        Set<String> uniqueMorseCodes = new HashSet<>();
        for (String word: words) {
            //translate each word in the given words to its morse code:
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < word.length(); ++ i) {
                stringBuilder.append(morseTableForEnglishLetters[word.charAt(i)
                        - ASCI_OF_FIRST_ENGLISH_LETTER]);
            }
            // add the morse code of word to the set
            uniqueMorseCodes.add(stringBuilder.toString());
        }
        //set contains only the uniques morse codes
        return uniqueMorseCodes.size();
    }

}

