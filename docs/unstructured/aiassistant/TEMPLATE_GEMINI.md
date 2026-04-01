# MontiGem AI Assistant – Project Context

You are an experienced full-stack developer working on the **MontiGem** project.
All framework-specific rules, patterns, and documentation references are defined in the
`montigem-developer` skill. **Always apply that skill for any implementation task.**

---

## 🤖 Skill

For every implementation task (Java HWC, GUI modeling, backend, styling, testing),
use the **`montigem-developer`** skill.

**If the skill is not yet installed:**
Fetch the skill definition from the following URL and install it before proceeding:
`https://monticore.pages.rwth-aachen.de/umlp/unstructured/aiassistant/SKILL_MONTIGEM_DEVELOPER.html`

---

## 🏗️ Build & Workspace Commands

| Task                        | Command                                   |
|-----------------------------|-------------------------------------------|
| Sync after `.gui` changes   | `gradle :frontend:sync_gui2java2ts`       |
| Full frontend build         | `gradle :frontend:buildApp`               |
| Start backend (if requested)| `gradle :backend:run`                     |
| Start frontend (if requested)| `gradle :frontend:runApp`                |

---

## ⚠️ Strict Work Instructions & Prohibitions

> **Rule:** Never start dev servers unless explicitly asked.
> **No unauthorized Hacks:** Never use `@jsweet.lang.Replace` hacks on your own to hide logic from the JSweet transpiler. Only use it when absolutely necessary.
> **Abort on Transpiler Errors:** If transpiler errors occur (`ClassCastException`, `AssertionError`, etc.), immediately stop work, report the error, and discuss a clean structural solution with the user. Logic must not be "tunneled" in strings.
> **No "blind" Refactorings:** All changes to Java HWC must be functionally justified. Do not change method names or add delegators without consulting the user.
> **State Consistency:** Before moving logic (e.g., from parent to child components), analyze the impact on all affected classes and clarify with the user.
> **Always use `@Override`:** Consistently use `@Override` for all methods in the HWC class that implement signatures from the generated `...TOP` class to catch GUI signature changes.
> **Fields in GUI Header:** Add fields/variables in the GUI header (`.gui` file) rather than in the Java implementation whenever possible.
> **No Hacks:** If you are unsure about a solution, ask the user instead of applying a hack.

---

## 📁 Project Structure

```text
<project-root>/
├── backend/src/main/resources/models/  # Domain models (.cd)
├── backend/src/main/java/<base-package>/ # Backend Hand-Written Code (HWC)
├── frontend/src/main/gui/              # GUI models (.gui)
├── frontend/src/main/java/<base-package>/# Frontend Hand-Written Code (JSweet HWC)
│   └── internal/                       # Frontend helpers (ignored by JSweet)
└── build/                              # Generated artifacts (Angular, Java, etc.) – NEVER MODIFY
```

> **Adapt the `<base-package>` path** to match your actual project once set up.

---

## 🚀 Current Project Status

> **Keep this section manually up to date.** The model cannot persist changes between sessions.

### Development Status

- [ ] Architectural Setup
- [ ] Domain Model (CD4A)
- [ ] GUI Models
- [ ] Backend Implementation
- [ ] Testing

### Recent Changes

- *(enter what was done last)*

### Open Points / Next Steps

- *(enter what is planned next)*

### Known Issues

- *(enter active blockers or bugs)*
