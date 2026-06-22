# GUI Validity Generator Internals

This document is aimed at generator developers/tool smiths and describes the generator implementation
and configuration for generation of GUI Form validity checks from OCL constraints.

The application developer modeling workflow is documented in
[`tutorial/12_OCLGUIValidityChecking.md`](tutorial/12_OCLGUIValidityChecking.md).

## Overview

OCL based GUI validity connects these generated pieces:

1. OCL invariants with error messages are transformed into Java classes with a method for checking validity and a method to get the error msg.
2. Domain classes expose per-attribute validation accessors.
3. Tagging of GUI components marks one argument as the domain entry and one
   argument as the generated validity target.
4. GUI Java generation adds a getter that converts a domain validation error
   into the displayable component validity type.
5. GUI HTML generation binds the target argument automatically unless the GUI
   model already provides it explicitly.

For example:

```ts
auto@GemTextInput(entry = person.name);
```

is generated as if the `validity` argument was also bound to a generated getter.
If the model contains an explicit `validity = ...`, automatic generation is
disabled for that component instance.

## Domain Validation

`DomainClassDecorator` creates the per attribute methods to check validity used by the GUI generation.
The check methode is generated even if no ocl constraint is given for an attribute or association. 
This allows application developers to override them via the TOP-Mechanism.

Relevant files:

- `umlp-tool/src/main/java/umlp/generator/domain/decorator/DomainClassDecorator.java`
- `umlp-tool/src/main/java/umlp/generator/ocl/OCLAttributeExtractor.java`
- `umlp-tool/src/main/resources/umlp/domain/CheckOCLConstraints.ftl`
- `umlp-tool/src/main/resources/umlp/domain/GetValidationError.ftl`

The generated domain class contains:

```java
public boolean checkOCLConstraints()
public Optional<ValidationError> get<Name>ValidationError()
```

`checkOCLConstraints()` evaluates all invariants known for the class.
`get<Name>ValidationError()` evaluates only invariants associated with the
attribute or association named `name` and returns the first present
`ValidationError`.

`OCLAttributeExtractor` decides which attributes an invariant belongs to. It
walks OCL invariants, detects field accesses on the invariant context type, and
only records invariants that define error messages. This matters because GUI
validity needs a user-facing message, especially the `shortError` text.

Associations are included in the generated accessor set as well, because GUI
validity can be derived from an association when validating inputs inside an
iteration over that association.

## Component Tags

GUI validity generated from OCL constraints is opt-in per component type through tags.
A component must tag exactly the source value parameter and the target validity parameter:

```text
tags GemTextInput {
  tag mc.fenix.input.GemTextInput.entry with domainValidationEntry;
  tag mc.fenix.input.GemTextInput.validity with domainValidationTarget;
}
```

The generator reads these tags through `TagLookUp`:

- `domainValidationEntry` marks the component argument whose expression points
  to a domain attribute, for example `person.name`.
- `domainValidationTarget` marks the component argument that receives the
  generated validity object, for example `validity`.

The production `GemTextInput` tags live in
`umlp-rte/frontend/input/src/main/gui/mc/fenix/input/GemTextInput.tags`.
Generator tests use the smaller test component in
`umlp-tool/src/test/resources/gui/mc/fenix/input/TestTextInput.tags`.

To enable automatic validity for another component, add equivalent tags to that
component's `.tags` file and ensure the target argument type can be constructed
with:

```java
new TargetValidityType("Error", errorMessage)
```

## GUI Detection Logic

`GUIGeneratorUtil` owns the decision whether a component instance receives
automatic validity:

- `getAutomaticDomainValidityBinding(ASTGUIComponent)`
- `shouldGenerateAutomaticDomainValidity(ASTGUIComponent)`
- `getAutomaticDomainValidityGetterNameTS(ASTGUIComponent)`
- `getAutomaticDomainValidityTargetArgName(ASTGUIComponent)`

Automatic validity is generated only when all conditions hold:

- the component declaration has a parameter tagged `domainValidationTarget`
- the component instance does not already provide that target argument
- the component instance has an argument tagged `domainValidationEntry`
- the entry expression is a direct field access, such as `person.name`

The explicit target-argument check is important. It lets application developers
override generated validity by writing:

```ts
field@GemTextInput(entry = person.name, validity = customValidity());
```

or disable it with:

```ts
field@GemTextInput(entry = person.name, validity = null);
```

## Generated GUI Java

`GUI2JavaClassDecorator` calls `addAutomaticDomainValidityGetter` after normal
argument getter/setter generation for each component.

Relevant files:

- `umlp-tool/src/main/java/umlp/generator/gui/genjava/decorator/GUI2JavaClassDecorator.java`
- `umlp-tool/src/main/resources/umlp/gui2java/GetValidityFromDomain.ftl`
- `umlp-tool/src/main/resources/umlp/gui2java/GetValidityFromDomainForAngular.ftl`

For a component id `auto` and target argument `validity`, the generated method
name is:

```java
getAutoValidity(...)
```

For frontend generation, a second Angular-tracked method is generated:

```java
getAutoValidityForAngular(...)
```

The generated getter:

1. calls `domainObject.get<Attr>ValidationError()`
2. reads `ValidationError.getShortError()`
3. creates the component validity object with status `"Error"`
4. returns `null` when no domain error exists

## Generated HTML Binding

`umlp-tool/src/main/resources/umlp/gui2html/Component.ftl` emits the automatic
Angular binding after regular component arguments:

```html
[validity] = 'getAutoValidityForAngular__ChangeDetection(...)'
```

The target argument name and getter name come from `GUIGeneratorUtil`. Iteration
item parameters are forwarded in the same way as for generated argument getters,
so validity still works for repeated components.

Because the binding is emitted only when `shouldGenerateAutomaticDomainValidity`
is true, explicit GUI arguments remain untouched.
