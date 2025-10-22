# Requirements
## Example Visualizations
In order to insert visualizations for your GUI components you can add an optional `images` directory to the task in the `build.gradle` file.

**Example:**
```gradle
tasks.register('latex', JavaExec) {
  mainClass.set('de.monticore.doc.MontigemDocTool')
  classpath = sourceSets.main.runtimeClasspath
  def argsList = [
          "-i", "./src/test/resources/lib",
          "--latex", "$buildDir/latex",
          "--examples", "./src/test/resources/examples",
          // images directory
          "--images", "./src/test/resources/images",
          "--hwc", "./src/test/resources/hwc",
          "--cd", "./src/test/resources/lib"
  ]

  ...
}
```
### Format
Images can be provided in two different formats: Either **.png** or **.jpg**.

To fit your image into the resulting PDF there are generally two options. The default handling scales the given image to 80% of the page width, to ensure good readability of larger visualizations. If you have images of smaller components, such as buttons or checkboxes, a second option is to append *'Small'* to the file name. Then the image will only take on 40% of the page width.

### Naming
For the naming of the files choose the same name as the original component and either append *'Example'* to the name or put *'My'* in front of it. You can also append *'Small'* to handle a small image. Take note that everything is handled case-sensitive. So look out for proper capitalization of your classes when you name the image files.

**Example** - valid image file names for component `GemCard` would be:
```
--- large images ---

GemCardExample.png
MyGemCard.jpg

--- small images ---

GemCardExampleSmall.png
MyGemCardSmall.jpg
```
