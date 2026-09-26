# Marker Picker

MarkerPicker is a calibration tool in the Vocabulary Builder Kit. It configures the Vocabulary Builder application (VB app)
so it works properly under specific lightning. 

## UX

For calibration, we use the lightning conditions the application is intended to work under.
User places markers in front of the camera and, in the MarkerPicker application,
takes a picture of the scene. Then user selects and saves markers colors into a Markers file.

While the GUI supports main commands, the selection of a specific color is performed via
the terminal.

## Technical notes

MarkerPicker saves colors of markers under the current lighting conditions (reference colors),
into the Markers file (markers.txt). VB app uses the reference colors to find markers in a scene image.

The **L\*a\*b\*** colorspace was selected to calculate visual (perceived) closeness of colors. The Markers file
keeps each individual color in a separate line and presents it in both **L\*a\*b\*** and **RGB** colorspaces.
While VB app uses the L\*a\*b\* colorspace, RGB is retained as more human-friendly, to track relevance of the
selected color and for debugging purposes.

## Building notes

To build the application you will need:
- `openCV` library built with `opencv_contrib` extra modules, for Java
- `javaFX SDK`

## Future Developments

- Clean up unused code
- Implement the color selection via GUI
- Adapt the application to deal with various image sizes coming from the camera

