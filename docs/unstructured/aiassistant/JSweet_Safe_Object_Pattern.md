# JSweet & MontiGem Specifics: The "Safe-Object" Architecture Pattern

To avoid `NullPointerException` and `ClassCastException` during JSweet transpilation (especially with complex domain objects), MontiGem often utilizes specific architecture patterns. These are essential to prevent JSweet transpiler crashes or runtime errors in the browser.

## 1. The "Safe-Object" Architecture Pattern
*   **Async Loading:** Data is loaded asynchronously in `init()`, and the UI state is updated via generated `setVariable()` methods.
*   **List-Mapping:** `IUMLPList` objects are converted into native JS arrays within `@Replace` blocks using `.toArray()`. This allows the use of standard TypeScript methods like `find()` or `filter()`.
*   **Type-Decoupling:** Complex types in `.gui` signatures are often defined as a generic `Object` or an unspecific type. The logic in Java HWC classes then uses `@Replace` to perform clean TypeScript casts, bypassing type issues during transpilation.

## 2. JSweet Protection Strategy & Limits (HWC)
To prevent JSweet transpiler crashes:
*   **Empty Method Bodies:** Java method bodies in HWC classes used by JSweet in the frontend MUST be empty (e.g., `return null;`) if they are replaced in TypeScript via `@Replace` or if they are internally too complex for JSweet.
*   **Logic Delegation:** Complex Java logic for HWCs should be moved to static helper methods within an `internal` package (e.g., `myproject.internal.*`). **This package is ignored by the JSweet transpiler.**
*   **No `var` with Lists:** Avoid using `var` with complex generated types (especially lists), as JSweet may fail with type inference.
*   **No `getLast()`:** Use `list.get(list.size() - 1)` instead of `list.getLast()` for MontiGem lists.
*   **Exclude Files Completely:** If backend utility classes are located in the frontend path, exclude them in the `build.gradle`:
    `tasks.named("umlpJava2ts") { excludeSource.add("glob:**/MyBackendOnlyUtil.java") }`
*   **Backend-Only Methods:** Use `@BackendOnly(BackendOnlyModes.THROW_ERROR)` (or `STATUS_OK` / `STATUS_ERROR` for commands), so JSweet replaces the method in the frontend with a simple error throw or result.

## 3. Asynchronicity & Imports in JSweet
*   **Iterating RTE Lists:** Iteration over `IUMLPList` is asynchronous. In TS, use: `for await (const item of myList)`.
*   **Loading Associations:** Getters for associations return a Promise. Use `await` (e.g., `await myObject.getCorrespondsTo()`).
*   **@Async Annotation:** Every Java HWC method that generates TS code with `await` (via `@Replace`) **must** be annotated with `@jsweet.lang.Async` in Java.
*   **Imports in @Replace:** Static imports for `@Replace` blocks must be declared via `@Component(tsImports = {"MyService, from 'path/to/MyService'"})`.

## 4. Shadowing Rule
DO NOT create a Java HWC file for components implemented purely in TypeScript (e.g., `FileUpload.ts`). The Java file would "shadow" and deactivate the TypeScript implementation during the build process, as the transpiler takes the `.java` file and generates a new `.ts` file from it, overwriting the manually created `.ts` file.

## 5. GUI-DSL Restrictions
*   **Parser Bugs:** Avoid nested `@iterate` blocks within `components = [...]`.
*   **Optional Handling:** Avoid functional operations on `Optional` (`map`, `flatMap`). Use classic `if (opt.isPresent())` checks in transpiled code.