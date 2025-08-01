The link element is used to create a hyperlink to another web page or to a specific section within a
page. 

## Usage example:

```gui
package example_basic;

import mc.fenix.basic.GemLink;

page GemLinkExample() {
  @GemLink(
    url = "https://www.google.com/",
    text = "click here for Google"
  );
}
```

<img src="./images/gemlink.png" alt="Example Link" width=40%/>

## GUI Model

`GemLink.gui` ([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/client/models/mc/fenix/basic/GemLink.gui))

```gui
package mc.fenix.basic;

component GemLink(
  String url,
  String text
)
```

| Name | Type | Description | Default value |
|---|---|---|---|
| `url` | `String` | URL the hyperlink redirects to. | `""` (doesn't link to anything) |
| `text` | `String` | Text of the link. | `""` (shows no text) |
