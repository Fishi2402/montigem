Button enables user interaction through simple clicks or taps, triggering
actions or tasks.

## Usage Example

```ts
package example_basic;

import mc.fenix.basic.GemButton;

page GemButtonExample() {
  @GemButton(label = "windows alert", leftClick = alert());
  @GemButton(label = "Cannot click me", disabled = true);
}
```
<img src="../images/gembutton.png" alt="gembutton" width=40%>

The Java implementation of `alert` method could look like this:
```java
package example_basic;
import umlp.guiutil.HTMLUtils;
import umlp.jsweet.extension.annotation.Component;
import java.util.function.Consumer;

@Component
public class GemButtonExample extends GemButtonExampleTOP {
  @Override
  public Consumer<Void> alert() {
    return (Void) -> HTMLUtils.alert("Hello");
  }
}
```

## GUI Model

`GemButton.gui` ([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/client/models/mc/fenix/basic/GemButton.gui))

```ts
package mc.fenix.basic;

component GemButton(
  String height = "auto",
  String width = "auto",
  String label = "",
  List<GUIViewElement> content,
  boolean disabled = false,
  Event<Void> leftClick,
  String routerLink = null,
  String routerLinkActive = null
)
```

|  Name               | Type                   | Description                                                                                 | Default value |
|---------------------|------------------------|---------------------------------------------------------------------------------------------|----------------|
| `height`            | `String`               | Height of the component in `px`, `%`, `vh`, etc.                                            | `"auto"` (collapses to a minimal possible height or takes up all available space depending on context) |
| `width`             | `String`               | Width of the component in `px`, `%`, `vh`, etc.                                             | `"auto"` (collapses to a minimal possible height or takes up all available space depending on context) |
| `label`             | `String`               | Button text.                                                                                | `""` |
| `content`           | `List<GUIViewElement>` | Button content if a more complex element than text is required. If label and content are both set, only content is shown. | `null` (no content) |
| `disabled`          | `boolean`              | If true, the button cannot be clicked.                                                      | `false` |
| `leftClick`         | `Event<Void>`          | Callback that is executed when a button is clicked.                                         | `null` (no action) |
| `routerLink`        | `String`               | Specifies a router path to navigate to when the button is clicked.                         | `null` (no navigation) |
| `routerLinkActive`  | `String`               | Specifies a CSS class to apply when the link's route is active.                            | `null` (no active class applied) |
