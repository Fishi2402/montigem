# Video

----------------------------------------------------------------------
## Overview

- [Goal](#goal)
- [GUI Model Component](#gui-model-component)
- [GUI Model](#gui-model)
- [Implementation](#implementation)
- [How to use GemVideo](#how-to-use-gemVideo)

----------------------------------------------------------------------

## Goal

The goal of this project was to implement and test a GUI model of the _video_ atomic component, the atomic component contains source as attribute and customized with height and width, and is tested using unit-tests.


----------------------------------------------------------------------

## GUI Model Component

The _<video>_ element is used to show a video on a web page. The _source_ attribute specifies the location (URL) of the video file, which allows you to specify video files which the browser may choose from. 

<img src = "./images/video.png" alt="Video" width=48.87%>

_this is the video from inout app._

----------------------------------------------------------------------

## GUI Model

In this section, we explain how to create models, generate components and adjust them with hand-written code. We will do it by creating a simple component for showing plain text.

Creat a new folder and name it as _gemvideo_, then add _gemvideo.gui_ inside the folder, the content of _gemvideo.gui_ should be as followings:

```
package basic.src.lib.gemvideo;

component GemVideo(
  String source
)
```
This is the component declaration, first we state a Java-like package name and declare a component named GemVideo with a value as its input parameter. More information about GUI models can be found in the GUI-DSL documentation. 

Since the GUI model only specifies the interface of the component. you need add _gem-video.component.html_ in the same folder to implement the video component. The content of html file should be as following:

```html
<video controls height="100%" width="100%">
    <source src={{source}}>
  </video>
```

The interpolation uses the double curly braces {{}} as delimiters to embed expressions into marked up text. _source_ refers the value of source variable. The _height_ and _width_ are set to 100%, otherwise the page might flicker while the video loads.

## Implementation

In this section, the detailed implementation process will be explained.

```html
<video controls height="100%" width="100%">
    <source src={{source}}>
  </video>
```

_GemVideo component implementation._

### Exporting GemVideo

----------------------------------------------------------------------

In order to use model component, add _index.ts_ with the following content:

```ts
export * from './gemvideo.module-gen';
export * from './gem-video/gem-video.component-gen';
```

The _export_ property of the _@NgModule_ decorator enable an Angular module to expose some of its components to the other modules in the applications, otherwise the components defined in a module could only be used in that module.

Moreover, adjust the _index.ts_ file from parent folder with following content:

```ts
// some exports ...
export * from "./lib/gemvideo";
```

----------------------------------------------------------------------

## How to use GemVideo

----------------------------------------------------------------------

We will demostrate the possible inputs with the examples.

### Input parameters

Add component to _app.component.html_ in _inout-tester_ application as following:

```html
<!-- some code -->
<div style="width: 420px; height: auto;">
  <gem-video
  source="https://archive.org/download/BigBuckBunny_124/Content/big_buck_bunny_720p_surround.mp4"
  ></gem-video>
</div>
```

_<div>_ tag is used to group content so it can be easily styled by width and height attributes. _<gem-video>_ is defined inside the <div> tag, the input parameter _source_ can be url of the video or directory of the video.

