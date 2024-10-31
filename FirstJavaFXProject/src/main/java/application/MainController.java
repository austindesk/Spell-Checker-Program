package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;


/**
 * Main controller for the Spell Checker application.
 * Manages the user interface and interacts with the SpellChecker class.
 *
 * @version 1.0
 */


public class MainController {
	private SpellChecker spellChecker = new SpellChecker();
	private SpellChecker.SpellCheckError selectedError;
	 private final int contextLength = 700; // Or your desired context length
	 private boolean filterHtmlTags = false;

    @FXML TextArea textArea;

    private File selectedFile;

    @FXML
    private Button aboutButton; // Reference to the About button in Settings.fxml

    @FXML
    private Button helpButton; // Reference to the Help button in Settings.fxml

    @FXML
    private Button contactButton; // Reference to the Contact button in Settings.fxml
    // Add these variables to track correction metrics
    private int numManualCorrections = 0;
    private int numSuggestedCorrections = 0;

    // Variables to track error metrics
    private int numMisspellings = 0;
    private int numMiscapitalizations = 0;
    /**
     * Gets the number of manual corrections made.
     *
     * @return The number of manual corrections.
     */
    // Getter methods for correction metrics
    public int getNumManualCorrections() {
        return numManualCorrections;
    }
    /**
     * Gets the number of corrections suggested by the system.
     *
     * @return The number of suggested corrections.
     */
    public int getNumSuggestedCorrections() {
        return numSuggestedCorrections;
    }

    /**
     * Gets the number of misspellings detected.
     *
     * @return The number of misspellings.
     */
    // Getter methods for error metrics
    public int getNumMisspellings() {
        return numMisspellings;
    }
    /**
     * Gets the number of miscapitalizations detected.
     *
     * @return The number of miscapitalizations.
     */
    public int getNumMiscapitalizations() {
        return numMiscapitalizations;
    }
    
    /**
     * Resets the correction metrics to zero.
     */
    // Reset method for correction metrics
    public void resetCorrectionMetrics() {
        numManualCorrections = 0;
        numSuggestedCorrections = 0;
    }

    /**
     * Event handler for the button click that opens a FileChooser to select a text or HTML file.
     * Reads the content of the selected file, resets spell checker and correction metrics,
     * optionally filters HTML tags, and sets the content to the TextArea.
     *
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    private void butclicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"),
                new ExtensionFilter("HTML Files", "*.html", "*.htm")
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedFile = file;
            try {
                // Reset the metrics when a new file is selected
                spellChecker.resetMetrics();
                resetCorrectionMetrics();
                String content = new String(Files.readAllBytes(file.toPath()));

                // Determine whether to filter HTML tags based on the file extension
                filterHtmlTags = isHtmlFile(file);

                // Optionally, prompt the user to filter HTML tags if the file is an HTML file
                if (filterHtmlTags) {
                    boolean userWantsToFilterTags = promptHtmlTagFiltering();
                    if (userWantsToFilterTags) {
                        content = filterHtmlTags(content);
                    }
                }

                // Set the new content to the textArea
                textArea.setText(content);
                spellChecker.resetMetrics();


            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog("Error reading file", "An error occurred while reading the file.");
            }
        }
    }
    
    
    
    /**
     * Filters HTML tags from the given content using a regular expression.
     *
     * @param content The text content containing HTML tags.
     * @return The content with HTML tags removed.
     */

    
    private String filterHtmlTags(String content) {
        // Use a regular expression to remove HTML tags
        return content.replaceAll("<[^>]*>|<\\s*\\/\\s*[^>]+>|<[^>]+\\s*\\/\\s*>", "");
    }
    
    
    /**
     * Checks whether the given file has an HTML extension (html or htm).
     *
     * @param file The File object representing the file to check.
     * @return {@code true} if the file has an HTML extension, {@code false} otherwise.
     */
    
    private boolean isHtmlFile(File file) {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("html") || extension.equals("htm");
    }
    /**
     * Prompts the user whether to filter HTML tags in the document or not.
     *
     * @return {@code true} if the user chooses to filter HTML tags, {@code false} otherwise.
     */

    private boolean promptHtmlTagFiltering() {
        // Prompt the user whether to filter HTML tags or not
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("HTML Tag Filtering");
        alert.setHeaderText("Do you want to filter HTML tags in the document?");
        alert.setContentText("Choose your option:");

        ButtonType filterButtonType = new ButtonType("Filter HTML Tags");
        ButtonType skipFilterButtonType = new ButtonType("Skip HTML Tags Filtering");
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(filterButtonType, skipFilterButtonType, cancelButtonType);

        Optional<ButtonType> result = alert.showAndWait();

        // Process the user's choice
        return result.map(buttonType -> buttonType == filterButtonType).orElse(false);
    }

    
    
    @FXML
    private void filterFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"),
                new ExtensionFilter("HTML Files", "*.html", "*.htm")
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedFile = file;
            try {
                String content = new String(Files.readAllBytes(file.toPath()));

                // Optionally, prompt the user to filter HTML tags if the file is an HTML file
                filterHtmlTags = isHtmlFile(file);

                // Apply HTML tag filtering if needed
                if (filterHtmlTags) {
                    content = filterHtmlTags(content);
                }

                textArea.setText(content);

            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog("Error reading file", "An error occurred while reading the file.");
            }
        }
    }

    
    /**
     * Performs a spell check on the content of the TextArea.
     * Invokes the spellChecker to detect errors, highlights them in the text area,
     * and displays a message indicating the spell check result.
     *
     * @param content The text content to perform the spell check on.
     */


    @FXML
    private void performSpellCheck(ActionEvent event) {
        String content = textArea.getText();
        performSpellCheck(content);
    }

    void performSpellCheck(String content) {
        // Spell check the content
        List<SpellChecker.SpellCheckError> errors = spellChecker.detectErrors(content);
        highlightErrors(errors);

        // You can also display a message or take additional actions based on the errors found
        if (errors.isEmpty()) {
            showInfoDialog("Spell Check Result", "No spelling errors found.");
        } else {
            showErrorDialog("Spell Check Result", errors.size() + " spelling error(s) found.");
        }
    }


    /**
     * Handles the action event when the exit button is clicked.
     * Displays a confirmation dialog and closes the application if the user chooses to exit.
     *
     * @param event The ActionEvent triggered by clicking the exit button.
     */
    @FXML
    private void Exitbut(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Note: Any unsaved changes made might be lost.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }
    /**
     * Handles the action event for the Save File button.
     * Saves the content of the TextArea to the selected file.
     * Displays an error dialog if an issue occurs during the saving process.
     *
     * @param event The ActionEvent triggered by the Save File button.
     */
    @FXML
    private void saveFile(ActionEvent event) {
        if (selectedFile != null) {
            try {
            	 // Spell check the content before saving
               // List<SpellChecker.SpellCheckError> errors = spellChecker.detectErrors(textArea.getText());
              //  highlightErrors(errors);
                Files.write(selectedFile.toPath(), textArea.getText().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog("Error saving file", "An error occurred while saving the file.");
            }
        } else {
            showInfoDialog("No file selected", "No file selected to save.");
        }
    }
    /**
     * Handles the action event for the Open Settings button.
     * Loads the Settings page using a FXMLLoader and displays it in a new scene.
     * Displays an error dialog if an issue occurs during the loading process.
     *
     * @param event The ActionEvent triggered by the Open Settings button.
     */
    @FXML
    private void openSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"));
            Parent settingsParent = loader.load();
            Scene settingsScene = new Scene(settingsParent);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(settingsScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error loading settings", "An error occurred while loading the settings page.");
        }
        
        
    }
    
    /**
     * Handles adding a word to the user dictionary based on a SpellCheckError.
     *
     * @param error The SpellCheckError containing information about the word to add to the dictionary.
     */
    
    private void handleAddToDictionary(SpellChecker.SpellCheckError error) {
        SpellChecker.addToUserDictionary(error, textArea.getText());

    }


    /**
     * Initializes the error preview when an error is detected in the spell-checked text.
     * Displays suggestions and corrective actions for the user to choose from.
     *
     * @param previewText The portion of the text to display in the preview.
     * @param error       The spell-check error containing information about the error.
     */

 // Inside the MainController or another appropriate class
    private void displayErrorPreview(String previewText, SpellChecker.SpellCheckError error) {
        // Create a new stage for error preview
        Stage previewStage = new Stage();
        previewStage.initModality(Modality.APPLICATION_MODAL);
        previewStage.setTitle("Error Preview");

        // Create UI components
        Label errorLabel = new Label("Error Type: " + error.getType());
        Label previewLabel = new Label("Preview:");

        // Highlight the error in the preview text
        TextFlow previewTextFlow = highlightError(previewText, error);

        // Add suggestions to the preview
        List<String> suggestions = getSuggestionsForError(error);
        Text suggestionText = new Text("Suggestions: " + String.join(", ", suggestions));
        suggestionText.setFill(Color.BLUE);  // Customize the color as needed

        // Add the words that were highlighted as errors
        Text errorWordsText = new Text("Error Words: " + previewText.substring(error.getStartIndex(), error.getEndIndex()));
        errorWordsText.setFill(Color.RED);  // Customize the color as needed

        // Create buttons for corrective actions
        Button manualCorrectionButton = new Button("Manual Correction");
        Button suggestionButton = new Button("Select Suggestion");
        Button deleteButton = new Button("Delete Word");
        Button ignoreButton = new Button("Ignore Error");
        Button ignoreForApplicationButton = new Button("Ignore for Rest of Application");
        Button addToDictionaryButton = new Button("Add to User Dictionary");

        // Add actions to buttons
     
        manualCorrectionButton.setOnAction(event -> handleManualCorrection(manualCorrectionButton, error));
        suggestionButton.setOnAction(event -> handleSuggestionSelection(error, previewStage));

        deleteButton.setOnAction(event -> applyDeletion(deleteButton, error, previewStage));


        //ignoreButton.setOnAction(event -> handleIgnoreError(error));
        ignoreButton.setOnAction(event -> handleIgnoreError(ignoreButton, error));
        addToDictionaryButton.setOnAction(event -> handleAddToDictionary(error));

        // Create a layout using VBox
        VBox layout = new VBox();
        layout.getChildren().addAll(errorLabel, previewLabel, previewTextFlow, suggestionText, errorWordsText,
                manualCorrectionButton, suggestionButton, deleteButton, ignoreButton, addToDictionaryButton);

        // Create scene and set it to the stage
        Scene scene = new Scene(layout, 500, 400);
        previewStage.setScene(scene);

        // Show the stage
        previewStage.show();
    }
    
    
    /**
     * Handles the user's selection of a suggestion from the presented options.
     * Applies the selected correction to the text area and updates relevant metrics.
     *
     * @param error         The spell-check error for which a correction is being selected.
     * @param previewStage  The stage containing the error preview.
     */
    
    
    
    
    
    
    private void handleSuggestionSelection(SpellChecker.SpellCheckError error, Stage previewStage) {
        List<String> suggestions = getSuggestionsForError(error);

        if (!suggestions.isEmpty()) {
            // Display suggestions in the ChoiceBox or any other UI element
            ChoiceDialog<String> dialog = new ChoiceDialog<>(suggestions.get(0), suggestions);
            dialog.setTitle("Select a Correction");
            dialog.setHeaderText("Choose a correction for the selected error:");
            dialog.setContentText("Correction:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(selectedSuggestion -> {
                // Apply the correction directly to the text area
                applyCorrection(selectedSuggestion, error);
                numSuggestedCorrections++;

                // Close the previewStage after correction is applied
                previewStage.close();
            });
        } else {
            System.out.println("No suggestions available for the selected error.");
        }
    }

    
    /**
     * Handles the action when the user chooses to ignore a spell-check error.
     * This method implements the logic for ignoring the error, such as marking it as ignored
     * or logging it for future reference. After handling, the preview stage is closed.
     *
     * @param ignoreButton The button representing the action to ignore the error.
     * @param error        The spell-check error being ignored.
     */
  
    private void handleIgnoreError(Button ignoreButton, SpellChecker.SpellCheckError error) {
        // Implement your logic for ignoring the error here
        // For example, you might mark the error as ignored in your application
        // or log it for future reference

        // After handling, close the preview stage
        closePreviewStage(ignoreButton);
    }

    
    /**
     * Closes the preview stage associated with a button.
     *
     * @param button The button whose associated preview stage is to be closed.
     */
    
    // Method to close the preview stage
    private void closePreviewStage(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    
    /**
     * Highlights the detected spell-check errors in the text area.
     * Displays an error preview for each error within the specified context length.
     *
     * @param errors The list of spell-check errors to highlight.
     */
    
    private void highlightErrors(List<SpellChecker.SpellCheckError> errors) {
        for (SpellChecker.SpellCheckError error : errors) {
            int startIndex = Math.max(0, error.getStartIndex() - contextLength);
            int endIndex = Math.min(textArea.getText().length(), error.getEndIndex() + contextLength);

            String previewText = textArea.getText().substring(startIndex, endIndex);
            displayErrorPreview(previewText, error);
        }
    }


   
    
    

    /**
     * Ignores a detected spell-check error.
     * This method implements the logic for ignoring the error, such as marking it as ignored
     * or logging it for future reference. After handling, the error highlights are cleared.
     *
     * @param error The spell-check error being ignored.
     */
    private void applyIgnoreError(SpellChecker.SpellCheckError error) {
        if (error != null) {
            // Implement your logic for ignoring the error here
            // For example, mark the error as ignored or log it for future reference
            markErrorAsIgnored(error);

            // After handling, you might want to clear the error highlights
            clearErrorHighlights();
        }
    }

    // Example method to mark the error as ignored in your application
    private void markErrorAsIgnored(SpellChecker.SpellCheckError error) {
        // Implement the logic to mark the error as ignored in your application
        // This could involve updating a data structure or database to keep track of ignored errors
        // For this example, let's just print a message
        System.out.println("Marking error as ignored: " + error.getType() + " at indices " + error.getStartIndex() + " to " + error.getEndIndex());
    }
    
    
    /**
     * Handles the manual correction of a spell-check error.
     * Opens a TextInputDialog to get manual input from the user and applies the correction.
     * After handling, the preview stage associated with the manual correction button is closed.
     *
     * @param manualCorrectionButton The button triggering the manual correction.
     * @param error                 The spell-check error being manually corrected.
     */
 
    
   
    private void handleManualCorrection(Button manualCorrectionButton, SpellChecker.SpellCheckError error) {
        // Create a TextInputDialog to get manual input from the user
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Manual Correction");
        dialog.setHeaderText("Enter your manual correction:");
        dialog.setContentText("Correction:");

        // Show the dialog and wait for the user's input
        Optional<String> result = dialog.showAndWait();

        // Process the user's input if available
        result.ifPresent(manualCorrection -> {
            // Implement your logic for applying the manual correction
            applyCorrection(manualCorrection, error);
            numManualCorrections++;
            // After handling, close the preview stage
            closePreviewStage(manualCorrectionButton);
        });
    }

   
 
        
    /**
     * Applies the correction to the text area based on the provided correction and error information.
     * If both correction and error are not null, the method determines the corrected content based on the error type,
     * updates the text area with the corrected content on the JavaFX Application Thread, and clears the error highlights.
     *
     * @param correction The correction to be applied.
     * @param error      The spell-check error being corrected.
     */

    private void applyCorrection(String correction, SpellChecker.SpellCheckError error) {
        if (error != null && correction != null) {
            String content = textArea.getText();

            // Determine the corrected content based on the error type
            int startIndex = error.getStartIndex();
            int endIndex = error.getEndIndex();

            String correctedContent = content.substring(0, startIndex) + correction + content.substring(endIndex);

            // Update the text area with the corrected content on the JavaFX Application Thread
            Platform.runLater(() -> {
                textArea.setText(correctedContent);

                // Clear the error highlights
                clearErrorHighlights();
            });
        }
    }

    /**
     * Applies the deletion correction to the text area based on the provided error information.
     * If the error is not null, the method determines the corrected content based on the error type,
     * updates the text area with the corrected content on the JavaFX Application Thread, clears the error highlights,
     * and closes the error preview stage.
     *
     * @param deleteButton   The button triggering the deletion action.
     * @param error          The spell-check error being corrected through deletion.
     * @param previewStage   The stage displaying the error preview.
     */
    private void applyDeletion(Button deleteButton, SpellChecker.SpellCheckError error, Stage previewStage) {
        try {
            if (error != null) {
                String content = textArea.getText();

                // Determine the corrected content based on the error type
                int startIndex = error.getStartIndex();
                int endIndex = error.getEndIndex();

                // Ensure that the indices are in the correct order
                int correctedStartIndex = Math.min(startIndex, endIndex);
                int correctedEndIndex = Math.max(startIndex, endIndex);

                // Check if the indices are within the bounds of the content
                if (correctedStartIndex >= 0 && correctedEndIndex <= content.length()) {
                    System.out.println("Corrected Deletion Indices: begin " + correctedStartIndex + ", end " + correctedEndIndex + ", length " + content.length());

                    String correctedContent = content.substring(0, correctedStartIndex) + content.substring(correctedEndIndex);

                    // Update the text area with the corrected content on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        textArea.setText(correctedContent);

                        // Clear the error highlights
                        clearErrorHighlights();

                        // Close the error preview stage
                        closePreviewStage(previewStage);
                    });
                } else {
                    System.err.println("Invalid indices for deletion: begin " + startIndex + ", end " + endIndex + ", length " + content.length());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception appropriately (log, display an error message, etc.)
        }
    }

  /**
 * Closes the specified preview stage if it is not null.
 * This method is typically used to close a secondary window or a pop-up in the application.
 *
 * @param previewStage The {@link Stage} object representing the preview stage to be closed.
 *                     If the stage is null, the method will do nothing.
 */
private void closePreviewStage(Stage previewStage) {
    if (previewStage != null) {
        previewStage.close();
    }
}

/**
 * Removes error highlights from the text in the text area.
 * This method clears any applied styles that were used to highlight errors in the text,
 * effectively resetting the text area's appearance to its default state.
 */
private void clearErrorHighlights() {
    // Remove the error style class from the entire text
    textArea.getStyleClass().remove("error");
}


    
    /**
     * Retrieves spelling suggestions for the given spell-check error.
     *
     * @param error The spell-check error for which suggestions are needed.
     * @return A list of suggested corrections.
     */

    private List<String> getSuggestionsForError(SpellChecker.SpellCheckError error) {
        String wordInError = textArea.getText().substring(error.getStartIndex(), error.getEndIndex());

        switch (error.getType()) {
            case "Misspelling":
                return spellChecker.suggestMisspellingCorrections(wordInError);
            case "MixedCapitalization":
            case "Miscapitalization":
                return spellChecker.suggestMiscapitalizationCorrections(wordInError);
            // Add cases for other error types if needed
            default:
                return Collections.emptyList();
        }
    }
    /**
     * Highlights the specified spell-check error in the provided text.
     *
     * @param text  The text containing the error.
     * @param error The spell-check error to highlight.
     * @return A TextFlow object with the error highlighted.
     */
    private TextFlow highlightError(String text, SpellChecker.SpellCheckError error) {
        int startIndex = Math.max(0, error.getStartIndex());
        int endIndex = Math.min(text.length(), error.getEndIndex());

        if (startIndex > endIndex || startIndex >= text.length() || endIndex < 0) {
            // Invalid indices, log an error or handle it appropriately
            System.err.println("Invalid indices for error: begin " + startIndex + ", end " + endIndex + ", length " + text.length());
            return new TextFlow(new Text(text));
        }

        String prefix = text.substring(0, startIndex);
        String errorText = text.substring(startIndex, endIndex);
        String postfix = text.substring(endIndex);

        Text prefixText = new Text(prefix);

        // Create a Text node for the errorText
        Text errorTextFlow = new Text(errorText);

        // Apply styling to the errorText
        errorTextFlow.setFont(Font.font("System", FontWeight.BOLD, 12));
        errorTextFlow.setFill(Color.RED); // You can adjust the color as needed

        Text postfixText = new Text(postfix);

        return new TextFlow(prefixText, errorTextFlow, postfixText);
    }


    
    /**
     * Opens the "Contact" page when the corresponding button is clicked.
     *
     * @param event The ActionEvent triggered by clicking the "Contact" button.
     */

    @FXML
    private void openContact(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Contact.fxml"));
            Parent contactParent = loader.load();
            Scene contactScene = new Scene(contactParent);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(contactScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error loading Contact page", "An error occurred while loading the Contact page.");
        }
    }
    
    
    
    
    /**
     * Opens the "Help" page when the corresponding button is clicked.
     *
     * @param event The ActionEvent triggered by clicking the "Help" button.
     */
    
    
    
    
    @FXML
    private void openHelp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Help.fxml"));
            Parent helpParent = loader.load();
            Scene helpScene = new Scene(helpParent);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(helpScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error loading Help page", "An error occurred while loading the Help page.");
        }
    }

    /**
     * Opens the "About" page when the corresponding button is clicked.
     *
     * @param event The ActionEvent triggered by clicking the "About" button.
     */
    @FXML
    private void openAbout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("About.fxml"));
            Parent aboutParent = loader.load();
            Scene aboutScene = new Scene(aboutParent);

            Stage stage = (Stage) aboutButton.getScene().getWindow(); // Use the aboutButton to get the scene

            stage.setScene(aboutScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error loading About page", "An error occurred while loading the About page.");
        }
    }
    /**
     * Opens the "User Dictionary Options" page when the corresponding button is clicked.
     *
     * @param event The ActionEvent triggered by clicking the "User Dictionary Options" button.
     */
    
    @FXML
    private void openUserDictionary(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDictionaryOptions.fxml"));
            Parent userDictionaryParent = loader.load();
            Scene userDictionaryScene = new Scene(userDictionaryParent);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(userDictionaryScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error loading User Dictionary Options", "An error occurred while loading the User Dictionary Options page.");
        }
    }

    /**
     * Displays an error dialog with the specified title and content.
     *
     * @param title   The title of the error dialog.
     * @param content The content of the error dialog.
     */
    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    /**
     * Displays an information dialog with the specified title and content.
     *
     * @param title   The title of the information dialog.
     * @param content The content of the information dialog.
     */
    private void showInfoDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Navigates to the "MainPage" when leaving the "Settings" page.
     *
     * @param event The ActionEvent triggered by leaving the "Settings" page.
     */
    
    @FXML
    private void leaveSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            Parent mainPageParent = loader.load();
            Scene mainPageScene = new Scene(mainPageParent);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(mainPageScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error leaving Settings", "An error occurred while leaving the Settings page.");
        }
    }
   
    
    /**
     * Navigates to the "Settings" page when leaving the "User Dictionary Options" page.
     *
     * @param event The ActionEvent triggered by leaving the "User Dictionary Options" page.
     */
    @FXML
    private void leaveUserDictionaryOptions(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"));
            Parent settingsParent = loader.load();
            Scene settingsScene = new Scene(settingsParent);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(settingsScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error loading Settings Page", "An error occurred while loading the Settings Page.");
        }
    }
    
    

/**
 * Navigates to the "Settings" page when leaving the "About" page.
 *
 * @param event The ActionEvent triggered by leaving the "About" page.
 */
  
    @FXML
    private void leaveAbout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"));
            Parent settingsParent = loader.load();
            Scene settingsScene = new Scene(settingsParent);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(settingsScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error loading Settings Page", "An error occurred while loading the Settings Page.");
        }
    }
    
    /**
     * Navigates to the "Settings" page when leaving the "Contact" page.
     *
     * @param event The ActionEvent triggered by leaving the "Contact" page.
     */
    
    
    @FXML
    private void leaveContact(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"));
            Parent settingsParent = loader.load();
            Scene settingsScene = new Scene(settingsParent);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(settingsScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error loading Settings Page", "An error occurred while loading the Settings Page.");
        }
    }
    
    

/**
 * Navigates to the "Settings" page when leaving the "Help" page.
 *
 * @param event The ActionEvent triggered by leaving the "Help" page.
 */
    
    @FXML
    private void leaveHelp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"));
            Parent settingsParent = loader.load();
            Scene settingsScene = new Scene(settingsParent);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(settingsScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error loading Settings Page", "An error occurred while loading the Settings Page.");
        }
    }
   
    

   
    
    /**
     * Opens a view of the user dictionary content in a popup or the default text editor.
     *
     * @param event The ActionEvent triggered by opening the view of the user dictionary.
     */

    

        @FXML
        private void openViewUserDictionary(ActionEvent event) {
            try (BufferedReader reader = new BufferedReader(new FileReader("userdictionary.txt"))) {
                String line;
                StringBuilder content = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                // Display the content in a popup
                showContentInPopup(content.toString());

                // Alternatively, open the text file with the default text editor
                openTextFileWithDefaultEditor();
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception according to your application's needs.
            }
        }
        
        
        /**
         * Displays the content of the user dictionary in a popup.
         *
         * @param content The content of the user dictionary.
         */

        private void showContentInPopup(String content) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("User Dictionary Content");
            alert.setHeaderText("Content of User Dictionary:");

            TextArea textArea = new TextArea(content);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            // Expand the text area to fit its content
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            // Add the text area to the alert's content
            GridPane contentGrid = new GridPane();
            contentGrid.setMaxWidth(Double.MAX_VALUE);
            contentGrid.add(textArea, 0, 0);

            alert.getDialogPane().setContent(contentGrid);

            // Show the alert
            alert.showAndWait();
        }
        /**
         * Opens the user dictionary text file with the default text editor.
         */

        private void openTextFileWithDefaultEditor() {
            try {
                File file = new File("userdictionary.txt");
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(file);
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception according to your application's needs.
            }
        }
    

    
    
    
    
    
        /**
         * Resets the user dictionary by clearing its contents.
         * This method is triggered by an ActionEvent, typically associated with a user action.
         *
         * @param event The ActionEvent triggered by resetting the user dictionary.
         *              It provides information about the event and the source that triggered it.
         *              For example, it could be a button click or a menu item selection.
         *              The method clears the contents of the user dictionary file, and a success
         *              message is printed to the console. If an IOException occurs during the process,
         *              the exception is printed, and further handling based on the application's needs
         *              can be implemented.
         */
    
       @FXML
    private void resetUserDictionary(ActionEvent event) {
        try {
            // Clear the contents of the user dictionary file
            Files.write(Path.of("userdictionary.txt"), new byte[0], StandardOpenOption.TRUNCATE_EXISTING);

            // You can print a message to the console if needed
            System.out.println("User dictionary reset successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., log it, show an alert) based on your application's needs
        }
    }

/**
 * Opens a dialog to add a word to the user dictionary.
 *
 * @param event The ActionEvent triggered by opening the "Add Word" dialog.
 */
       
       @FXML
       private void openAddWordDialog(ActionEvent event) {
           TextInputDialog dialog = new TextInputDialog();
           dialog.setTitle("Add Word to User Dictionary");
           dialog.setHeaderText("Enter the word you want to add:");
           dialog.setContentText("Word:");

           Optional<String> result = dialog.showAndWait();
           result.ifPresent(this::addWordToUserDictionary);
       }
       /**
        * Adds a word to the user dictionary.
        *
        * @param word The word to be added to the user dictionary.
        */
       private void addWordToUserDictionary(String word) {
           try (BufferedWriter writer = new BufferedWriter(new FileWriter("userdictionary.txt", true))) {
               writer.write(word.toLowerCase() + "\n");
           } catch (IOException e) {
               e.printStackTrace();
               // Handle the exception according to your application's needs
           }
       }
       
       
       
    
       /**
        * Displays spell check metrics in an information alert.
        *
        * @param event The ActionEvent triggered by requesting spell check metrics.
        */
       @FXML
       private void showMetrics(ActionEvent event) {
           Alert alert = new Alert(AlertType.INFORMATION);
           alert.setTitle("Spell Check Metrics");
           alert.setHeaderText(null);

           // Access the metrics using getter methods
          // int numManualCorrections = getNumManualCorrections();
         //  int numSuggestedCorrections = getNumSuggestedCorrections();
        // Access the metrics using getter methods
           int numManualCorrections = getNumManualCorrections();
           int numSuggestedCorrections = getNumSuggestedCorrections();
           //int numMisspellings = getNumMisspellings();
           int numMiscapitalizations = spellChecker.getNumMiscapitalizations();
           int numMisspellings = spellChecker.getNumMisspellings(); // Use getNumMisspellings
           

           // ... other metrics as needed

           // Create the content text for the alert
           String contentText = String.format("Number of Manual Corrections: %d\nNumber of Suggested Corrections: %d\n"
                   + "Number of Misspellings: %d\nNumber of Miscapitalizations: %d",
                   numManualCorrections, numSuggestedCorrections, numMisspellings, numMiscapitalizations);

           // ... add other metrics to contentText as needed

           alert.setContentText(contentText);
           alert.showAndWait();
       }
               
       
       
   }
    
    
    
    
  





