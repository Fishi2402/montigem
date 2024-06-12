# Change Log

Only big changes are listed. Since there is no semantic versioning for Fenix,
the changes are tagged with a date.

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
