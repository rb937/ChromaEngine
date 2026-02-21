#  ChromaEngine

A high-performance, non-destructive image manipulation and color-grading engine built from scratch in Java.

ChromaEngine bypasses standard high-level image processing libraries (like OpenCV or AWT's built-in filters) to perform **direct-memory bitwise processing** on ARGB channels. By manipulating raw 1D integer arrays in memory, the engine achieves instantaneous, real-time pixel processing suitable for both professional photo editing and 2D pixel-art asset preparation.

## Why I Built This

I originally developed ChromaEngine to solve a workflow bottleneck in my own 2D pixel art game development. Heavy, general-purpose software like Photoshop or GIMP felt too slow and bloated for repetitive tasks like recoloring character variants, stripping backgrounds, and adjusting the cinematic lighting of sprite sheets. 

I wanted a lightweight, custom-built tool that could mathematically process my game assets exactly how my game engine needed them, while simultaneously serving as a deep dive into Java memory management, bitwise operations, and linear algebra.

## Core Architecture & Features

* **Direct Memory Access:** Extracts and mutates raw `int[]` pixel arrays, reducing memory overhead and allowing O(n) processing speeds across millions of pixels.
* **Non-Destructive Pipeline:** Utilizes a multi-array memory cache to preserve original image data, allowing for real-time slider previews without destructive compounding or bitwise overflow artifacts.
* **Convolution Matrices:** Implements custom 3x3 kernels to perform spatial filtering for multi-pass Gaussian Blurs and Edge Sharpening.
* **Spatial Algorithms:** Utilizes Pythagorean distance-from-center calculations to dynamically generate cinematic Vignettes (both shadow and highlight borders).
* **Perceptual Luminance:** Implements industry-standard human-eye perception weights (`0.299 R + 0.587 G + 0.114 B`) for true Grayscale conversion and smart Vibrance/Saturation multipliers.

## Included Tools

* **Time of Day:** Dynamic linear interpolation (Lerp) to simulate ambient midnight shadows or harsh midday sunlight, along with a brightness slider for extra control.
* **Focus:** 3x3 Convolution matrix routing for sharpening edges or blurring details.
* **Vignette:** Distance-based shadowing and highlight borders.
* **Luminance:** True Grayscale and rapid Bitwise Color Inversion.
* **Color Space:** Smart Vibrance (targeting muted colors) and flat Saturation multipliers.

## Interface
![Interface Demo](https://github.com/rb937/ChromaEngine/raw/main/media/Demo.gif)

*(Note: If the video does not play you can watch the demo at \media\demo.gif)

## Project Structure

```text
ChromaEngine/
├── src/main/
│   ├── java/com/chromaengine/
│   │   ├── App.java                 # Main application launcher
│   │   ├── PrimaryController.java   # JavaFX UI event router
│   │   ├── ImageManager.java        # Core memory manager & true-original backups
│   │   └── filters/                 # Custom bitwise mathematical algorithms
│   │       ├── PixelFilter.java     # Base interface for the rendering pipeline
│   │       ├── BrightnessFilter.java
│   │       ├── ConvolutionFilter.java # 3x3 kernel math for blur/sharpen
│   │       ├── GrayscaleFilter.java
│   │       ├── SaturationFilter.java  # Smart vibrance & saturation
│   │       ├── TimeOfDayFilter.java
│   │       └── VignetteFilter.java    # Distance-from-center spatial math
│   └── resources/com/chromaengine/
│       └── primary.fxml             # XML layout for the dynamic UI
├── .gitignore                       # Target exclusions and IDE settings
├── pom.xml                          # Maven build configuration and dependencies
└── README.md                        # Project documentation
```
## Installation & Setup
### Prerequisites
* **Java Development Kit (JDK):** Version 11 or higher (recommended for JavaFX)

* **Build Tool:** Apache Maven

#### Step 1: Clone the Repository
Open your terminal and pull the code to your local machine:

```Bash
git clone [https://github.com/rb937/ChromaEngine](https://github.com/rb937/ChromaEngine)
cd ChromaEngine
```
#### Step 2: Build the Engine
Use Maven to clean the directory, resolve any dependencies, and compile the custom filter classes

```Bash
mvn clean compile
```
#### Step 3: Launch the Application
Once the Java classes are compiled, launch the JavaFX rendering UI
```Bash
mvn javafx:run
```
