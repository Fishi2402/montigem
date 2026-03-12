# 5. Runtime Environment (RTE) & Synchronization

The RTE handles the communication between frontend and backend.

## 5.1 The Manager

The `Manager` (e.g., `CarrentalManager`) is a singleton available in both Backend (Java) and Frontend (TS). It holds the state of all loaded domain objects.

*   **Identity Map:** Ensures that for a given ID, only one object instance exists in memory.
*   **CRUD:** `getById(id)`, `getAll()`, `create()`, `delete()`.

## 5.2 The Command System

Synchronization is based on the **Command Pattern**.

1.  **Action:** User clicks a button -> `user.setName("Alice")`.
2.  **Command Creation:** The setter `setName` creates a `User_SetNameCommand(id, "Alice")`.
3.  **Execution (Local):** The command is executed locally (optimistic update).
4.  **Transmission:** The command is serialized to JSON and sent via WebSocket to the backend.
5.  **Execution (Backend):**
    *   The backend receives the JSON.
    *   Deserializes it to the Java Command class.
    *   Executes `doAction()`: Updates the backend object and persists it to the DB.
6.  **Broadcast:** The backend sends the command JSON to all *other* connected clients.
7.  **Execution (Remote):** Other clients receive the JSON, deserialize it, and execute it locally.

## 5.3 The Observer Pattern

The frontend uses an Observer pattern to automatically update the UI.

*   **Registration:** When a GUI component uses a domain object (e.g., `@GemText(value=user.name)`), the generated code automatically registers an observer on `user`.
*   **Notification:** When `user.setName(...)` is called (locally or via sync), the observer is triggered.
*   **Update:** The Angular Change Detection is triggered, and the UI updates.

**Manual Observation:**
You can manually register observers in your HWC:
```java
GUIUtils.registerObserverToDomainObject(user, () -> {
    // Callback when user changes
});
```

---
[< Previous: Frontend Logic](04_FrontendLogic.md) | [Back to Index](../MontiGem_Documentation.md) | [Next: Styling >](06_Styling.md)
