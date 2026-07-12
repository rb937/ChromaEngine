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
import javafx.scene.control.TextArea;
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

    @FXML
    private TextArea logTextArea;

    private double dragStartX = 0;
    private double dragStartY = 0;

    @FXML
    public void initialize() {
        imageManager = new ImageManager();
        imageManager.setLogger(this::log);

        log("Application started.");

        filterModeDropdown.getItems().addAll(
                "Time of Day",
                "Grayscale",
                "Invert Colors",
                "Focus",
                "Vignette",
                "Saturation");
        filterModeDropdown.setPromptText("Select a filter");
        filterModeDropdown.getSelectionModel();

        filterModeDropdown.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateDynamicLabels(newVal);
            applyFilter();
        });

        imageView.scaleXProperty().bind(zoomSlider.valueProperty());
        imageView.scaleYProperty().bind(zoomSlider.valueProperty());

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

        log("Selected filter option: " + selectedFilter);

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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));

        Stage currentStage = (Stage) imageView.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(currentStage);

        if (selectedFile != null) {
            boolean success = imageManager.loadImage(selectedFile.getAbsolutePath());

            if (success) {
                Image displayImage = SwingFXUtils.toFXImage(imageManager.getCurrentImage(), null);
                log("Opened image: " + selectedFile.getAbsolutePath());
                imageView.setImage(displayImage);
                imageView.setFitWidth(800);
                imageView.setFitHeight(600);
                zoomSlider.setValue(1.0);
            }
        }
    }

    @FXML
    private void resetImage() {
        if (imageManager == null || imageManager.getCurrentImage() == null) {
            log("Nothing to reset!");
            return;
        }

        imageManager.hardReset();
        intensitySlider.setValue(0);
        brightnessSlider.setValue(0);
        zoomSlider.setValue(1.0);

        filterModeDropdown.getSelectionModel().clearSelection();
        Image displayImage = SwingFXUtils.toFXImage(imageManager.getCurrentImage(), null);
        imageView.setImage(displayImage);
        log("Resetted image.");
    }

    @FXML
    private void applyFilter() {
        if (imageManager == null || imageManager.getCurrentImage() == null) {
            return;
        }
        imageManager.resetToOriginal();

        String selectedFilter = filterModeDropdown.getValue();
        double intensity = intensitySlider.getValue();

        if ("Time of Day".equals(selectedFilter)) {
            TimeOfDayFilter timeFilter = new TimeOfDayFilter(intensity);
            timeFilter.apply(
                    imageManager.getDisplayPixelData(),
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

        Image displayImage = SwingFXUtils.toFXImage(imageManager.getCurrentImage(), null);
        imageView.setImage(displayImage);
    }

    @FXML
    private void closeImage() {
        if (imageManager == null || imageManager.getCurrentImage() == null) {
            log("No Image is loaded");
            return;
        }

        imageManager.unloadImage();
        imageView.setImage(null);

        intensitySlider.setValue(0);
        brightnessSlider.setValue(0);
        zoomSlider.setValue(1.0);
        filterModeDropdown.getSelectionModel().clearSelection();
    }

    @FXML
    private void commitLayer() {
        if (imageManager == null || imageManager.getCurrentImage() == null) {
            log("No Image is loaded");
            return;
        }
        System.arraycopy(
                imageManager.getDisplayPixelData(), 0,
                imageManager.getOriginalPixelData(), 0,
                imageManager.getDisplayPixelData().length);

        intensitySlider.setValue(0);
        brightnessSlider.setValue(0);
        System.out.println("Layer committed! You can now stack another filter.");
        log("Applied filter.");
        log("Layer committed! You can now stack another filter.");
    }

    @FXML
    private void saveImage() {
        if (imageManager == null || imageManager.getCurrentImage() == null) {
            System.err.println("Nothing to save!");
            log("Nothing to save!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Processed Image");

        fileChooser.setInitialFileName("processed_image.png");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Image", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Image", "*.jpg"));

        Stage currentStage = (Stage) imageView.getScene().getWindow();
        File fileToSave = fileChooser.showSaveDialog(currentStage);

        if (fileToSave != null) {
            String format = "png";
            if (fileToSave.getName().toLowerCase().endsWith(".jpg")) {
                format = "jpg";
            }
            imageManager.saveImage(fileToSave.getAbsolutePath(), format);
            log("Saved image to: " + fileToSave.getAbsolutePath());
        }
    }

    public void log(String message) {
        logTextArea.appendText(message + "\n");
        logTextArea.setScrollTop(Double.MAX_VALUE);
    }
}