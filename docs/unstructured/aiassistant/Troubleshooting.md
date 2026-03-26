# Troubleshooting & Best Practices

This document collects common pitfalls and best practices encountered during development with MontiGem.

---

## 1. GUI DSL & Components

### 1.1 Styling & Layout of Standard Components
**Problem:** Styling parameters like `hAlign` or `style` on components like `GemText` or `GemButton` are ignored or cause errors.
**Solution:**
*   **Alignment:** Use the `hAlign` (horizontal) or `vAlign` (vertical) parameters on the **parent container** (e.g., `GemColumn`, `GemRow`).
*   **Styling:** For custom styles, assign an **identifier** to your component instance (`myButton@GemButton(...)`) and target it in your component's SCSS file (`.myButton { ... }`).
*   **Inner Elements:** Many components have an outer wrapper. To style the inner part, use `::ng-deep`:
    ```scss
    :host ::ng-deep .myButton .gem-button { background-color: navy; }
    ```

### 1.2 Complex Expressions in Parameters
**Problem:** Expressions with string concatenation and method calls directly in a component parameter lead to parser errors.
**Solution:** Move the logic into a dedicated helper method in your Java HWC file and call that method from the GUI model.

---

## 2. Backend & Commands

### 2.1 Implementing Singleton Commands (`0xC0FFEE00`)
**Problem:** Build fails with `Custom implementation of command class ... required`.
**Solution:** Create a class in `backend/src/main/java/.../commands/` matching the command name, extend the `...TOP` class, and implement `public SimpleResult<Void> doAction()`.

### 2.2 Loading Resources from JARs
**Problem:** `Paths.get(url.toURI())` works in IDE but fails with `FileSystemNotFoundException` in the packaged JAR.
**Solution:** Use `ClassLoader.getResourceAsStream()`.

---

## 3. JSweet & TypeScript

### 3.1 Overwriting Hand-Written TypeScript Files
**Problem:** Custom `MyComponent.ts` gets overwritten by JSweet during the build.
**Solution:** Create a separate TS file (e.g., `MyHelper.ts`) without a Java counterpart. Import it into the Java Component context via the annotation: `@Component(tsImports={"MyHelper, from 'path/to/MyHelper'"})`.

### 3.2 Asynchronous Operations (`async/await`)
**Problem:** TypeScript errors with Promises when accessing RTE elements.
**Solution:** 
* Use `for await (const item of list)` for `IUMLPList`.
* Use `await` for association getters. 
* Annotate the Java HWC method with `@jsweet.lang.Async`.

---

## 4. Testing

### 4.1 `page.init()` in Tests
**Problem:** Tests fail with `NullPointerException` or unexpected state.
**Solution:** The `init()` method containing startup logic is **not** called automatically in Unit/E2E Tests. Always call `page.init()` manually in your test setup (`@BeforeEach`) after injecting dependencies.

### 4.2 Redundant Setters for Singletons
**Problem:** Cluttered tests with manual `setApp()` or `setManager()`.
**Solution:** The component's `init()` method usually fetches singletons automatically. You rarely need to inject them manually in tests.

---

## 5. Exports & Rendering

### 5.1 Charts in PDFs
**Problem:** Charts (ApexCharts, Chart.js) are missing when exporting a page to PDF.
**Solution:**
*   **Canvas:** Use `myCanvasElement.toDataURL('image/png')` before exporting.
*   **SVG:** Do not convert directly. Manually redraw the chart geometry using PDF primitive APIs (e.g. `doc.rect()`, `doc.text()` in jsPDF).