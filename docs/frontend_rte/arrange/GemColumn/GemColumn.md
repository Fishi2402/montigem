GemColumn is a container which arranges input elements vertically.

## Usage example

```ts
package example_arrange;

import mc.fenix.arrange.GemColumn;
import mc.fenix.basic.GemText;

page GemColumnExample() {
  @GemColumn(
    rowGap = "15px",
    components = [
      @GemText(value = "First row"),
      @GemText(value = "Second row"),
      @GemText(value = "Third row")
    ]
  );
}
```

<img src="../images/GemColumnExample.PNG" alt="Columns Layout" width=50%>

---

Following example shows a `300 x 200` column. The horizontal alignment of
elements is set to evenly distribute extra space to the left of the inner
column, between the inner column and the "Third row", and to the right of the
"Third row". The outer column is set to wrap elements that cannot fit into the
container. Since the inner column has height of `100%`, the "Third row" is
forced to wrap. The inner column sets vertical align to `"bottom"` which forces
"First row" and "Second row" to the bottom of the container. The `rowGap` is set
to `"5px"` which creates a gap between the "First row" and "Second row".
```ts
package example_arrange;

import mc.fenix.arrange.GemColumn;
import mc.fenix.basic.GemText;

page GemColumnExample2() {
  outer@GemColumn(
    width = "300px",
    height = "200px",
    hAlign = "space-evenly",
    wrap = "wrap",
    components = [
      inner@GemColumn(
        width = "30%",
        height = "100%",
        vAlign = "bottom",
        rowGap = "5px",
        components = [
          @GemText(value = "First row"),
          @GemText(value = "Second row")
        ]
      ),
      @GemText(value = "Third row")
    ]
  );
}
```
Additional SCSS styles:
```
.outer { border: 1px solid red; }
.inner { border: 1px solid black; }
```

<img src="../images/GemColumnExample2.PNG" alt="Columns Layout" width=50%>

## GUI Model

`GemColumn.gui`
([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/client/models/mc/fenix/arrange/GemColumn.gui))

```gui
package mc.fenix.arrange;

component GemColumn(
  String height = "100%",
  String width = "100%",
  String hAlign = "stretch",
  String vAlign = "stretch",
  String rowGap = "0",
  String colGap = "0",
  String wrap = "nowrap",
  List<GUIViewElement> components,
  GUIViewElement component
)
```

| Name | Type  |  Description |  Default value |
| ---- | ---- | ----- | ----- | 
| `height` | `String` | Height of the component in `px`, `%`, `vh`, etc. | `"100%"` (takes 100% of its parent height) |
| `width`  | `String` | Width of the component in `px`, `%`, `vh`, etc. | `"100%"` (takes 100% of its parent width) |
| `hAlign`  | `String` (restricted set) | Horizontal alignment of the embedded components. Invalid values are ignored. | `"left"` |
||`"left"`, `"center"`, `"right"` | Set position of the elements inside the container. ||
||`"stretch"`| Stretch elements proportionally to fill space if the elements' size is not specified. ||
||`"space-between"`| Distribute empty space equally between the elements. ||
||`"space-around"`| Distribute empty space around each element equally. ||
||`"space-evenly"`| Distribute empty space around the elements and make those gaps equal size. ||
| `vAlign` | `String` (restricted set) | Vertical alignment of the embedded components. Invalid values are ignored. | `"top"` |
||`"top"`, `"center"`, `"bottom"`| Set position of the elements inside the container. ||
||`"stretch"`| Stretch elements proportionally to fill space if the elements' size is not specified. ||
||`"space-between"`| Distribute empty space equally between the elements. ||
||`"space-around"`| Distribute empty space around the elements equally. ||
||`"space-evenly"`| Distribute empty space around the elements and make those gaps equal size. ||
| `rowGap` | `String` | Gap between rows of elements in the container in `px`, `%`, `vh`, etc. | `"0"` |
| `colGap` | `String` | Gap between columns of elements in the container in `px`, `%`, `vh`, etc. Relevant when `wrap` is set to `"wrap"` or `"wrap-reverse"`. | `"0"` |
| `wrap` | `String` (restricted set) | Set behavior for element wrapping. Invalid values are ignored. | `"nowrap"` |
||`"nowrap"`| Elements are never wrapped. If elements do not into the container, a scrollbar appears. ||
||`"wrap"`| Elements that do not fit into the container are wrapped, i.e. a new column is created to the right. ||
||`"wrap-reverse"`| Similar to `"wrap"`, but a new column is created to the left. ||
| `components` | `List<GUIViewElement>` | Components to display inside a column. | `null` (no elements) |
| `component` | `GUIViewElement` | **Deprecated.** Shortcut for `components` property for the case when only one component has to be displayed. If both `component` and `components` are defined, `component` is shown first. | `null` (no element) |
