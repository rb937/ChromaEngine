package com.chromaengine.ui;

import com.chromaengine.core.ImageManager;
import com.chromaengine.filters.TimeOfDayFilter;
import com.chromaengine.filters.VignetteFilter;
import com.chromaengine.filters.InvertFilter;
import com.chromaengine.filters.SaturationFilter;
import com.chromaengine.filters.BrightnessFilter;
import com.chromaengine.filters.ConvolutionFilter;
import com.chromaengine.filters.GrayscaleFilter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class PrimaryController {

    @FXML
    private ImageView imageView;

    private ImageManager imageManager;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label slider1Label;
    @FXML
    private Label slider2Label;
    @FXML
    private Slider zoomSlider;

    private double dragStartX = 0;
    private double dragStartY = 0;

    @FXML
    public void initialize() {
        imageManager = new ImageManager();

        filterModeDropdown.getItems().addAll(
                "Time of Day",
                "Grayscale",
                "Invert Colors",
                "Focus",
                "Vignette",
                "Saturation");
        filterModeDropdown.setPromptText("Select a filter");
        filterModeDropdown.getSelectionModel();

        // 1. DYNAMIC LABELS: Listen to the dropdown menu and change the text
        filterModeDropdown.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateDynamicLabels(newVal);
            applyFilter(); // Automatically apply the new filter when selected
        });

        // 2. ZOOM LOGIC: Bind the image size to the zoom slider
        imageView.scaleXProperty().bind(zoomSlider.valueProperty());
        imageView.scaleYProperty().bind(zoomSlider.valueProperty());

        // PANNING LOGIC
        imageView.setOnMousePressed(event -> {
            dragStartX = event.getSceneX() - imageView.getTranslateX();
            dragStartY = event.getSceneY() - imageView.getTranslateY();
            imageView.setCursor(javafx.scene.Cursor.CLOSED_HAND);
        });

        imageView.setOnMouseDragged(event -> {
            imageView.setTranslateX(event.getSceneX() - dragStartX);
            imageView.setTranslateY(event.getSceneY() - dragStartY);
        });

        imageView.setOnMouseReleased(event -> {
            imageView.setCursor(javafx.scene.Cursor.DEFAULT);
        });

        // 3. FILTER LOGIC: Listen to the value sliders
        intensitySlider.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        intensitySlider.setValue(0);
        brightnessSlider.setValue(0);

        slider1Label.setText("");
        slider2Label.setText("");

        intensitySlider.setVisible(false);
        intensitySlider.setManaged(false);

        brightnessSlider.setVisible(false);
        brightnessSlider.setManaged(false);
    }

    /**
     * Changes the UI text depending on what filter is selected in the dropdown.
     */
    private void updateDynamicLabels(String selectedFilter) {
        intensitySlider.setValue(0);
        brightnessSlider.setValue(0);

        slider1Label.setText("");
        slider2Label.setText("");

        intensitySlider.setMin(-100);
        brightnessSlider.setMin(-100);
        intensitySlider.setVisible(false);
        intensitySlider.setManaged(false);
        brightnessSlider.setVisible(false);
        brightnessSlider.setManaged(false);

        if ("Time of Day".equals(selectedFilter)) {
            slider1Label.setText("Time (Night <-> Day)");
            slider2Label.setText("Brightness Offset");
            intensitySlider.setVisible(true);
            intensitySlider.setManaged(true);
            brightnessSlider.setVisible(true);
            brightnessSlider.setManaged(true);
        } else if ("Focus".equals(selectedFilter)) {
            slider1Label.setText("Focus (Sharpen <-> Blur)");
            slider2Label.setText("");
            intensitySlider.setVisible(true);
            intensitySlider.setManaged(true);
        } else if ("Grayscale".equals(selectedFilter)) {
            slider1Label.setText("Color Fade (%)");
            slider2Label.setText("");
            intensitySlider.setMin(0);
            intensitySlider.setVisible(true);
            intensitySlider.setManaged(true);
        } else if ("Invert Colors".equals(selectedFilter)) {
            slider1Label.setText("");
            slider2Label.setText("");
        } else if ("Vignette".equals(selectedFilter)) {
            slider1Label.setText("White <-> Black");
            slider2Label.setText("");
            intensitySlider.setVisible(true);
            intensitySlider.setManaged(true);
        } else if ("Saturation".equals(selectedFilter)) {
            slider1Label.setText("Saturation");
            slider2Label.setText("Vibrance");
            intensitySlider.setMin(-100);
            brightnessSlider.setMin(-100);
            intensitySlider.setVisible(true);
            intensitySlider.setManaged(true);
            brightnessSlider.setVisible(true);
            brightnessSlider.setManaged(true);
        }
    }

    @FXML
    private ComboBox<String> filterModeDropdown;
    @FXML
    private Slider intensitySlider;
    @FXML
    private Slider brightnessSlider;

    @FXML
    private void openImage() {
        // 1. Create a standard File Chooser window
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");

        // 2. Add filters so the user only selects valid image types (PNG, JPG, etc.)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));

        // 3. Open the window and wait for the user to select a file
        // We get the current window (Stage) from our imageView to anchor the dialog box
        Stage currentStage = (Stage) imageView.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(currentStage);

        // 4. If the user picked a file (and didn't hit 'Cancel')
        if (selectedFile != null) {
            // Pass the absolute path of whatever file they picked to our engine
            boolean success = imageManager.loadImage(selectedFile.getAbsolutePath());

            if (success) {
                // Update the canvas
                Image displayImage = SwingFXUtils.toFXImage(imageManager.getCurrentImage(), null);
                imageView.setImage(displayImage);
                // Force the image to fit the screen nicely, but let the user zoom in later
                imageView.setFitWidth(800);
                imageView.setFitHeight(600);
                zoomSlider.setValue(1.0); // Reset zoom on new image
            }
        }
    }

    /**
     * The physical physical reset button. Wipes all math and resets sliders.
     */
    @FXML
    private void resetImage() {
        if (imageManager == null || imageManager.getCurrentImage() == null)
            return;

        // 1. Wipe memory back to the absolute original file
        imageManager.hardReset();

        // 2. Reset the UI
        intensitySlider.setValue(0);
        brightnessSlider.setValue(0);
        zoomSlider.setValue(1.0);
        filterModeDropdown.getSelectionModel().clearSelection();

        // 3. Push the clean pixels to the screen
        Image displayImage = SwingFXUtils.toFXImage(imageManager.getCurrentImage(), null);
        imageView.setImage(displayImage);
    }

    @FXML
    private void applyFilter() {
        // Safety check
        if (imageManager == null || imageManager.getCurrentImage() == null) {
            return;
        }

        // 1. NON-DESTRUCTIVE EDITING: Instantly revert the memory back to the original,
        // pristine image.
        imageManager.resetToOriginal();

        // 2. Get the current UI values
        String selectedFilter = filterModeDropdown.getValue();
        double intensity = intensitySlider.getValue(); // Goes from 0 to 100

        // 3. Route to the correct mathematical algorithm
        if ("Time of Day".equals(selectedFilter)) {
            // Slider at 0 = Midnight, 50 = Original, 100 = High Noon
            TimeOfDayFilter timeFilter = new TimeOfDayFilter(intensity);
            timeFilter.apply(
                    imageManager.getDisplayPixelData(), // Note: We pass the Display array now, not the original!
                    imageManager.getWidth(),
                    imageManager.getHeight());
            new BrightnessFilter(brightnessSlider.getValue()).apply(
                    imageManager.getDisplayPixelData(),
                    imageManager.getWidth(),
                    imageManager.getHeight());
        } else if ("Invert Colors".equals(selectedFilter)) {
            new InvertFilter().apply(imageManager.getDisplayPixelData(), imageManager.getWidth(),
                    imageManager.getHeight());
        } else if ("Grayscale".equals(selectedFilter)) {
            new GrayscaleFilter(intensity).apply(imageManager.getDisplayPixelData(), imageManager.getWidth(),
                    imageManager.getHeight());
        } else if ("Focus".equals(selectedFilter)) {
            new ConvolutionFilter(intensity).apply(imageManager.getDisplayPixelData(), imageManager.getWidth(),
                    imageManager.getHeight());
        } else if ("Vignette".equals(selectedFilter)) {
            new VignetteFilter(intensity).apply(
                    imageManager.getDisplayPixelData(),
                    imageManager.getWidth(),
                    imageManager.getHeight());
        } else if ("Saturation".equals(selectedFilter)) {
            new SaturationFilter(intensity, brightnessSlider.getValue()).apply(
                    imageManager.getDisplayPixelData(),
                    imageManager.getWidth(),
                    imageManager.getHeight());
        }

        // 4. Push the modified pixels to the screen
        Image displayImage = SwingFXUtils.toFXImage(imageManager.getCurrentImage(), null);
        imageView.setImage(displayImage);
    }

    @FXML
    private void commitLayer() {
        if (imageManager == null || imageManager.getCurrentImage() == null)
            return;

        // "Bake" the current display pixels into the original backup array
        System.arraycopy(
                imageManager.getDisplayPixelData(), 0,
                imageManager.getOriginalPixelData(), 0,
                imageManager.getDisplayPixelData().length);

        // Reset the UI sliders back to 0 so the user can stack the next effect
        intensitySlider.setValue(0);
        brightnessSlider.setValue(0);
        System.out.println("Layer committed! You can now stack another filter.");
    }

    @FXML
    private void saveImage() {
        if (imageManager == null || imageManager.getCurrentImage() == null) {
            System.err.println("Nothing to save!");
            return;
        }

        // 1. Create the Save Dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Processed Image");

        // 2. Set the default file name
        fileChooser.setInitialFileName("processed_image.png");

        // 3. Force the user to save it as a PNG or JPG
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Image", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Image", "*.jpg"));

        // 4. Open the window and wait for the user to pick a location
        Stage currentStage = (Stage) imageView.getScene().getWindow();
        File fileToSave = fileChooser.showSaveDialog(currentStage);

        if (fileToSave != null) {
            // Figure out if they chose PNG or JPG based on the file extension they typed
            String format = "png"; // Default to PNG for pixel art (preserves transparency)
            if (fileToSave.getName().toLowerCase().endsWith(".jpg")) {
                format = "jpg";
            }

            // Send it to the engine to be written to the disk
            imageManager.saveImage(fileToSave.getAbsolutePath(), format);
        }
    }
}