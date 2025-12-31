# TOP Mechanism

Most of the more advanced features of MontiGem involve extending generated code
(models) with hand-written code. Such extensions are mostly done using so-called
TOP-mechanism, which is described in this document, as well as its variations.
This part mostly goes through the general concept, you will try using the
TOP-mechanim in later tutorial steps.

The TOP-mechanism is a way to integrate hand-written code into generated code
using inheritance. There are several other variations of TOP-mechanism that
can be used in a MontiGem app development, all of them have similar principles.

Further explanation will mention *source root* - a set of paths to folders that
MontiGem considers to be folder containing the source code of an app.

Default app setup has source roots for:

- **backend**

    - `backend/build/umlp2java/` – code generated from models
    - `backend/src/main/java` – hand-written code

- **frontend**

    - `frontend/build/app/umlpJava2ts/`, `frontend/build/app/gui2java2ts/` – code generated from models
    - `frontend/src/main/java` – hand-written java code
    - `frontend/src/main/gui` – hand-written ts, html, scss code and GUI models

- **models**

    - `models` – folder with models

!!! note
    The path to the generated code is normally irrelevant for the app development,
    since the paths are easier to find out from the qualified names of classes and
    components.

### TOP Backend Java Classes

Backend Java classes can be extended by defining a file with the same path
from the source root as of the generated Java file and then extending the
generated classes.

Simpler: given a qualified name of a class `foo.bar.Baz`, if you create a file
`foo/bar/Baz.java` in source root of hand-written backend code, the generator
will produce `foo/bar/BazTOP.java` file. You can then extend your `Baz` class
with `BazTOP` (`Baz extends BazTOP`) to inherit and override its
functionality. The generator will use your `Baz` class instead of the
generated one.

!!! note
    If some generated backend service instantiates `Baz` object to
    send it to the frontend, you can override instantiation logic using
    TOP-mechanism to send an altered version of the object instead.

### TOP Frontend TypeScript Classes from Class Diagram

Frontend TypeScript classes generated from a class diagram can be extended by
defining a file with the same path from the source root as of the generated
TypeScript file and then extending the generated classes.

!!! note
    Basically the same schema as for backend Java classes

Simpler: given a qualified name of a class `foo.bar.Baz`, if you create a file
`foo/bar/Baz.ts` in source root of hand-written frontend code, the generator
will produce `foo/bar/BazTOP.ts` file. You can then extend your `Baz` class
with `BazTOP` (`Baz extends BazTOP`) to inherit and override its
functionality.

### TOP Mechanism with JSweet

MontiGem targets application development completely with Java to enable app
developers to write and execute e2e tests as unit tests. For this purpose the
logic normally written in TypeScript is instead written in Java and then
translated by MontiGem to an Angular-compatible TypeScript code.

Transpiling Java code to TypeScript code means that concepts such as specific
types, methods, statements, expressions, etc. from Java have to be mapped to an
equivalent concept in TypeScript. One example is the `Integer` type from Java
which is transpiled to a TypeScript `number`, e.g., whenever you return an
`Integer` in a Java method the TypeScript method transpiled by JSweet will
return a `number`.

Frontend Java classes generated from GUI models can be extended by defining a
file with the same path from the source root as of the generated Java file
exactly as Backend Java classes. Additionally, you have to add the following
annotation: `@umlp.jsweet.extension.annotation.Component` to the hand-written
class.

Simpler: given a GUI component `myapp.SomePage`, if you create a file
`myapp/SomePage.java` in source root of hand-written frontend code, the
generator will produce `myapp/SomePageTOP.java` file.

### TOP Frontend TypeScript Classes from GUI Models

In some cases hand-written Java component extension is not enough, e.g., when
the component uses a 3rd party library, like `d3.js`, or when the component
logic requires DOM manipulations.

Frontend TypeScript classes generated from GUI models can be extended by
defining a file with the same path from the source root as of the generated
TypeScript file and then extending the generated classes with `Component`
suffix and using generated `config` constant.

Simpler: given a GUI component `myapp.SomePage`, if you create a file
`myapp/SomePage.ts` in source root of hand-written frontend code, the
generator will produce `myapp/SomePageTOP.ts` and `myapp/SomePageComponent.ts`
file. You can then extend your `SomePage` class with `SomePageComponent` to
inherit and override its functionality. Additionally, you need to import and
use `config` constant from the `myapp/SomePageComponent.ts` file:

```ts
import { SomePageComponent, config } from "@src/myapp/SomePageComponent";
import { Component } from "@angular/core";

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class SomePage extends SomePageComponent {
}
```

!!! note 
    The `@Component(...)` annotation is always the same. There is a
    technical limitation imposed by Ivy compiler (used in Angular framework)
    that makes it difficult to completely eliminate this code. This issue will
    be resolved in the future.

!!! note
    If you have to overwrite the Angular logic in `SomePageComponent`
    (this is very rarely the case) you have to overwrite the TypeScript file.

### TOP Frontend Templates (HTML) and Stylesheets (SCSS)

Frontend HTML and SCSS files generated from GUI models can be **overwritten**
by defining a file with the same path from the source root as of the generated
HTML and SCSS files.

!!! note 
    HTML and SCSS do not support inheritance, which is why the generated
    code cannot be reused.

Simpler: given a GUI component `myapp.SomePage`, if you create a file
`myapp/SomePage.html` in source root of hand-written frontend code, this file
will be used instead of a generated one. The same applies to
`myapp/SomePage.scss`

!!! note
    Most of current MontiGem builds do not generate SCSS file at all, but have
    few default style definitions specified via `styles: config.styles` of a
    `Component` annotation.

### TOP Frontend Routing

Frontend routing files (with suffix `GemRouting.ts`) generated from GUI
**package** can be extended by defining a file with the same path from the
source root as of the generated routing files.

Simpler: given that there is a GUI page `myapp.SomePage`, if you create a file
`MyappGemRouting.ts` in source root of hand-written frontend code, this file
will be used instead of a generated one. The file contains a `routes`
constant, that specifies the URL for all pages that reside in the
corresponding package.

There is global routing file `AppGemRouting.ts` that specifies routing for all
components. Normally you would only override `AppGemRouting`, as the URL doesn't
really matter to the end user. If you are curious about routing rules, the full
set of rules are specified in the [Angular
guide](https://v17.angular.io/guide/router).

For example, try changing the default route in a hand-written extension of
`AppGemRouting` by adjusting the `redirectTo` value:
```ts
/* (c) https://github.com/MontiCore/monticore */
import { Routes } from "@angular/router";
import { routes as routesTOP } from "AppGemRoutingTOP";

export const routes: Routes = [
  // set default path
  {
    path: "",
    pathMatch: "full",
    redirectTo: "gui/Start"
  },
  ...routesTOP
]
```
Since `routes` is a JSON object, you can create your own JSON object (must be
assigned to `routes` variable) and reuse the contents of the generated
`routesTOP` object with a spread operator (`...routesTOP`). In this example the
hand-written `routes` makes sure that navigating to your website without
specific path (e.g., `www.some-website.com` or `localhost:4200` during the
development), the app user will get redirected to the `Start` page.
