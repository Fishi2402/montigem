# MontiGem Patterns & Pitfalls

This document describes common mistakes during development with MontiGem/JSweet and the resulting best-practice patterns.

---

## 1. GUI Modeling & Layout Patterns

### 1.1 Complex Logic in Guards
*   ❌ **Error-prone way:** Complex Java expressions directly in the `@guard`: `@guard(p.status == ACTIVE && searchTerm != null)`.
*   ✅ **The MontiGem way:** Move logic to a Java method: `@guard(isVisible(p, searchTerm))`.

### 1.2 String Concatenation in Arguments
*   ❌ **Error-prone way:** Adding strings in the GUI DSL: `@GemText(value = "Status: " + p.status)`.
*   ✅ **The MontiGem way:** Create a "String Factory" method in Java: `@GemText(value = getStatusLabel(p))`.

### 1.3 Semicolons & Commas in Component Lists
*   ❌ **Error-prone way:** Missing delimiters after `@guard` or `@iterate` within `components = [...]`.
*   ✅ **The MontiGem way:** Terminate statements in the block with `;` and separate blocks in the array with `,`:
    ```java
    components = [
        @guard(cond) { @GemText(...); }, // Comma as separator!
        @iterate(item : list) { @GemText(...); }
    ]
    ```

### 1.4 Correct Use of GemDialog
*   ❌ **Error-prone way:** Removing the dialog completely from the DOM via `@guard` (e.g., `@guard(isOpen) { @GemDialog(...) }`).
*   ✅ **The MontiGem way:** Pass a boolean parameter to the page and bind it to `isOpen`: `myDialog@GemDialog(isOpen = isMyDialogOpen)`. Event handlers then control the field `this.setIsMyDialogOpen(true)`.

### 1.5 Flexbox & Height 0
*   ❌ **Error-prone way:** Setting `height: 100%` on a child when the parent has no explicit height (element collapses).
*   ✅ **The MontiGem way:** Ensure the parent chain has heights, or use a fallback: `min-height: 400px;`.

---

## 2. Java HWC & JSweet Patterns

### 2.1 Boolean Getters
*   ❌ **Error-prone way:** For the attribute `boolean isSelected`, call the getter `getIsSelected()`.
*   ✅ **The MontiGem way:** MontiCore generates `isIsSelected()` by default.

### 2.2 Package Naming
*   ❌ **Error-prone way:** Using the capitalized CD model as a Java package (`import Dtmm.Assessment;`).
*   ✅ **The MontiGem way:** Java packages are always lowercase (`import dtmm.Assessment;`). Only in the `.gui` is the model referenced directly (`Dtmm.Assessment`).

### 2.3 Primitive vs. Wrapper Return Types
*   ❌ **Error-prone way:** Overriding a TOP method and returning `boolean`, even though the TOP class expects `java.lang.Boolean`.
*   ✅ **The MontiGem way:** Always use wrapper classes (`Boolean`, `Integer`) for HWC returns to the GUI.

### 2.4 Calling Singleton Commands from the Frontend
*   ❌ **Error-prone way:** Calling `App.getInstance().myMethod()` from the frontend HWC.
*   ✅ **The MontiGem way:** Singleton methods become commands. Use the generated static call: `App.executeMyMethod(param);`.

### 2.5 The "List-Item-State" Problem
*   ❌ **Error-prone way:** Storing UI state (e.g., dropdown selection in a list) in a page variable. Changing one changes all.
*   ✅ **The MontiGem way:** Include the UI state in the domain object (CD) as a `temp...` field: `class Task { String tempStatus; }`.

### 2.6 Manual List Management
*   ❌ **Error-prone way:** Loading and saving a list manually via `SEOrgaManager.getProjectList()`. No live synchronization occurs.
*   ✅ **The MontiGem way:** Include the list as a parameter in the page header of the `.gui`. MontiGem initializes and synchronizes it automatically.

### 2.7 Java Streams and JSweet
*   ❌ **Error-prone way:** `myGemList.stream().filter(...).collect(...)`. (Results in JS runtime errors).
*   ✅ **The MontiGem way:** Use classic `for` loops.

### 2.8 Mandatory Fields in Builders
*   ❌ **Error-prone way:** Creating an object via `builder().build().get()` and setting mandatory fields afterwards. This often leads immediately to validation errors during the build.
*   ✅ **The MontiGem way:** Put all mandatory fields directly into the builder chain.

---

## 3. Architecture & Performance Patterns

### 3.1 Manager Architecture (TOP vs. Impl)
*   ❌ **Error-prone way:** Overriding and mixing methods directly in the HWC Manager.
*   ✅ **The MontiGem way:** Mark the Impl class with `@jsweet.lang.Erased` and mark backend methods with `@BackendOnly`.

### 3.2 Responsive Charts (ResizeObserver)
*   ❌ **Error-prone way:** Calculating chart size only in `ngOnInit`. (Charts disappear during layout changes).
*   ✅ **The MontiGem way:** In TypeScript, set a `ResizeObserver` on the container to redraw the chart upon changes (e.g., sidebar toggle).