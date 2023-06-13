# Venn-Diagram

----------------------------------------------------------------------

## Overview

- [Goal](#goal)
- [Venn Diagram](#Venn-Diagram)
- [Data Structure](#data-structure)
- [Implementation](#implementation)
- [How to use the Chart and Inputs](#how-to-use-the-chart-and-inputs)
- [References](#references)

## Goal

The goal of this project was to implement and test a new Angular library which contains a chart, in this case a venn diagram, using the ng2-charts library. The chart should be customizable using multiple inputs and different data structures. The functions used in the component were tested with unit-tests.

## Venn Diagram
A Venn diagram is a diagram style that shows the logical relation between sets. It is used to teach elementary set theory, and to illustrate simple set relationships in probability, logic, statistics, linguistics and computer science. 

<img src = "./images/venndiagram.png" alt = "Venn Diagram" width=30%>
<img src = "./images/venn-diagram.png" alt = "Venn Diagram" width=30%>


_The left diagram simply demonstrate the possible relation between A and B. The right diagram shows the relationship between smoke, fat, salt, meat._

<img src = "./images/Venn_diagram_example.png">

This example illustrates what the venn diagram works. There are two sets A and B, represented here as colored circles. The yellow circle, set A, is the collection of creatures with two legs that cannot fly, The blue circle, set B, represents the creatures can fly with fewer or more than two legs. This overlapping region is the intersection of A and B, would only contain those elements (in this example, creatures) that are members of both set A (two-legged creatures) and set B (flying creatures). The colored region is the union of set A and set B, it includes all living creatures that with two legs that cannot fly and the flying creatures. The outside region are the complement of set A and set B, which represents the living creatures that cannot fly with fewer or more than two legs.[1]


## Data Structure

The data structure is defined in gem-venn-diagram-types.types-gen.ts, and the used data structure is VennDiagramEntry.

### VennDiagramEntry

VennDiagramEntry consists of a list of two parameters: sets and size. sets is list of string and size is integer. 

```typescript
export class VennDiagramExampleComponent {
  public data: VennDiagramEntry[] = dummy;
}

const dummy: VennDiagramEntry[] = [
  {
    sets: ['test1'],
    size: 8
  },
  {
    sets: ['test2'],
    size: 5
  },
  {
    sets: ['test1', 'test2'],
    size: 3
  }
];
```


## GUI Model and Class Diagram

In this section, GUI model and class diagram implementation details are described and examples are given.

```
package charts.src.lib.gemvenndiagram;

import charts.src.lib.gemvenndiagram.GemVennDiagramTypes.VennDiagramEntry;

component GemVennDiagram(
    List<VennDiagramEntry>  sets
)

```

First of all, the java-like package should be declared at the beginning, import the VennDiagramEntry from the class diagram. In the component, the _sets_ should be defined, whose type is the list of VennDiagramEntry.


In addition to a GUI model, a class diagram is needed to describe additional types of more complex components, such as charts. Again, a Java-like import is done in this case. An example of a class diagram model can be seen in Listing 4.2

```
package charts.src.lib.gemvenndiagram;

classdiagram GemVennDiagramTypes {

  class VennDiagramEntry {
    List<String> sets;
    Integer size;
  }

}
```

Here is the generated file from class diagram.
```
  export interface VennDiagramEntry {
       sets?: string[];
       size?: number;
  }
```

##  Implementation

Here we illustrate the implementation of chart application.

### Hand-written Code Extension

To make use of our GUI model, the hand-written code  in gem-venn-diagram.component.ts has to be extended with the generated code. To achieve this, the generated file gem-venn-diagram.component-gen.ts must be imported into the hand-written gem-venn-diagram.component.ts and the generated class GemVennDiagram is used as a parent class for the hand-written one, as seen in the following Listing.


```typescript
// some imports
import { GemVennDiagram as GemVennDiagramTOP } from './gem-venn-diagram/gem-venn-diagram.component-gen';
import { VennDiagramEntry } from './gem-venn-diagram-types/gem-venn-diagram-types.types-gen';


@Component({
  // ...config
})

export class GemVennDiagram extends GemVennDiagramTOP  {
     // ... implementation
}

```

## How to use the Chart and Inputs

Note that it is highly recommended to put the whole bar-chart into a <div>-element, because the default width of the bar-chart element is calculated in respect to the parent <div>’s width.
Next, the possible inputs will be explained in detail and examples for these inputs are given.


### Sets

Sets is the list of VennDiagramEntry.

Sets contains all values that should be shown in the diagram.

```html
<div class="example">
  <montigem-venn-diagram
    [sets]="data"
  >
  </montigem-venn-diagram>
</div>
```

## References
[1] In _Wikipedia, the free encyclopedia- venn diagram_. https://en.wikipedia.org/wiki/Venn_diagram Accessed: 2022-08-12.
