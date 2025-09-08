---
hide:
  - navigation
---

The audio component embeds sound content on a page.

## Usage example

```ts
package example_basic;

import mc.fenix.basic.GemAudio;

page GemAudioExample() {
  @GemAudio(
    width = "300px",
    source = "assets/audio/audio.mp3"
  );
}
```

<img src="./images/gemaudio.png" alt="AudioComponent" width=50%>

## GUI Model

`GemAudio.gui` ([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/frontend/models/mc/fenix/basic/GemAudio.gui))  

```java
package mc.fenix.basic;

component GemAudio(
  String width = "auto",
  String source
)
```

| Name | Type | Description | Default value |
|---|---|---|---|
| `width`  | `String` | Width of the component in `px`, `%`, `vh`, etc. | `"auto"` (takes up all available space) |
| `source` | `String` | The path to the audio file. It can be a URL or a relative path. The relative path starts from the root folder (typically `src/`). | `null` (no source) |
