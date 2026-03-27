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

### 1. TOP Mechanism – Generated vs. Hand-Written Code (HWC)

- **Never modify** files in `build/` or files ending in `...TOP.java`.
- All customizations go into HWC classes in `src/main/java/`, inheriting from the `TOP` class.
- **Frontend HWC** must carry the annotation `@umlp.jsweet.extension.annotation.Component`.

### 2. JSweet-Safe Java (Frontend Logic)

JSweet transpiles Java to TypeScript. The following restrictions are **absolute**:

| ❌ Forbidden | ✅ Replacement |
|-------------|---------------|
| `java.util.stream.*` | Standard `for` loops |
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

### 3. GUI Modeling (`.gui` files)

- Variables in `.gui` are strict: delegate concatenation, calculations, and complex
  expressions to helper methods in the Java HWC.
- **Always sync after any `.gui` change** before touching HWC:
  `gradle :frontend:sync_gui2java2ts`

### 4. Styling (SCSS)

- Use `:host ::ng-deep` to target inner DOM elements.
  Prefixing with `:host` is **mandatory** – bare `::ng-deep` leaks globally.
- Prefer `@mixin` over `@extend` to avoid unintended selector merging.

### 5. Testing

- In Simulated GUI tests, `page.init()` **must be called manually** to trigger
  initialization logic. It is not called automatically.

---

## 🔥 Build Failure Protocol

If `gradle :frontend:buildApp` fails, work through this checklist in order:

1. **Scan for forbidden classes:** search for `File`, `Path`, `Stream` in
   frontend-transpiled classes. Annotate offending helpers with `@jsweet.lang.Erased`.
2. **Check `@Replace` methods:** all replaced methods must have empty bodies (`return null;`).
3. **Check for stream usage:** replace every `.stream()` chain with a `for` loop.
4. **Consult the Troubleshooting guide:**
   `https://monticore.pages.rwth-aachen.de/umlp/unstructured/aiassistant/Troubleshooting.html`

---

## 🚀 Standard Operating Procedure

```text
1. FETCH    Identify relevant .gui / .cd files and the matching docs chapter.
            Call web_fetch on the chapter URL before writing any code.

2. PLAN     Determine: HWC location, class name, TOP parent, required annotations.
            Verify the component API URL if a UI component is involved.

3. IMPLEMENT Write JSweet-safe Java HWC.
            Checklist: no IO, no Streams, no getLast(), correct annotations.

4. SYNC     Run: gradle :frontend:sync_gui2java2ts

5. BUILD    Run: gradle :frontend:buildApp
            On failure → follow Build Failure Protocol above.

6. TEST     In simulated GUI tests, call page.init() manually.
```