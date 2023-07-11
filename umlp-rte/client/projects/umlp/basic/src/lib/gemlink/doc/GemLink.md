# Link Embed Element

----------------------------------------------------------------------

## Overview
- [Goal](#goal)
- [Link Component](#Link-component)
- [GUI Model](#gui-model)
- [Implementation](#implementation)
- [How to use GemLink](#how-to-use-gemLink)
- [References](#references)

----------------------------------------------------------------------

## Goal

The goal of this project was to implement and test the GUI model of the link atomic component which is using the `<a>` HTML tag.
The hyperlink should take a URL and an optional text.

----------------------------------------------------------------------

## Link Component

The link component creates a text hyperlink which links to the URL given as the required ***url*** attribute. More on the URL in [How to use Gemlink](#how-to-use-gemlink)

The optional second attribute is the ***text*** attribute, which is the clickable hyperlink text in case the url should not be displayed as said text.

<img src="./images/with-text.png" alt="Example Image" width=40%/>
<img src="./images/without-text.png" alt="Example Image" width=40%/>

_On the left the both attributes are provided and on the right only the ***url*** attribute is used_

----------------------------------------------------------------------

## GUI Model

In this section, GUI model declaration details are described and an example is given.

First of all, an empty folder for the GUI model is needed. By name convention, the folder should be named with a ***gem*** prefix to avoid name collisions.  Inside of the new folder, a file called ***GemLink.gui*** is created.

`GemLink.gui` ([open](../GemLink.gui))

```
package basic.src.lib.gemlink;

component GemLink(
  String url,
  String text
)
```
_Listing 3.1: GUI model of GemLink._

Listing 3.1 shows an example GUI model for the hyperlink element with input parameters ***url*** and ***text***. The input parameters represent the described atrributes in the [Link Component](#link-component) section.

Furthermore, a Java-like _package_ with the corresponding name and path is stated before the component is declared.

However, the GUI model only specifies the interface of the _GemLink_ component and not the implementation itself, so an HTML file is needed in order to use the HTML as the template for the generated Angular component files.

----------------------------------------------------------------------

## Implementation

In this section, implementation details are explained and examples are shown.

```html
<a
[href]="url">
  {{text == undefined || text == '' ? url : text}}
</a>
```
_Listing 4.1: GemLink component implementation._

The url is placed inside the ```href``` attribute by using angulars [property binding](https://angular.io/guide/property-binding) which will use the public variable ```url``` defined in the **`gem-link.ts`** file.

The hyperlink text is displayed by using angulars [interpolation](https://angular.io/guide/interpolation) `{{  }}`. If the optional variable ***text*** is not provided or an empty string the ***url*** is displayed instead.

Note however, that renaming the GUI model may break the code, since generated files will remain untouched.


### Exporting GemLink

To be able to use the link component, the classes of the component need to be publicly available. To achieve this, a file named _index.ts_ is created inside the _gemlink_ folder and the generated files are exported.

`index.ts` ([open](../index.ts))
```ts
export * from './gemlink.module-gen';
export * from './gem-link/gem-link.component-gen';
```
_Listing 4.2: index.ts file of gemlink folder._

Moreover, the _index.ts_ file in the parent folder needs to be adjusted as well. 

`index.ts` ([open](../../../index.ts))
```ts
// other exports ...
export * from "./lib/gemlink";
```
_Listing 4.3: index.ts file of the parent folder._

### Generate

Now the component is ready to be generated. First `mvn clean` can be used to clear the old generated files which can fix some errors. ***Note*** hovever that this will stop any active running tester. The second mvn `install` command however won't stop any running tester and will generate the files. 

```console
> mvn clean
> mvn install -Pgui
```
_Listing 6.1: mvn generate._

Note: This only generated the component files needed for usage in tester or as an component in other gui files. To actually see the result this component needs to be referenced in a tester.

After executing the `mvn install -Pgui` command a new directory `gem-link` is generated. It contains `gem-link.component-gen.html` which will be empty since we are going to use a handwritten html file and the `gem-link.component-gen.ts` file which is the basic typescript code needed to make an angular component work.

----------------------------------------------------------------------

## How to use GemLink

In this section the possible inputs will be explained, as well as an example with its result will be shown.

Links are just clickable text, therefore they are displayed _inline_ by default which makes them perfect to use inside longer texts or menus that use `<li>` lists to manage their items.

### Input Parameters

The ***url*** input parameter can be an absolute- or relative URL, link to an anchor ***#anchorname***, other protocols and even a script. 
[w3schools.com about href](https://www.w3schools.com/tags/att_a_href.asp).

The input parameter ***text*** is optional and represents the clickable text.

```html
<gem-link [url]="'https://www.google.com/'" [text]="'click here for Google'"></gem-link>
```
_Listing 4.4: Usage of component with both inputs and https._

```html
<gem-link [url]="'../app/my-relative-file.html'"></gem-link>
```
_Listing 4.4: Usage of component with only url input and relative path._