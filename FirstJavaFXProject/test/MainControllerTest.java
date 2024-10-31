package application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainControllerTest {

    /**
     * Test to verify that performSpellCheck() correctly identifies no errors in a text without spelling mistakes.
     * This test checks the correctness of spell checking functionality in scenarios with perfect spelling.
     */
    @Test
    void testPerformSpellCheckNoErrors() {
        // Arrange: Create a MainController instance and define a test string without spelling errors.
        MainController mainController = new MainController();
        String contentWithoutErrors = "This is a test without errors.";

        // Act: Perform spell check on the test string.
        mainController.performSpellCheck(contentWithoutErrors);

        // Assert: Validate that the number of identified misspellings and miscapitalizations are zero.
        assertEquals(0, mainController.getNumMisspellings(), "No misspellings should be found.");
        assertEquals(0, mainController.getNumMiscapitalizations(), "No miscapitalizations should be found.");
        assertEquals(0, mainController.getNumSuggestedCorrections(), "No suggested corrections should be found.");
    }

    /**
     * Test to verify that performSpellCheck() correctly identifies and counts spelling errors in a text.
     * This test is crucial for ensuring the spell check functionality works for texts with errors.
     */
    @Test
    void testPerformSpellCheckWithErrors() {
        // Arrange: Create a MainController instance and define a test string with a misspelled word.
        MainController mainController = new MainController();
        String contentWithErrors = "This is a test with misspelled word mistaake.";

        // Act: Perform spell check on the test string.
        mainController.performSpellCheck(contentWithErrors);

        // Assert: Validate that the number of identified misspellings is greater than zero.
        assertTrue(mainController.getNumMisspellings() > 0, "Misspellings should be identified.");
        // Additional assertions can be added here based on the specific behavior of your application.
    }

    /**
     * Integration test to verify the interaction between MainController and SpellChecker.
     * This test ensures that MainController correctly utilizes the SpellChecker for identifying spelling errors.
     */
    @Test
    void testPerformSpellCheckIntegration() {
        // Arrange: Create a MainController instance.
        MainController mainController = new MainController();

        // Act: Perform spell check on a test string with a misspelled word.
        mainController.performSpellCheck("This is a test with misspelled word mistaake.");

        // Assert: Validate that the number of misspellings is as expected.
        assertEquals(1, mainController.getNumMisspellings(), "Exactly one misspelling should be identified.");

        // Additional assertions can be added here to verify other expected behaviors of the MainController after performing a spell check.
        // This test also indirectly tests the interaction between MainController and SpellChecker.
    }
}

