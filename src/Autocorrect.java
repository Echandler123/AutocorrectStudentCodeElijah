import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author Elijah Chandler
 */
public class Autocorrect {

    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */
    private String[] words;
    private int threshold;
    public Autocorrect(String[] words, int threshold) {
        this.words = words;
        this.threshold = threshold;
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        ArrayList<String> suggestions = new ArrayList<>();
        // Find all words within threshold
        for(int i = 0; i < words.length; i++) {
            if(levDist(typed, words[i]) <= threshold) {
                suggestions.add(words[i]);
            }
        }
        // Sort method from LLM that sorts suggestions by levenshtein distance, then alphabetically
        suggestions.sort((a, b) -> {
            int distA = levDist(typed, a);
            int distB = levDist(typed, b);
            if(distA != distB) {
                return Integer.compare(distA, distB);
            }
            return a.compareTo(b);
        });
        // Convert ArrayList to String array
        if(suggestions.size() > 0) {
            String[] result = new String[suggestions.size()];
            for(int i = 0; i < suggestions.size(); i++) {
                result[i] = suggestions.get(i);
            }
            return result;
        }
        return new String[0];
    }
    public int levDist(String s1, String s2) {
        // Handle empty strings
        if(s1.isEmpty()) {
            return 0;
        }
        if(s2.isEmpty()) {
            return 0;
        }
        // Create a 2D array to store distances
        int[][] distances = new int[s1.length()+1][s2.length()+1];
        // Initialize the first row and column
        for(int i = 0; i < s1.length() + 1; i++) {
            distances[i][0] = i;
        }
        for(int j = 0; j < s2.length() + 1; j++) {
            distances[0][j] = j;
        }
        // Fill in the rest of the array
        for(int i = 1; i < s1.length() + 1; i++) {
            for(int j = 1; j < s2.length() + 1; j++) {
                // If the characters are the same, no edit is needed
                if(s1.charAt(i-1) == s2.charAt(j-1)) {
                    distances[i][j] = distances[i-1][j-1];
                }
                // If the characters are different, find the minimum edit distance
                else {
                    distances[i][j] = Math.min(Math.min(distances[i-1][j] + 1, distances[i][j-1] +1),distances[i-1][j-1] + 1);
                }
            }
        }
        // Return the bottom-right value, which is the edit distance
        return distances[s1.length()][s2.length()];
    }

    /**
     * Loads a dictionary of words from the provided textfiles in the dictionaries directory.
     * @param dictionary The name of the textfile, [dictionary].txt, in the dictionaries directory.
     * @return An array of Strings containing all words in alphabetical order.
     */
    private static String[] loadDictionary(String dictionary)  {
        try {
            String line;
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionaries/" + dictionary + ".txt"));
            line = dictReader.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = dictReader.readLine();
                words[i] = line;
            }
            return words;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        // Load the dictionary
        String[] words = loadDictionary("large");
        Scanner scanner = new Scanner(System.in);
        // Prompt the user to enter a word
        System.out.println("Enter a word: ");
        String typed = scanner.nextLine();
        int threshold = 2;
        // Create a new instance of the Autocorrect class
        Autocorrect auto = new Autocorrect(words, threshold);
        // Run the test and print the suggestions
        while(!typed.equals("")) {
            System.out.println("Suggestions: ");
            String[] suggestions = auto.runTest(typed);
            for(String suggestion: suggestions) {
                System.out.println(suggestion);
            }
            System.out.println("Enter a word to check: ");
            typed = scanner.nextLine();
        }
    }
}
