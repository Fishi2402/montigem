# Modeling tests with sequence diagrams

The idea is to use sequence diagrams to model unit tests.

## Generator Concept

Since the current generated code does not support the monitoring of return calls of methods (and exits via exceptions), 
the generator needs to be adjusted to generate the following test-infrastructure for such tests:

Given the following class diagram:
```umlp
cd orig {
  class Foo {
    int bar();
  }
}
```

Our generator currently generates
```
class FooTop {...}
class Foo extends FooTop {...}
```
where `Foo` may contain handwritten code.

We need to expand this structure:
```
class FooMonitor extends Foo {
  int bar() {
    reportStart(); 
    try {
      int res = super.bar();
      reportEnd(res);
      return res;
    } catch (Exception e) {
      reportError(e);
      throw e;
    }
  }
}
```

### Implementation note on inheritance

Since in our system all instances are Builders, we don't want to extend `Foo` but `FooBuilder` instead.

```
class A
class B extends A
class ABuilder extends A
class BBuilder extends B //implements methods of ABuilder
class AMonitor extends ABuilder
class BMonitor extends BBuilder //implements methods of AMonitor
```

### Static delegators

We don't need to adjust these, since they all delegate to adjusted instances

### Instance creation

We need to make sure, that every call to a new instance is replaced by an adjusted monitor instance. 
