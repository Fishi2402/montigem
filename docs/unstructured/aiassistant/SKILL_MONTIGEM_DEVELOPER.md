---
name: montigem-developer
description: >
  Expert development workflows, patterns, and rules for the MontiGem framework
  (Angular + Spring Boot + JSweet + CD4A/GUI-DSL).
  Use this skill for every implementation task: Java HWC, GUI modeling,
  backend logic, styling, and testing.
---

# MontiGem Developer Skill

You are an expert full-stack developer specialized in the **MontiGem framework**
(Angular + Spring Boot + JSweet + CD4A/GUI-DSL).

**Core principle:** Never guess parameter types, component signatures, or API shapes.
Always fetch the relevant documentation chapter *before* implementing anything.

---

## 📚 Documentation Reference Index

### Core Chapters (fetch with `web_fetch` before implementing)

| # | Topic | URL |
|---|-------|-----|
| 01 | Introduction | `https://monticore.pages.rwth-aachen.de/umlp/reference/01_Introduction.html` |
| 02 | Data Modeling (associations, cardinalities, types) | `https://monticore.pages.rwth-aachen.de/umlp/reference/02_DataModeling.html` |
| 03 | GUI Modeling (layouts, components, control structures) | `https://monticore.pages.rwth-aachen.de/umlp/reference/03_GUIModeling.html` |
| 04 | Frontend Logic (binding, event handling) | `https://monticore.pages.rwth-aachen.de/umlp/reference/04_FrontendLogic.html` |
| 05 | RTE & Synchronization (commands, manager, builders) | `https://monticore.pages.rwth-aachen.de/umlp/reference/05_RTE_Synchronization.html` |
| 06 | Styling & Theming (SCSS, ::ng-deep) | `https://monticore.pages.rwth-aachen.de/umlp/reference/06_Styling.html` |
| 07 | Backend | `https://monticore.pages.rwth-aachen.de/umlp/reference/07_Backend.html` |
| 08 | TOP Mechanism | `https://monticore.pages.rwth-aachen.de/umlp/reference/08_TOPMechanism.html` |
| 09 | Project Setup | `https://monticore.pages.rwth-aachen.de/umlp/reference/09_ProjectSetup.html` |
| 10 | Master-Detail How-To | `https://monticore.pages.rwth-aachen.de/umlp/reference/10_HowTo_MasterDetail.html` |
| 13 | Testing (Playwright, Simulated GUI) | `https://monticore.pages.rwth-aachen.de/umlp/reference/13_Testing.html` |

### Specialized Guides

- **JSweet User Guide:** `https://monticore.pages.rwth-aachen.de/umlp/JSweetUserGuide.html`
- **JSweet "Safe-Object" Pattern:** `https://monticore.pages.rwth-aachen.de/umlp/unstructured/aiassistant/JSweet_Safe_Object_Pattern.html`
- **Patterns & Pitfalls:** `https://monticore.pages.rwth-aachen.de/umlp/unstructured/aiassistant/MontiGem_Patterns_and_Pitfalls_Extended.html`
- **Troubleshooting:** `https://monticore.pages.rwth-aachen.de/umlp/unstructured/aiassistant/Troubleshooting.html`

### UI Component API

URL pattern: `https://monticore.pages.rwth-aachen.de/umlp/gendoc_components/mc/fenix/{package}/{ComponentName}.html`

Example: `.../fenix/input/TextInput.html`

Build the URL from the component's package and class name. Do not attempt to fetch
the pattern template literally – construct the concrete URL first.

---

## 🛡️ Core Rules & Guardrails

### 1. Strict Work Instructions & Prohibitions

- **No unauthorized Hacks:** Never use `@jsweet.lang.Replace` hacks on your own to hide logic from the JSweet transpiler. Only use `@jsweet.lang.Replace` when absolutely necessary (e.g., native browser functions like `confirm`, `alert`, `window.location`). Logic must not be "tunneled" in strings.
- **No Hacks:** If you are unsure about a solution, ask the user instead of applying a hack.
- **Abort on Transpiler Errors:** If transpiler errors occur (e.g., `ClassCastException`, `AssertionError`), immediately stop work, report the error, and discuss a clean structural solution with the user.
- **No "blind" Refactorings:** All changes to Java HWC must be functionally justified. Do not change method names or add delegators without consulting the user.
- **State Consistency:** Before moving logic (e.g., from parent to child components), analyze the impact on all affected classes and clarify with the user.
- **Always use `@Override`:** Consistently use `@Override` for all methods in the HWC class that implement signatures from the generated `...TOP` class. This ensures the correct method signature is hit and catches errors if the GUI signature changes.
- **Fields in GUI Header:** Add fields/variables in the GUI header (`.gui` file) rather than in the Java implementation whenever possible.

### 2. TOP Mechanism – Generated vs. Hand-Written Code (HWC)

- **Never modify** files in `build/` or files ending in `...TOP.java`.
- All customizations go into HWC classes in `src/main/java/`, inheriting from the `TOP` class.
- **Frontend HWC** must carry the annotation `@umlp.jsweet.extension.annotation.Component`.

### 3. JSweet-Safe Java (Frontend Logic)

JSweet transpiles Java to TypeScript. The following restrictions are **absolute**:

| ❌ Forbidden | ✅ Replacement |
|-------------|---------------|
| `java.io.*`, `java.nio.*` | Not available frontend-side |
| `list.getLast()` | `list.get(list.size() - 1)` |
| `var` for complex types | Explicit type declarations |

**Annotations for mixed frontend/backend classes:**

- `@BackendOnly` – marks command methods that should not be transpiled.
- `@jsweet.lang.Erased` – **mandatory** on any private helper method that uses
  `File`, `Path`, or `Files`. Without this, the frontend build **will** fail.
- Methods replaced by `@Replace` **must have empty bodies** (e.g., `return null;`).

**`internal` sub-package:** Move complex, non-transpilable Java logic into a
sub-package named `internal` (e.g., `myapp.internal.*`). JSweet ignores this package.

### 4. GUI Modeling (`.gui` files)

- Variables in `.gui` are strict: delegate concatenation, calculations, and complex
  expressions to helper methods in the Java HWC.
- **Always sync after any `.gui` change** before touching HWC:
  `gradle :frontend:sync_gui2java2ts`

### 5. Styling (SCSS)

- Use `:host ::ng-deep` to target inner DOM elements.
  Prefixing with `:host` is **mandatory** – bare `::ng-deep` leaks globally.
- Prefer `@mixin` over `@extend` to avoid unintended selector merging.

### 6. Testing

- In Simulated GUI tests, `page.init()` **must be called manually** to trigger
  initialization logic. It is not called automatically.

---

## 💡 Lessons Learned & Best Practices

### 1. GUI DSL Syntax & Parser Rules

- **Semicolons in `@iterate`:** Inside an `@iterate` block, **every top-level component** must be terminated with a semicolon `;` if it's part of a component list. Missing semicolons lead to cryptic `no viable alternative at input` errors.
- **Encapsulating `@iterate`:** If an `@iterate` block is part of a `components = [...]` array alongside other components, isolate it by encapsulating it in its own `GemRow` or `GemColumn` to avoid parser ambiguities.
- **Typing in `@iterate`:** While typed variables (e.g., `Dossier d`) are preferred, if the parser merges type and name (e.g., `Dossierd`), fall back to omitting the type (`d : dossiers`).

### 2. State Management & Reactivity

- **Declarative Reactivity:** Control reactivity via **parameter passing** rather than explicit change events (`onChange`). If a GUI method receives variables as parameters, the framework automatically detects the dependency and recalculates when variables change.
- **Two-Way Binding:** Use the **`entry`** parameter (for inputs) as the standard tool for Two-Way Data Binding to write values directly back to Java variables without manual setter callbacks.

### 3. HWC & Lifecycle

- **`@Override` Duty (e.g., Customizing Setters):** All methods in the HWC class that implement signatures from the generated `...TOP` class must be marked with **`@Override`**. This is especially critical in frontend HWC when overriding generated setters. Overriding these `set...` methods allows you to intercept state changes or UI events safely. Without `@Override`, if the GUI generator changes the method signature, your custom logic becomes "dead code" (it compiles but is never called). Always call `super.set...` within your overridden method to preserve the generated base behavior (like event emitting).
- **Initialization:** Use the generated `init...` hooks (e.g., `initSelectedDossierId()`) to set default values for page variables. They are called by the framework at the right lifecycle moment.

### 4. UI Components & Styling

- **Component Interfaces:** Never guess parameter names. Use the official Gendoc or check the symbol files (e.g., `GemCheckBoxInput` uses `checked` and `labelText`; `GemColumn` has **no** `padding` parameter).
- **Styling Strategy:** Avoid inline styles (`style = "..."`). The clean way is to give the component a **name/ID** in the `.gui` file (e.g., `filterSection @GemRow(...)`) and apply styling in the corresponding **`.scss`** file.

### 5. JSweet Transpilation

- **`@Replace` as Last Resort:** Use `@jsweet.lang.Replace` **only** for native browser functions (`confirm`, `alert`, `window.location`) or when Java code is absolutely untranspilable. Logic should primarily be in clean Java.
- **Optional Handling:** Many model class fields are `Optional`. Ensure correct `Optional.of(...)` wrapping when building objects, as JSweet is strict here.

---

## 🔥 Build Failure Protocol

If `gradle :frontend:buildApp` fails, work through this checklist in order:

1. **Scan for forbidden classes:** search for `File`, `Path` in
   frontend-transpiled classes. Annotate offending helpers with `@jsweet.lang.Erased`.
2. **Check `@Replace` methods:** all replaced methods must have empty bodies (`return null;`).
3. **Consult the Troubleshooting guide:**
   `https://monticore.pages.rwth-aachen.de/umlp/unstructured/aiassistant/Troubleshooting.html`

---

## 🚀 Standard Operating Procedure

```text
1. FETCH    Identify relevant .gui / .cd files and the matching docs chapter.
            Call web_fetch on the chapter URL before writing any code.

2. PLAN     Determine: HWC location, class name, TOP parent, required annotations.
            Verify the component API URL if a UI component is involved.

3. IMPLEMENT Write JSweet-safe Java HWC.
            Checklist: no IO, no getLast(), correct annotations.

4. SYNC     Run: gradle :frontend:sync_gui2java2ts

5. BUILD    Run: gradle :frontend:buildApp
            On failure → follow Build Failure Protocol above.

6. TEST     In simulated GUI tests, call page.init() manually.
```