# 9. Project Setup & Build

## 9.1 Folder Structure

*   `models/`: `.umlp` and `.gui` files.
*   `backend/`: Spring Boot application.
*   `frontend/`: Angular application.

## 9.2 Gradle Tasks

*   `gradle build`: Builds the entire project.
*   `gradle :backend:run`: Starts the backend server.
*   `gradle :frontend:runApp`: Starts the frontend dev server.
*   `gradle :frontend:sync_gui2java2ts`: Updates the frontend code after model changes (faster than full restart).

## 9.3 Custom Build Tasks

You can extend `build.gradle` to add custom tasks, e.g., to copy assets.

```groovy
// frontend/build.gradle
tasks.register("copySourceCodeToAssets", Copy) {
  from("src/main/gui") { include "**/*.gui" }
  into layout.buildDirectory.dir("app/src/assets/source-code")
}
tasks.named("sync_gui2java2ts") { dependsOn "copySourceCodeToAssets" }
```

---
[< Previous: TOP Mechanism](08_TOPMechanism.md) | [Back to Index](../MontiGem_Documentation.md) | [Next: How-To Guides >](10_HowTo_MasterDetail.md)
