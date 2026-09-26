# Marker Picker

MarkerPicker is a calibration tool in the Vocabulary Builder Kit. It configures the Vocabulary Builder application (VB app)
to work properly under specific lightning. 

## Technical notes

MarkerPicker saves colors of markers under the current lighting conditions, into the Markers file (markers.txt).
VB app reads the Markers file and use the colors to find markers in a scene.

**Lab** colorspace was selected to calculate visual (perceived) closeness of colors. The Markers file
keeps each individual color in a separate line and presents it in both **Lab** and **RGB** colorspaces.
While VB app uses Lab colorspace, RGB is kept as more human friendly, to track relevance of the
selected color and for debugging purposes.

## UX

User's perspective scenario is the following. In MarkerPicker, user takes a picture of the scene,
selects markers in the scene image and then saves them into the Markers file.

While the GUI supports main commands, the selection of a specific color is performed via
the terminal.

## Building notes

To build the application you will need:
- `openCV` library built with `opencv_contrib` extra modules, for Java
- `javaFX SDK`

## Future Developments

- Clean up unused code
- Implement the color selection via GUI,
- Adapt the application to deal with various image sizes coming from the camera

