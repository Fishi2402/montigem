The component shows a video on a web page. 

## Usage example

```gui
package example_basic;

import mc.fenix.basic.GemVideo;

page GemVideoExample() {
  @GemVideo(source = "https://archive.org/download/BigBuckBunny_124/Content/big_buck_bunny_720p_surround.mp4");
}
```

<img src = "../images/gemvideo.png" alt="Example Video" width=50%>

## GUI Model

`GemVideo.gui`([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/client/models/mc/fenix/basic/GemVideo.gui))

```gui
package mc.fenix.basic;

component GemVideo(
  String source
)
```

| Name | Type | Description | Default value |
|---|---|---|---|
| `source` | `String` | The path to the video file. It can be a URL or a relative path. The relative path starts from the root folder (typically `src/`). | `null` (no source) |
