# MontiGem Patterns, Pitfalls & Best Practices

This document consolidates common design patterns, pitfalls, and best practices for developing applications with MontiGem. It covers GUI modeling, frontend logic, visualization, and data modeling.

---

## 1. GUI Modeling Patterns

### 1.1 The "Logic-in-Java" Pattern
**Goal:** Keep the GUI DSL clean and avoid parser errors with complex expressions.

*   ❌ **Pitfall:** Writing complex boolean logic directly in `@guard`:
    ```java
    @guard(p.status == ACTIVE && searchTerm != null && p.name.contains(searchTerm))
    ```
    *   *Consequence:* Parser errors ("Unable to derive type") or misinterpretation.
*   ✅ **Pattern:** Delegate logic to a Java method that returns `Boolean`.
    ```java
    // GUI
    @guard(isProjectVisible(p, searchTerm))

    // Java
    public Boolean isProjectVisible(Project p, String term) {
        if (term == null || term.isEmpty()) return true;
        return p.getName().contains(term) && p.getStatus() == ProjectStatus.ACTIVE;
    }
    ```

### 1.2 The "String Factory" Pattern
**Goal:** Display concatenated strings or formatted values.

*   ❌ **Pitfall:** Concatenating strings in the GUI DSL:
    ```java
    @GemText(value = "Status: " + p.status)
    ```
    *   *Consequence:* Compiler errors, especially with Enums or non-primitive types.
*   ✅ **Pattern:** Create a helper method in Java.
    ```java
    // GUI
    @GemText(value = getStatusLabel(p))

    // Java
    public String getStatusLabel(Project p) {
        return "Status: " + p.getStatus();
    }
    ```

### 1.3 Method Overloading Avoidance
**Goal:** Use the same logic for different contexts (e.g., navigation).

*   ❌ **Pitfall:** Calling the same method name with different arguments in the GUI DSL.
    ```java
    @GemButton(leftClick = navigateTo("PageA"))
    @GemButton(leftClick = navigateTo("PageB"))
    ```
    *   *Consequence:* Generator error "Method overloading is not yet supported".
*   ✅ **Pattern:** Create explicit alias methods in Java.
    ```java
    // GUI
    @GemButton(leftClick = goToPageA())
    @GemButton(leftClick = goToPageB())

    // Java
    public Consumer<Void> goToPageA() { return navigateTo("PageA"); }
    public Consumer<Void> goToPageB() { return navigateTo("PageB"); }
    ```

---

## 2. Frontend Logic Patterns

### 2.1 The "List-Item-State" Pattern
**Goal:** Maintain individual UI state for items in a list (e.g., a dropdown selection per row).

*   ❌ **Pitfall:** Using a global page variable for the state.
    *   *Consequence:* Changing one dropdown changes all rows simultaneously.
*   ✅ **Pattern:** Add a temporary field to the domain object (CD4A) to hold the UI state.
    ```java
    // CD4A
    class Task {
      String title;
      String tempStatus; // UI State
    }

    // GUI
    @iterate(Task t : tasks) {
      @GemDropdownInput(entry = t.tempStatus, ...)
    }
    ```

### 2.2 Automatic List Initialization
**Goal:** Display a list of domain objects (e.g., all projects).

*   ❌ **Pitfall:** Loading the list manually in Java (`Manager.getAll()`) and storing it in a local variable.
    *   *Consequence:* The list often remains empty or doesn't sync because the framework's auto-wiring is bypassed.
*   ✅ **Pattern:** Declare the list as a page parameter.
    ```java
    page ProjectOverview(List<Project> projects) { ... }
    ```
    *   *Benefit:* MontiGem automatically initializes and syncs this list.

### 2.3 Event Typing (Consumer<Object>)
**Goal:** Handle events like `keyRelease` that pass data.

*   ❌ **Pitfall:** Using specific types in the handler signature: `public Consumer<String> handleInput()`.
    *   *Consequence:* Incompatibility with the generated `TOP` class, which often expects `Consumer<Object>`.
*   ✅ **Pattern:** Use `Consumer<Object>` and cast internally.
    ```java
    public Consumer<Object> handleInput() {
        return obj -> {
            if (obj instanceof String) {
                String s = (String) obj;
                // ...
            }
        };
    }
    ```

### 2.4 JSweet Streams (Avoidance)
**Goal:** Process collections in frontend logic.

*   ❌ **Pitfall:** Using `java.util.stream.Stream` (e.g., `list.stream().filter(...)`).
    *   *Consequence:* JSweet does not fully support the Java Stream API. It may compile but fail at runtime.
*   ✅ **Pattern:** Use classic loops or a custom, eager Stream implementation if available.

---

## 3. Visualization & Layout Patterns

### 3.1 Responsive Charts (ResizeObserver)
**Goal:** Charts (D3, Chart.js, Vis.js) should adapt when the container size changes (e.g., resizing window, toggling sidebars).

*   ❌ **Pitfall:** Calculating size only once in `ngOnInit`.
    *   *Consequence:* Charts remain small or disappear when the layout changes.
*   ✅ **Pattern:** Use `ResizeObserver` and `getBoundingClientRect`.
    ```typescript
    // TypeScript (HWC)
    private resizeObserver: ResizeObserver;

    ngAfterViewInit() {
        this.resizeObserver = new ResizeObserver(() => {
            // Debounce and redraw
            this.redrawChart();
        });
        this.resizeObserver.observe(this.container.nativeElement);
    }
    ```

### 3.2 Flexbox & Height 0
**Goal:** A component should fill the available vertical space.

*   ❌ **Pitfall:** Setting `height: 100%` on a child when the parent has no explicit height.
    *   *Consequence:* The component collapses to height 0.
*   ✅ **Pattern:** Ensure the parent chain has defined heights (or `flex: 1`) and use `min-height` as a fallback.
    ```scss
    :host {
        display: block;
        height: 100%;
        min-height: 400px; /* Fallback */
    }
    ```

### 3.3 Hand-Written Components (HWC)
**Goal:** Create complex, reusable UI components with custom TypeScript logic.

*   ✅ **Pattern:**
    1.  Define the interface in `.gui` (generates `TOP` class).
    2.  Implement the logic in `.ts` (extending the generated component).
    3.  Define the template in `.html` and styles in `.scss`.
    4.  Place all files in `frontend/src/main/gui/...`.

---

## 4. Data Modeling Patterns

### 4.1 UI State in Domain Objects
**Goal:** Persist UI-specific state (like "is selected", "is expanded") without polluting the database.

*   ✅ **Pattern:** Use the `temp...` naming convention or non-persistent fields (if supported) in the CD4A model.
    ```java
    class Node {
        String name;
        boolean tempExpanded; // Used for UI tree expansion
    }
    ```
