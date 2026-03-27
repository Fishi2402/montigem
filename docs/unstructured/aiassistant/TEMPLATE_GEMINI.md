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

> **Rule:** Never start dev servers unless explicitly asked.

---

## 📁 Project Structure

```text
<project-root>/
├── src/main/resources/models/     # CD4A & GUI DSL models (.cd, .gui)
├── src/main/java/<base-package>/  # Hand-Written Code (HWC)
│   └── internal/                  # Backend-only helpers (ignored by JSweet)
├── frontend/src/                  # Generated Angular code – DO NOT EDIT
└── build/                         # All generated artifacts – NEVER MODIFY
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