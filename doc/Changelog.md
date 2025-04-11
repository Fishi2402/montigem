# Change Log

Only big changes are listed. Since there is no semantic versioning for Fenix,
the changes are tagged with a date.

## Upcoming (if confirmed)

### Changes

For MontiGem developers:

Changes in GUI DSL AST and corresponding type checks:

- Add type parameters
  ```diff
    grammar GUIDSL extends de.monticore.symbols.OOSymbols,
                         de.monticore.literals.MCCommonLiterals,
                         de.monticore.types.MCSimpleGenericTypes,
  +                      de.monticore.types.TypeParameters,
                         de.monticore.expressions.CommonExpressions,
                         de.monticore.ocl.SetExpressions 
  ```
  `TypeParameters` are type parameter lists that can be used to declare generic model elements.
  ```diff
    symbol GUIComponentDeclaration implements Function =
  -   (["component"] | ["page"]) Name "("
  +   (["component"] | ["page"]) Name TypeParameters? "("
        (GUIParam || ",")*
      ")"
      GUIComponentAdaptation?
      GUIBlock?
    ;
  ```
  This change enables the declaration of generic GUI components as follows:
  ```java
  component FamilyTree<T>() {}
  ```
  Currently, a CoCo is implemented for GUIDSL that forbids the usage of `extends` in a type parameter

- Add type arguments
  ```diff
    scope GUIComponent implements Expression =
      id:GUIVariableDeclaration? "@"
  -   Name@GUIComponentDeclaration GUINamedArgs
  +   Name@GUIComponentDeclaration GUITypeArgs? GUINamedArgs
    ;
  
  + GUITypeArgs = "<" (MCTypeArgument || ",")* ">";
  ```
  A list of type arguments (defined in `GUITypeArgs`) can be added to a GUI component using `MCTypeArgument` provided in `MCSimpleGenericTypes`.

  This change enables following:
  ```java
  1  |  package example.generic;
  2  |  
  3  |  import example.Domain.Person;
  4  |
  5  |  page PersonHeritageDetails() {
  6  |    @FamilyTree<Person>();
  7  |  }
  ```
  Uses type `Person` (imported in line 4) as type argument of component `FamilyTree` in line 7.

## Upcoming

### Changes

For MontiGem users:

- GUI component usage, `@iterate` and `@guard` become `Expression`
- enable OCL list comprehensions
- Consequences: enable defining lists, list comprehensions and GUI components as
  default parameter values and as iterables in `@iterate`. Example: 
  ```java
  1  | package foo;
  2  | import Domain.Car;
  3  | 
  4  | page Bar(
  5  |   GUIViewElement title = @GemText(value = "Good"),
  6  | 
  7  |   List<String> textLines = ["Hello", " world!"],
  8  | 
  9  |   List<Car> cars = [c | c in Car, c.color.equals("red")]
  10 | ) {
  11 |   @iterate(GUIViewElement elem : [@GemText(), @GemButton()]) {
  12 |     elem;
  13 |   }
  14 | }
  ```
  * line 5 - allow default GUI components
  * line 7 - allow default lists
  * line 9 - allow list comprehensions
  * line 11 - allow lists in `iterate`

## 2025-03-15

### Changes

For MontiGem users:

Enable filtered commands in backend of MontiGem apps. The feature is a
prototype. Given a command as a string, for example:
```
{\"id\":12,\"commandType\":\"loadingtests.commands.Person_get_filtered\",\"participantId\":1,\"filter\":\"[ p | p in Person, p.age > 18, p.age < 23, p.age >= 15, p.age <= 66, p.name == \\\"Alice\\\", p.name != \\\"Bob\\\"]\" }
```
It can be passed to `CommandObjectMapper` to deserialize to get a command
object. The command object may also be created using a new constructor with
`String filter` as an input. The command with a filter will be processed and
return ids of objects satisifying the filter.

Only simple filters can currently be defined. Supported filter options:
- a single generator (`p in Person`)
- comparison operators (`<`, `>`, `<=`, `>=`, `!=`, `==`)
- the expressions used in a comparison can either constants (`18`, `"Alice"`) or
  simple field access (`p.age`) The fteature only supports SQL databases.

Most of the restrictions have a corresponding error message.

For MontiGem developers:

Given a class diagram `Foo` with a class `Bar`, following infrastructure (sorted
by the order of execution) handles the commands with filters:

- `Bar_get_filtered`
- `FooManager#_doGetFilteredBarList`
- `FooDAOFactory.getBarDAO().selectFilteredBarList(filter)`
- `BarDAO#ensureSelectFilteredBarCreated`
- `OCL2SQL` (RTE)
- `OCL2SQLPrinter` (RTE)
- `BarQueryBuilderMap`
- `BarQueryBuilder`

## 2025-02-20

### Changes

For MontiGem developers:

Changes in GUI DSL AST and corresponding type checks:

- add OCL SetExpressions
  ```diff
  grammar GUIDSL extends de.monticore.symbols.OOSymbols,
                          de.monticore.literals.MCCommonLiterals,
                          de.monticore.types.MCSimpleGenericTypes,
                          de.monticore.expressions.CommonExpressions,
  +                       de.monticore.ocl.SetExpressions
  ```
  OCL `SetExpressions` includes `SetEnumeration` (`SetEnumeration` is an
  `Expression`), which replaces `GUIList`. This enables lists as default
  parameter values:
  ```java
  7  |   List<String> textLines = ["Hello", " world!"],
  ```
  `GUIList` is removed from GUI DSL:
  ```diff
  -  GUIList = "[" (GUIArgValue || ",")* "]";
  ```
  `SetExpressions` includes `SetComprehension`, which enables following:
  ```java
  9  |   List<Car> cars = [c | c in Car, c.color.equals("red")]
  ```

-   `GUIComponent`, `GUIIterate`, `GUIGuard` implement `Expression`:
    ```diff
    -  scope GUIComponent =
    +  scope GUIComponent implements Expression =
        id:GUIVariableDeclaration? "@"
        Name@GUIComponentDeclaration GUINamedArgs
      ;

    -  scope (non_exporting) GUIIterate =
    +  scope (non_exporting) GUIIterate implements Expression =
        id:GUIVariableDeclaration? "@iterate" "(" IterateControl ")" GUIBlock
      ;

    -  GUIGuard =
    +  GUIGuard implements Expression =
        id:GUIVariableDeclaration? "@guard" "(" condition:Expression ")"
          thenStatement:GUIBlock
        ("else"
          elseStatement:GUIBlock
        )?
      ;
    ```

    This change enables following:
    ```java
    5  |   GUIViewElement title = @GemText(value = "Good"),
    ```
    ```java
    11 |   @iterate(GUIViewElement elem : [@GemText(), @GemButton()]) {
    ```

    Since `GUIComponent`, `GUIIterate`, `GUIGuard` and `GUIList` are currently
    used in places, where an `Expression` is being used, the corresponding
    Production rule is removed:
    ```diff
    -  GUIArgValue = Expression | GUIComponent | GUIGuard | GUIIterate | GUIList;

    -  GUINamedArg = Name@GUIParam "=" GUIArgValue;
    +  GUINamedArg = Name@GUIParam "=" Expression;

      GUIAdapt =
    -    position:["before" | "after" | "replace"] Name@Variable ":" GUIArgValue;
    +    position:["before" | "after" | "replace"] Name@Variable ":" Expression;
    ```

## 2025-01-15

### Additions

- added "generate TypeScript for library" (`gen4TSLib = true`) flag to the
  generator and frontend plugin. The flag forces the generator to generate
  relative import paths instead of path for source roots. Details in
  visualization library README.

### Changes

For MontiGem users:

- changed source root from `@src/` to empty string, e.g., 
  ```diff 
  - import { Bar } from '@src/foo/Bar';
  + import { Bar } from 'foo/Bar';
  ```
  Migration script:
  ```sh
  find . -type d -name "build" -prune -o \( -type f \( -name "*.ts" -o -name "*.java" \) -exec sed -i 's|@src/||g' {} + \)
  ```
- adjusted default `tsconfig.json` file accordingly. In most cases deleting the
  file should be enough. Adding library paths to `tsconfig.json` is no longer
  necessary.
- changed paths to styles from theme library, e.g.,
  ```diff
  - @import "../node_modules/@umlp/theme/theme";
  + @import "../node_modules/@umlp/theme/src/mc/fenix/theme/theme";
  ```
  Migration script:
  ```sh
  find . -type d -name "build" -prune -o -type f -name "styles.scss" -exec sed -i 's|umlp/theme|umlp/theme/src/mc/fenix/theme|g' {} +
  ```

For MontiGem developers:

- restructured visualization library. Details in visualization library README.
- partially moved import handling for TypeScript files to
  `TSImportHelper`.

## 2024-07-24

### Additions

- added generator for GUI RTE types, which can now be used backend-side to create gui data.
  Documentation can be found in `rte/RTETypesGenerator.md`.

## 2024-06-10

### Additions 

- from every list or constructor call a local variable is created in a
  component, if the object is created outside `@iterate`. If the object is
  inside an `@iterate`, an entry is added to a local (nested) map pointing to
  the object.

  *Example:*

  GUI model:
  ```ts
  page Foo() { bar@PersonCreate(p = Person()); }
  ```
  Java/TS:
  ```java
  class Foo {
    protected Person barP_0;
    public Person getBarP_0() { /*...*/ }
  }
  ```
  > Note: unlike most of the GUI things, the lists and constructors are created
  > once, so we create values once and put them into local variables.

- every component, list, constructor call, component input parameter, and an
  iterable of an `@iterate` has an initialization method. Those methods are
  chained together in a correct order, where the main initialization method is
  an `init` method. In Java code, the `init` method is not called. In TS code,
  the method is called from `ngOnInit` of the Angular extension (`XComponent.ts`
  class file).

  *Example:*

  GUI model:
  ```ts
  page Foo(Person baz) { bar@PersonCreate(p = Person()); }
  ```
  Java/TS:
  ```java
  class Foo {
    /* ... attrs ... */

    public void init() {
      this.initBaz();
    }

    public void initBaz() {
      /* ... baz initialization ... */
      initBarP_0();
    }

    public void initBarP_0() {
      /* ... barP_0 initialization ... */
      initBarP_0();
    }
  }
  ```
  > Note: the order is not optimal, will be changed
  > [#4130](https://git.rwth-aachen.de/monticore/monticore/-/issues/4130)

- every component now has a `destroy` method that `Manager.removeObserver(this)`

### Changes 

- no longer allowed to use an unreferencable variable in a GUI model, methods
  have to be used instead

  *Example:*

  Error:
  ```
  @GemText(value = iDontExist)
  ```
  Fine:
  ```
  @GemText(value = iDontExist())
  ```

- allowed to use methods as values for component arguments and iterables of
  `@iterate`. Not allowed to use methods in complex expression that can be
  refactored as a simple method call.
 
  > Note: can be used pretty much anywhere where expected, except `@guard`
  > condition
  > [#4131](https://git.rwth-aachen.de/monticore/monticore/-/issues/4131)

- renamed `router` to `_router`

  Helpful script for migration:
  ```sh
  find . -type d -name 'node_modules' -prune -o -type f -name '*.ts' -exec sed -i 's/this\.route/this\._route/g' {} +
  ```

- replaced DomainBuilder-typed attributes via Domain-typed attributes, e.g.,
  `PersonBuilder p` -> `Person p`. Domain-typed attributes have relevant
  methods, such `isBuildable` (added) and `isBuildFinished`.

- generated list attributes are changed from `GemList` to `Array` type
  > Note: the conversion is handled by the `init` chain, the updates are done
  > via `Observer` implementation. Observer of associations are not handled yet
  > [#4133](https://git.rwth-aachen.de/monticore/monticore/-/issues/4133)

- generated constructors now always have 0 parameters. You should call `set`
  methods for setting input parameters of components. Overridden TS constructors
  have to call `super()` instead of `super(route, router)`.

- initialization logic must always be in overridden `init`. `ngOnInit` should
  not be overridden, unless you know exactly why you need to do it.

- Derived class generation: renamed `get` methods to `calc`. `get` methods now
  call `calc` by default in Java. In TS `get` methods are abstract (in derived
  class `X`), or return local variable (in class `XImpl`).

  *Example:*

  CD:
  ```java
  / class DerivedInfo {
    Foo bar;
  }
  ```
  Java:
  ```java
  // DerivedInfo.java
  public Foo getBar() {
    return calcBar();
  }

  public abstract Foo calcBar();
  ```
  > Note: needed for init chain

  Helpful script for migration:
  ```sh
  find . -type f \( -name "*Impl.java" -o -name "*Impl.ts" \) \
  -not -path "*/node_modules/*" \
  -not -name "ManagerImpl.java" \
  -not -name "ClientImpl.java" \
  -not -name "ManagerImpl.ts" \
  -not -name "ClientImpl.ts" \
  -exec sed -i 's/ get/ calc/g' {} +
  ```

- renamed derived `size` to `calcSize`. Same explanation as above.

  Helpful script for migration:
  ```sh
  find . -type f \( -name "*Impl.java" -o -name "*Impl.ts" \) \
  -not -path "*/node_modules/*" \
  -not -name "ManagerImpl.java" \
  -not -name "ClientImpl.java" \
  -not -name "ManagerImpl.ts" \
  -not -name "ClientImpl.ts" \
  -exec sed -i 's/ size/ calcSize/g' {} +
  ```

- moved Observer logic from `XComponent` to `X` class

- `Observer` in TS are now extended using mixins instead of local variable
  initialized with an anonymous class.

### Fixes

- Constructor calls and lists should now work properly

  *Example:*
  ```ts
  page Foo(Person p) {
    @NestedThing(
      myNestedList = [[Person(), Person()], p.friends, [p]]
    );
  }
  ```

- `Event` parameters now get corresponding `EventEmitter` attributes that are
  called if the parameter is bound to other event in GUI model.
  > Note: reference parameters still miss proper event emitter logic
  > [#4119](https://git.rwth-aachen.de/monticore/monticore/-/issues/4119)
