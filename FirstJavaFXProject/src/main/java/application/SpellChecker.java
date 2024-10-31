package application;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *  Spell Checker application.
 * Manages the user interface and interacts with the SpellChecker class.
 *
 * 
 * @version 1.0
 */


public class SpellChecker {

    // Metrics variables
    private int numCharacters;
    private int numLines;
    private int numWords;
    private int numMisspellings;
    private int numMiscapitalizations;
    private int numDoubleWords;
    private int numManualCorrections;
    private int numSuggestedCorrections;

    public SpellChecker() {
        // Initialize metrics variables
        numCharacters = 0;
        numLines = 0;
        numWords = 0;
        numMisspellings = 0;
        numMiscapitalizations = 0;
        numDoubleWords = 0;
        numManualCorrections = 0;
        numSuggestedCorrections = 0;
    }
    // Method to update metrics for number of characters, lines, and words
    private void updateTextMetrics(String content) {
        numCharacters = content.length();
        numLines = content.split("\r\n|\r|\n").length;
        numWords = content.split("\\s+").length;
    }


    public void resetMetrics() {
        // Reset all metrics variables to zero
        numCharacters = 0;
        numLines = 0;
        numWords = 0;
        numMisspellings = 0;
        numMiscapitalizations = 0;
        numDoubleWords = 0;
        numManualCorrections = 0;
        numSuggestedCorrections = 0;
    }

    // Method to update metrics for each type of error
    private void updateErrorMetrics(List<SpellCheckError> errors) {
        for (SpellCheckError error : errors) {
            switch (error.getType()) {
                case "Misspelling":
                    numMisspellings++;
                    break;
                case "Miscapitalization":
                    numMiscapitalizations++;
                    break;
                case "DoubleWord":
                    numDoubleWords++;
                    break;
                // Add cases for other error types as needed
            }
        }
    }

    // Method to update metrics for each type of correction.
    public void updateCorrectionMetrics(String content, SpellCheckError error, String correction) {
        // Use the error type to determine the type of correction made
        switch (error.getType()) {
            case "Misspelling":
            case "MixedCapitalization":
            case "Miscapitalization":
                numManualCorrections++;
                break;
            case "DoubleWord":
                // No specific metric for double word corrections
                break;
            // Add cases for other error types as needed
        }

        // Check if the correction matches the original word or is a suggested correction
        if (correction.equals(content.substring(error.getStartIndex(), error.getEndIndex()))) {
            numManualCorrections++; // Counted as a manual correction
        } else {
            numSuggestedCorrections++; // Counted as a suggested correction
        }
    }
    public int getNumCharacters() {
        return numCharacters;
    }

    public int getNumLines() {
        return numLines;
    }

    public int getNumWords() {
        return numWords;
    }

    public int getNumMisspellings() {
        return numMisspellings;
    }

    public int getNumMiscapitalizations() {
        return numMiscapitalizations;
    }

    public int getNumDoubleWords() {
        return numDoubleWords;
    }

    public int getNumManualCorrections() {
        return numManualCorrections;
    }

    public int getNumSuggestedCorrections() {
        return numSuggestedCorrections;
    }



 // Class implementation...
    
    /**
     * This is a Javadoc comment for the constructor.
     * It provides information about the parameters and functionality.
     */


    // Placeholder class representing a detected error
    public static class SpellCheckError {
        private int startIndex;
        private int endIndex;
        private String type;
        /**
         * Constructs a SpellCheckError object.
         *
         * @param startIndex The index of the first character of the error in the text.
         * @param endIndex   The index of the last character of the error in the text.
         * @param type       The type of the error (e.g., spelling, grammar).
         *                   Should be a descriptive string indicating the error type.
         * @throws IllegalArgumentException If startIndex is greater than endIndex.
         */

        public SpellCheckError(int startIndex, int endIndex, String type) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.type = type;
        }

        /**
         * Gets the start index of the document range.
         *
         * @return The start index of the document range.
         */
        public int getStartIndex() {
            return startIndex;
        }
        /**
         * Gets the end index of the document range.
         *
         * @return The end index of the document range.
         */
        public int getEndIndex() {
            return endIndex;
        }
        /**
         * Gets the type associated with the document range.
         *
         * @return The type of the document range.
         */
        public String getType() {
            return type;
        }
    }


    private static List<String> dictionary = loadDictionary(); // Load dictionary from a file or another source
    private static List<String> userDictionary = loadUserDictionary(); //loads the userdictionary
    /**
     * Loads the dictionary from a file or another source.
     * The dictionary is typically used for spell checking.
     *
     * @return A List of Strings representing the loaded dictionary.
     * @throws IOException If an I/O error occurs while reading the dictionary file.
     */
    
    static List<String> loadDictionary() {
        List<String> loadedDictionary = new ArrayList<>();

        try {
            // Assuming dictionary.txt is in the same package as the SpellChecker class or in the src directory
            InputStream inputStream = SpellChecker.class.getResourceAsStream("/dictionary.txt");

            if (inputStream != null) {
                InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(streamReader);

                // Read lines from the file
                String line;
                while ((line = reader.readLine()) != null) {
                    loadedDictionary.add(line);
                }

                // Close the streams
                inputStream.close();
                streamReader.close();
                reader.close();
            } else {
                System.err.println("File not found: dictionary.txt");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., log it, show an alert) based on your application's needs
        }

        return loadedDictionary;
    }

    /**
     * Loads the dictionary from a file or another source.
     * The dictionary is typically used for spell checking.
     *
     * @return A List of Strings representing the loaded dictionary.
     * @throws IOException If an I/O error occurs while reading the User dictionary file.
     */
    
    
    //Loads the user dictionary
    private static List<String> loadUserDictionary() {
        List<String> loadedDictionary = new ArrayList<>();

        try {
            // Assuming dictionary.txt is in the same package as the SpellChecker class or in the src directory
            InputStream inputStream = SpellChecker.class.getResourceAsStream("/userDictionary.txt");

            if (inputStream != null) {
                InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(streamReader);

                // Read lines from the file
                String line;
                while ((line = reader.readLine()) != null) {
                    loadedDictionary.add(line);
                }

                // Close the streams
                inputStream.close();
                streamReader.close();
                reader.close();
            } else {
                System.err.println("File not found: userDictionary.txt");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., log it, show an alert) based on your application's needs
        }

        return loadedDictionary;
    }


    public  boolean isHtmlTag(String word) {
        return word.matches("<\\s*\\/?\\s*[a-zA-Z]+\\s*[^>]*>");
    }
    
    /**
     * Detects spelling and capitalization errors in the provided content.
     * Uses regular expressions to find HTML tags, misspelled words, miscapitalizations,
     * mixed capitalizations within a word, and double words.
     *
     * @param content The text content to spell-check.
     * @return A List of SpellCheckError objects representing detected errors.
     */
    
    
    

    public List<SpellCheckError> detectErrors(String content) {
        List<SpellCheckError> errors = new ArrayList<>();

        // Use a regex pattern to find HTML tags
        Pattern htmlTagPattern = Pattern.compile("<[^>]+>");
        Matcher htmlTagMatcher = htmlTagPattern.matcher(content);

        // Find HTML tags and store their start and end indices
        List<int[]> htmlTagIndices = new ArrayList<>();
        while (htmlTagMatcher.find()) {
            int startIndex = htmlTagMatcher.start();
            int endIndex = htmlTagMatcher.end();
            htmlTagIndices.add(new int[]{startIndex, endIndex});
        }

        // Detect misspellings (words not found in the dictionary)
        String[] words = content.split("\\b[^a-zA-Z]+\\b");
        List<int[]> htmlTagstuff = extractHtmlTagIndices(content); // Extract HTML tag indices

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            // Skip purely numeric sequences or words containing digits
            if (word.matches("[0-9]+") || word.matches(".*\\d.*")) {
                continue;
            }

            // Skip HTML tags
            if (isHtmlTag(word) || isInsideHtmlTag(i, htmlTagstuff)) {
                continue;
            }

            if (!isWordInDictionary(word)) {
                int startIndex = content.indexOf(word);
                int endIndex = startIndex + word.length();
                errors.add(new SpellCheckError(startIndex, endIndex, "Misspelling"));
            }
        }


        // Detect miscapitalizations
        Pattern sentenceStartPattern = Pattern.compile("(?<=\\.|\\?|!)\\s*\\b[a-z]");
        Matcher sentenceStartMatcher = sentenceStartPattern.matcher(content);
        while (sentenceStartMatcher.find()) {
            int startIndex = sentenceStartMatcher.start() + 1; // Adjust for the space
            int endIndex = startIndex + 1;
            errors.add(new SpellCheckError(startIndex, endIndex, "Miscapitalization"));
        }

        // Detect mixed capitalization within a word
        Pattern mixedCapitalizationPattern = Pattern.compile("\\b[A-Za-z]*[a-z]+[A-Z]+[A-Za-z]*\\b");
        Matcher mixedCapitalizationMatcher = mixedCapitalizationPattern.matcher(content);
        while (mixedCapitalizationMatcher.find()) {
            errors.add(new SpellCheckError(mixedCapitalizationMatcher.start(), mixedCapitalizationMatcher.end(), "MixedCapitalization"));
        }

        // Detect double words
        Pattern doubleWordPattern = Pattern.compile("\\b(\\w+)\\s+\\1\\b");
        Matcher doubleWordMatcher = doubleWordPattern.matcher(content);
        while (doubleWordMatcher.find()) {
            errors.add(new SpellCheckError(doubleWordMatcher.start(), doubleWordMatcher.end(), "DoubleWord"));
        }

        // Update error metrics
        updateErrorMetrics(errors);

        return errors;
    }

    /**
     * Extracts the start and end indices of the HTML tags in the given content.
     * 
     * @param content The text content to extract HTML tag indices from.
     * @return A List of int arrays where each array represents the start and end indices of an HTML tag.
     */
    
    
    
    private static List<int[]> extractHtmlTagIndices(String content) {
        List<int[]> indices = new ArrayList<>();
        Pattern tagPattern = Pattern.compile("<[^>]+>");
        Matcher tagMatcher = tagPattern.matcher(content);

        while (tagMatcher.find()) {
            int startIndex = tagMatcher.start();
            int endIndex = tagMatcher.end();
            indices.add(new int[]{startIndex, endIndex});
        }

        return indices;
    }
    
    /**
     * Checks if the given word index is within an HTML tag.
     *
     * @param wordIndex       The index of the word to check.
     * @param htmlTagIndices  A List of int arrays representing start and end indices of HTML tags.
     * @return {@code true} if the word is inside an HTML tag, {@code false} otherwise.
     */
    
    

    private static boolean isInsideHtmlTag(int wordIndex, List<int[]> htmlTagIndices) {
        // Check if the word is within an HTML tag
        for (int[] tagIndices : htmlTagIndices) {
            if (tagIndices[0] <= wordIndex && wordIndex <= tagIndices[1]) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if the given word is in the dictionary or user dictionary.
     * Handles hyphenated words by checking each part individually.
     *
     * @param word The word to check.
     * @return {@code true} if the word is in the dictionary or user dictionary, {@code false} otherwise.
     */
    private static boolean isWordInDictionary(String word) {
        // Check the original word
        if (dictionary.contains(word.toLowerCase())) {
            return true;
        }

        if (userDictionary.contains(word.toLowerCase())) {
            return true;
        }

        // Check hyphen-separated parts
        String[] hyphenatedParts = word.split("-");
        for (String part : hyphenatedParts) {
            if (!dictionary.contains(part.toLowerCase())) {
                return false;
            }

            if (!userDictionary.contains(part.toLowerCase())) {
                return false;
            }
        }

        return true;
    }
    
    
    /**
     * Corrects a spelling or capitalization error in the provided content.
     * Uses the information from the given SpellCheckError object to identify the error location
     * and replaces the error with the provided correction.
     *
     * @param content    The text content to correct.
     * @param error      The SpellCheckError object representing the error to be corrected.
     * @param correction The correction to apply to the identified error.
     * @return A corrected version of the content with the specified error corrected.
     */
    

    public static String correctError(String content, SpellCheckError error, String correction) {
        // Implement correction logic based on the error type
        // Use the startIndex and endIndex to identify the error location in the content
        // Replace the error with the provided correction
        switch (error.getType()) {
            case "Misspelling":
            case "MixedCapitalization":
                return content.substring(0, error.getStartIndex()) +
                        correction +
                        content.substring(error.getEndIndex());
            case "Miscapitalization":
                // Correct the miscapitalization by capitalizing the detected word
                return content.substring(0, error.getStartIndex()) +
                        correction.toUpperCase() +
                        content.substring(error.getEndIndex());
            case "DoubleWord":
                // Remove the second instance of the double word
                return content.substring(0, error.getStartIndex()) +
                        content.substring(error.getEndIndex());
            default:
                return content;
        }
    }
    
    
    /**
     * Generates suggestions for correcting a miscapitalized word.
     * Suggestions include the capitalized version of the word,
     * the word with no capitalization, and the word with all letters capitalized.
     *
     * @param word The miscapitalized word.
     * @return A List of suggested corrections for the miscapitalized word.
     */
    

    public List<String> suggestMiscapitalizationCorrections(String word) {
        List<String> suggestions = new ArrayList<>();

        // Suggest the capitalized version of the word
        suggestions.add(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());

        // Suggest the word with no capitalization
        suggestions.add(word.toLowerCase());

        // Suggest the word with all letters capitalized
        suggestions.add(word.toUpperCase());

        return suggestions;
    }
    /**
     * Generates suggestions for correcting a misspelled word.
     * Suggestions include words formed by removing each letter,
     * inserting each possible letter, and swapping each pair of consecutive letters.
     *
     * @param word The misspelled word.
     * @return A List of suggested corrections for the misspelled word.
     */
    
    

    // Another method for generating suggestions for misspellings
    // Another method for generating suggestions for misspellings
    public List<String> suggestMisspellingCorrections(String word) {
        List<String> suggestions = new ArrayList<>();

        // Generate suggestions by removing each letter
        for (int i = 0; i < word.length(); i++) {
            String removedWord = word.substring(0, i) + word.substring(i + 1);
            if (isWordInDictionary(removedWord)) {
                suggestions.add(removedWord);
            }
        }

        // Generate suggestions by inserting each possible letter
        for (int i = 0; i < word.length() + 1; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                String insertedWord = word.substring(0, i) + ch + word.substring(i);
                if (isWordInDictionary(insertedWord)) {
                    suggestions.add(insertedWord);
                }
            }
        }

        // Generate suggestions by swapping each pair of consecutive letters
        char[] chars = word.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            char temp = chars[i];
            chars[i] = chars[i + 1];
            chars[i + 1] = temp;

            String swappedWord = new String(chars);
            if (isWordInDictionary(swappedWord)) {
                suggestions.add(swappedWord);
            }

            // Swap back to the original positions
            temp = chars[i];
            chars[i] = chars[i + 1];
            chars[i + 1] = temp;
        }

        return suggestions;
    }
     
    /**
     * Generates suggestions for correcting a double word error.
     * For double words, no suggestions are needed, as the user can simply opt to remove the second instance.
     *
     * @param word The double word.
     * @return An empty List, as no suggestions are needed for double words.
     */

    // Another method for generating suggestions for double words
    public List<String> suggestDoubleWordCorrections(String word) {
        // For double words, no suggestions are needed
        // The user can simply opt to remove the offending second instance
        return new ArrayList<>();
    }

    /**
     * Adds a misspelled word to the user dictionary file based on the given error and content.
     *
     * @param error   The SpellCheckError object representing the misspelling.
     * @param content The text content containing the misspelled word.
     */
    public static void addToUserDictionary(SpellCheckError error, String content) {
        // Extract the misspelled word from the content based on the error indices
        String word = content.substring(error.getStartIndex(), error.getEndIndex());

        // Add the word to the user dictionary file
        try {
            // Assuming userdictionary.txt is in the same package as the SpellChecker class or in the src directory
            FileWriter writer = new FileWriter("userdictionary.txt", true); // true for append mode
            writer.write(word.toLowerCase() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., log it, show an alert) based on your application's needs
        }
    }


    //catts





}
