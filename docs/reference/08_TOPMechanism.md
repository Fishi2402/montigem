# 8. The TOP Mechanism

The TOP mechanism allows you to extend generated code without modifying it.

*   **Backend (Java):** Create a class `Foo extends FooTOP` in `backend/src/main/java`. The generator will use your class.
*   **Frontend (Java):** Create a class `Foo extends FooTOP` in `frontend/src/main/java`. Add `@umlp.jsweet.extension.annotation.Component`.
*   **Frontend (Templates):** Create `Foo.html` or `Foo.scss` in `frontend/src/main/gui` to completely overwrite the generated files.

---
[< Previous: Backend Customization](07_Backend.md) | [Back to Index](../MontiGem_Documentation.md) | [Next: Project Setup >](09_ProjectSetup.md)
