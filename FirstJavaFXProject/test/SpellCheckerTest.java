package test;

import static org.junit.jupiter.api.Assertions.*;

import application.SpellChecker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class SpellCheckerTest {

    @Test
    void testSpellCheckerInitialization() {
        // Arrange
        SpellChecker spellChecker = new SpellChecker();

        // Assert
        assertNotNull(spellChecker, "SpellChecker object should not be null");
    }

    @Test
    void testResetMetrics() {
        // Arrange
        SpellChecker spellChecker = new SpellChecker();

        // Act .
        spellChecker.resetMetrics();

        // Assert.
        assertEquals(0, spellChecker.getNumCharacters());
        assertEquals(0, spellChecker.getNumLines());
        assertEquals(0, spellChecker.getNumWords());
        assertEquals(0, spellChecker.getNumMisspellings());
        assertEquals(0, spellChecker.getNumMiscapitalizations());
        assertEquals(0, spellChecker.getNumDoubleWords());
        assertEquals(0, spellChecker.getNumManualCorrections());
        assertEquals(0, spellChecker.getNumSuggestedCorrections());
    }
    @Test
    void testConstructor() {
        // Arrange
        int startIndex = 0;
        int endIndex = 5;
        String type = "Misspelling";

        // Act
        SpellChecker.SpellCheckError error = new SpellChecker.SpellCheckError(startIndex, endIndex, type);

        // Assert
        assertEquals(startIndex, error.getStartIndex());
        assertEquals(endIndex, error.getEndIndex());
        assertEquals(type, error.getType());
    }

    @Test
    void testEquality() {
        // Arrange
        SpellChecker.SpellCheckError error1 = new SpellChecker.SpellCheckError(0, 5, "Misspelling");
        SpellChecker.SpellCheckError error2 = new SpellChecker.SpellCheckError(0, 5, "Misspelling");
        SpellChecker.SpellCheckError error3 = new SpellChecker.SpellCheckError(5, 10, "Miscapitalization");

        // Assert
        assertEquals(error1, error2, "Instances with the same values should be equal");
        assertNotEquals(error1, error3, "Instances with different values should not be equal");
    }
    
    
    

    @Test
    void testUpdateCorrectionMetricsForMisspelling() {
        // Arrange
        SpellChecker spellChecker = new SpellChecker();
        spellChecker.resetMetrics();

        // Act
        String content = "Thsi is a misspelled word.";
        List<SpellChecker.SpellCheckError> errors = spellChecker.detectErrors(content);
        SpellChecker.SpellCheckError misspellingError = errors.get(0);
        String correction = "This"; // Correct the misspelling
        spellChecker.updateCorrectionMetrics(content, misspellingError, correction);

        // Assert
        assertEquals(1, spellChecker.getNumManualCorrections());
        assertEquals(1, spellChecker.getNumSuggestedCorrections());
    }

    @Test
    void testIsHtmlTag() {
        // Arrange
        SpellChecker spellChecker = new SpellChecker();

        // Act
        boolean result = spellChecker.isHtmlTag("<p>");

        // Assert
        assertTrue(result, "The provided string is an HTML tag");
    }

    @Test
    void testDetectErrors() {
        // Arrange
        SpellChecker spellChecker = new SpellChecker();

        // Act
        List<SpellChecker.SpellCheckError> errors = spellChecker.detectErrors("Thsi is a misspelled word.");

        // Assert
        assertEquals(1, errors.size(), "There should be one detected error");
        assertEquals("Misspelling", errors.get(0).getType(), "The detected error should be a misspelling");
    }

    // Add more test methods for other functionalities...
   
    @Test
     void testUpdateErrorMetrics() {
    //      Arrange
          SpellChecker spellChecker = new SpellChecker();
             spellChecker.resetMetrics();

    
            List<SpellChecker.SpellCheckError> errors = new ArrayList<>();
            errors.add(new SpellChecker.SpellCheckError(0, 5, "Misspelling"));
             errors.add(new SpellChecker.SpellCheckError(10, 15, "Miscapitalization"));
        errors.add(new SpellChecker.SpellCheckError(20, 25, "DoubleWord"));
           

          // Assert
           assertEquals(1, spellChecker.getNumMisspellings());
           assertEquals(1, spellChecker.getNumMiscapitalizations());
           assertEquals(1, spellChecker.getNumDoubleWords());
      }
       
      @Test
     void testCorrectError() {
           // Arrange
           SpellChecker spellChecker = new SpellChecker();
           spellChecker.resetMetrics();
           String content = "Thsi is a misspelled word.";
           SpellChecker.SpellCheckError misspellingError = new SpellChecker.SpellCheckError(0, 5, "Misspelling");

    //     // Act
           String correctedContent = spellChecker.correctError(content, misspellingError, "This");

          // Assert
          assertEquals("This is a misspelled word.", correctedContent, "The error should be corrected");
      }

       @Test
     void testSuggestMiscapitalizationCorrections() {
        // Arrange
        SpellChecker spellChecker = new SpellChecker();

         // Act
          List<String> suggestions = spellChecker.suggestMiscapitalizationCorrections("woRd");

           // Assert
            assertEquals(3, suggestions.size(), "There should be three suggestions");
         assertTrue(suggestions.contains("Word"), "Suggestion should include capitalized version");
          assertTrue(suggestions.contains("word"), "Suggestion should include lowercase version");
        assertTrue(suggestions.contains("WORD"), "Suggestion should include all uppercase version");
      }

     @Test
    void testSuggestMisspellingCorrections() {
        // Arrange
          SpellChecker spellChecker = new SpellChecker();

       // Act
         List<String> suggestions = spellChecker.suggestMisspellingCorrections("helo");

         // Assert
           assertEquals(2, suggestions.size(), "There should be two suggestions");
           assertTrue(suggestions.contains("hello"), "Suggestion should include correct spelling");
           assertTrue(suggestions.contains("helo"), "Suggestion should include original spelling");
      }

     @Test
      void testSuggestDoubleWordCorrections() {
          // Arrange
          SpellChecker spellChecker = new SpellChecker();

    //     // Act
          List<String> suggestions = spellChecker.suggestDoubleWordCorrections("word word");

          // Assert
         assertTrue(suggestions.isEmpty(), "There should be no suggestions for double words");
      }
     
     @Test
     void testAddToUserDictionary() {
         // Arrange
         SpellChecker spellChecker = new SpellChecker();
         spellChecker.resetMetrics();
         String content = "Thsi is a misspelled word.";
         SpellChecker.SpellCheckError misspellingError = new SpellChecker.SpellCheckError(0, 3, "Misspelling");

         // Act and Assert
         assertDoesNotThrow(() -> spellChecker.addToUserDictionary(misspellingError, content),
                 "Adding to user dictionary should not throw an exception");
     }
     
     @Test
     void testLoadDictionary() {
         // Act
         List<String> dictionary = SpellChecker.loadDictionary();

         // Assert
         assertNotNull(dictionary, "Dictionary should not be null");
         assertFalse(dictionary.isEmpty(), "Dictionary should not be empty");
         assertTrue(dictionary.contains("example"), "Dictionary should contain a sample word (adjust as per your dictionary content)");
     }
     
     
    
     
     
     
     
     
     
     
     
     
     
     
     
}
