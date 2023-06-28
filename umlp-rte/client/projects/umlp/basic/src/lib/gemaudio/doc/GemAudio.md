# Audio

----------------------------------------------------------------------

## Goal

The goal of this project is to implement and test a new Angular library which contains a chart, in this case is audio component. This component should be customizable with source, and is tested using unit-tests.

----------------------------------------------------------------------

## Overview
- [Goal](#goal)
- [Overview](#overview)
- [Audio](#Audio)
- [GUI Model](#gui-model)
- [Implementation](#implementation)
- [How to use the Audio Component and Inputs](#how-to-use-the-audio-component-and-inputs)
- [References](#references) 

----------------------------------------------------------------------

## Audio

The audio component is used to embed sound content in the documents. It looks like a music player with a play button and a track bar. It may contain one or more audio sources. More information could be found in [1].

<img src="https://raw.githubusercontent.com/xiaochunjimmy/AudioPlayer/master/img/cover.png" alt="AudioComponent" width=80%>

_An example of audio component_

----------------------------------------------------------------------

## GUI Model

In this section discuss about the GUI model, which is the typical component declaration. 


1. First create a new folder in inout library with name _gemaudio_. 
2. Then create the .gui file with name _GemAudio.gui_. And add the following code: 

```
package basic.src.lib.gemaudio;

component GemAudio(
  String source
)
```
In this file, we state the Java-like package name _gemaudio_, declare the component named _GemAudio_, and define input parameter _source_.

----------------------------------------------------------------------

## Implementation

In this section the implementation details will be discussed.

1. Create a .html file named _gem-audio.component.html_, with the following code:
```
<figure>
    <audio
        controls
        src={{source}}>
    </audio>
</figure>
```
By using this hand-written HTML file, the generated one will automally point to this one as its template. 

----------------------------------------------------------------------

## How to use the Audio Component and Inputs

In this section will show how to use the Audio Component and explain the input.

**Note that it is highly recommended to put the Audio Component into a div-element.**

```ts
<div fxLayout fxLayoutGap="20px">
  <gem-audio source=".\assets\audio\smb_gameover.wav"></gem-audio>
</div>
```

_Listing 6.1 the Example of usage of audio component and input_

### source
Specify the source of the audio file.

----------------------------------------------------------------------

## References
[1] _mdn web docs- The Embed Audio element. _ https://developer.mozilla.org/en-US/docs/Web/HTML/Element/audio#specifications Accessed: 2022-08-12.
