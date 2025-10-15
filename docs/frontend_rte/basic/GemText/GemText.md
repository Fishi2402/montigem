---
hide:
  - navigation
---

Displays text on a webpage.

## Usage example

```ts
package example_basic;

import mc.fenix.basic.GemText;

page GemTextExample() {
  @GemText(
    value = "My text",
    color = "blue",
    size = "1rem",
    fontFamily = "'Lucida Console', Courier, monospace",
    fontStyle = "oblique 12deg",
    weight = "200",
    width = "200px",
    height = "3rem"
  );
}
``` 

<img src="./images/gemtext.png" alt="Text" width=40%/>

## GUI Model

`GemText.gui` ([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/frontend/models/mc/fenix/basic/GemText.gui))

```java
package mc.fenix.basic;

component GemText(
  String value,
  String color,
  String size,
  String fontStyle = "unset",
  String weight = "unset",
  String fontFamily = "unset",
  String width = "100%",
  String height = "100%"
)
```

| Name           | Type     | Description                                                                                                   | Default value                |
|----------------|----------|---------------------------------------------------------------------------------------------------------------|------------------------------|
| `value`        | `String` | Text content.                                                                                                 | `""` (shows no text)         |
| `color`        | `String` | Text color (e.g., `"black"`, `"#FF0000"`, `"rgba(0,0,0,0.5)"`, etc.).                                          | `null` (uses default color)  |
| `size`         | `String` | Font size (e.g., `"16px"`, `"1em"`, `"large"`).                                                               | `null` (uses default size)   |
| `fontStyle`    | `String` | Font style. Can be `"normal"`, `"italic"`, `"oblique <deg>"`, or a universal value.                          | `"unset"`                    |
| `weight`       | `String` | Font weight. Can be `"normal"`, `"bold"`, `"lighter"`, `"<number>"`, or a universal value.                   | `"unset"`                    |
| `fontFamily`   | `String` | Font family. Example: `"Gill Sans", sans-serif`.                                                              | `"unset"`                    |
| `width`        | `String` | Width of the input field.                                                                                     | `"100%"`                     |
| `height`       | `String` | Height of the input field.                                                                                    | `"100%"`                     |
