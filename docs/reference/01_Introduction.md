# 1. Introduction

MontiGem is a model-driven, low-code platform for generating full-stack web applications. It bridges the gap between high-level modeling and modern web technologies by generating a Spring Boot backend and an Angular frontend from a common set of models.

## 1.1 Architecture Overview

A MontiGem application consists of three main layers:

1.  **Models:** The single source of truth.
    *   **Class Diagrams (CD4A):** Define the data structure, domain entities, and services.
    *   **GUI Models (GUI DSL):** Define the user interface structure, layout, and binding to domain data.
2.  **Generator:** Transforms models into executable code.
    *   **Backend Generator:** Produces Java code (Spring Boot, JPA/Hibernate) for the server.
    *   **Frontend Generator:** Produces TypeScript (Angular) and HTML/SCSS for the client. It uses **JSweet** to transpile hand-written Java frontend logic into TypeScript.
3.  **Runtime Environment (RTE):** A set of libraries providing common functionality.
    *   **Synchronization:** Handles real-time data sync between server and clients via WebSockets.
    *   **Command System:** Encapsulates all data modifications as commands.
    *   **Manager:** A singleton providing access to domain objects.

## 1.2 The TOP Mechanism

MontiGem employs the **Generation Gap Pattern** via the **TOP Mechanism** to allow safe customization of generated code.

*   **Generated Code:** For a model element `Foo`, the generator creates an abstract base class `FooTOP`.
*   **Hand-Written Code (HWC):** The developer creates a subclass `Foo extends FooTOP`.
*   **Injection:** The generator and RTE are configured to instantiate `Foo` instead of `FooTOP` whenever an instance is needed.

This applies to:
*   **Backend Entities:** `Customer extends CustomerTOP`
*   **Commands:** `RentItem extends RentItemTOP`
*   **Frontend Components:** `CarOverview extends CarOverviewTOP`

---
[Back to Index](../MontiGem_Documentation.md) | [Next: Data Modeling >](02_DataModeling.md)
