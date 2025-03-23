# Testing UMLP and UMLP Applications
Sections:
1. [Base Concept](#base-concept)
2. [Testing Base Features](#testing-base-features)
3. [Testing Synchronization between Server and Client(s)](#testing-synchronization-between-server-and-clients)
4. [Testing Synchronization between Server and (Multiple) Databases](#testing-synchronization-between-server-and-multiple-databases)
5. [E2E Testing using Simulated GUI](#e2e-testing-using-simulated-gui)

## Base Concept

The base concept for testing in UMLP revolves around the `TestSetup` interface.
This interface defines the basic structure for setting up and cleaning up test environments.
For each domain class diagram, multiple different implementations are generated.
Here for the `Flensburg` example:
1. `FlensburgMockTestSetup`, which keeps all domain objects in ram
2. `FlensburgSQLDBTestSetup`, which keeps them in a sql database(typically H2 during testing, Postgres during production)
3. `FlensburgNeo4JDBTestSetup`, which keeps them in a graph database(typically Neo4J)

Some tests want to execute actions in the role of the backend as well as in the role of a client.
To enable this in conjunction with the static delegate pattern, we bundle all static state in the generated implementations of `TestContext`.
The `TestSetup` sets up one backend `TestContext`, available via `getBackend()`, and a configurable amount of client `TestContexts`, available via `getClients(i)`.
To switch the current role, the `TestContext` has the `activate()` function, which switches all static delegates(such as the `FlensburgManager`). 

## Testing Base Features

Testing base features typically involves verifying the functionality of individual components or interactions within a single context (either backend or client).  These tests often utilize mock objects to isolate the unit under test. Check [CityInfoTest.java](examples/flensburg-test/backend/src/test/java/flensburg/CityInfoTest.java) for an example.

**Example (`CityInfoTest.java` excerpt, no synchronization):**

```java
public class CityInfoTest extends FlensburgMockTestSetup {

  public CityInfoTest() {
    super(ClientSetup.of(1)); // Only one "client" context is used, no actual synchronization
  }

  @Test
  public void testCityInfo(){
    // Backend is activated by default
    Car c1 = FlensburgManager.carBuilder().wheelLeft(false).namesOfAllParts(new ArrayList<>()).build().get();
    // ...
    
    CityInfo cityInfo = new CityInfo();

    assertNotNull(cityInfo);
    assertEquals(2, cityInfo.calcNumberOfCities());
    // ... other assertions ...
  }
}
```

This example tests calculations performed by the `CityInfo` class without involving any client-server or database synchronization.

## Testing Synchronization between Server and Clients

A crucial aspect of internal UMLP testing is verifying synchronization between the server (backend) and one or more clients.  This involves creating objects in one context (e.g., a client) and verifying that the changes are reflected in another context (e.g., the server or another client). It is crucial to activate the context, where changes should happen, before object modifications.

**Key Concepts:**

*   **Context Activation:**  Use `getBackend().activate()` or `getClients(i).activate()` to switch the active context before making changes.  The active context is where operations like object creation and modification will occur.
*   **Object Creation and Modification:**  Create and modify objects within the active context.
*   **Verification:** Switch to a different context (server or another client) and verify that the changes are reflected correctly.
*   **`assertNotSame`:** Use this to confirm that you're dealing with distinct object instances in different contexts (even though they represent the same data).
*   **`assertEquals`:** Use this to verify that the data within the objects is consistent across contexts.

**Example ([PersonSyncTest.java](examples/flensburg-test/backend/src/test/java/flensburg/sync/PersonSyncTest.java) excerpt):**

```java
public class PersonSyncTest extends FlensburgMockTestSetup {
  public PersonSyncTest(){
    super(2); // set number of clients
  }

  @Test
  public void testMainResidenceIsSyncedToServer(){
    // ...
    getClients(0).activate(); // Activate client 0
    Person p1001 = /* ... create person ... */;

    getBackend().activate(); // Activate the backend
    Person p1001Server = FlensburgManager.getPersonSure(startGemId); // Retrieve person from server

    assertNotSame(p1001, p1001Server); // Verify they are different instances
    assertEquals(startGemId, p1001Server.getGemId()); // Verify data consistency
    // ... more assertions ...
  }
    @Test
  public void testMainResidenceChangeIsSyncedToClient(){
      //...
    getClients(0).activate(); // Activate client 0
    Person p1001 = /* ... create person ... */;

    getBackend().activate();  // Activate server
    Person p1001Server = FlensburgManager.getPersonSure(startGemId);
    Address a2 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    p1001Server.setMainResidence(a2); // Modify the object in the SERVER context

    getClients(0).activate(); // Activate client context
    assertEquals("Aachen", p1001.getMainResidence().getCity()); // Check client

    //...
  }
}
```

This example demonstrates:

1.  Creating a `Person` object in the client context.
2.  Activating the backend context.
3.  Retrieving the corresponding `Person` object from the server.
4.  Verifying that the objects are different instances (`assertNotSame`).
5.  Asserting that the data is synchronized (`assertEquals`).

## Testing Synchronization between Server and (Multiple) Databases

UMLP also supports testing synchronization with databases.
When `hasDatabase()` returns `true`, the tests interact with a real database. 
The testing framework provides helper classes (e.g., `FlensburgSQLDBTestSetup`) to manage database connections and setup.

**Key Concepts:**

*  **`hasDatabase()`:**  Checks if a database is used in the test.
*  **DAO (Data Access Object):**  Use DAOs (e.g., `FlensburgDAOFactory.getPersonDAO()`) to interact directly with the database and verify data persistence.
*  **`TESTING_deepEquals`:** Provides a deep comparison method to check if objects retrieved from different context are identical.

**Example ([HumanSyncDbTest.java](examples/flensburg-test/backend/src/test/java/flensburg/sync/HumanSyncDbTest.java)):**

```java
public class HumanSyncDbTest extends FlensburgSQLDBTestSetup {
  // ...
  @Test
  public void testFieldChangeIsSavedInDb(){
    getClients(0).activate();
    var h = FlensburgManager.humanBuilder().age(20).build().get();

    HumanDAO dao = new HumanDAO(); // Get the DAO
    var hDb1 = dao.selectHuman(h.getGemId()).get(); // Retrieve from DB
    assertEquals(h.getAge(), hDb1.getAge());

    h.setAge(21); // Modify in the CLIENT context
    var hDb2 = dao.selectHuman(h.getGemId()).get(); // Retrieve again
    assertEquals(h.getAge(), hDb2.getAge()); // Verify change in DB
  }
}
```

We can also test multiple persistence implementations at the same time by defining all test cases(=methods) in an interface and creating one subclass for the mock, one for SQL, and one for Neo4J.
To enable variation points depending on the availability of the database, the `TestSetup` provides the method `hasDatabase`.

**Example ([PersonSyncTest.java](examples/flensburg-test/backend/src/test/java/flensburg/sync/PersonSyncTest.java) excerpt using `TESTING_deepEquals`):**
```java
public class PersonSyncTest extends TestSetup<FlensburgTestContext> {
  class PersonSyncMockTest extends FlensburgMockTestSetup implements PersonSyncTest {
    public PersonSyncMockTest() {
      super(2);
    }
  }

  class PersonSyncDBTest extends FlensburgSQLDBTestSetup implements PersonSyncTest {
    public PersonSyncDBTest() {
      super(2);
    }
  }

  @Test
  default void testAddOwnsIsSyncedToServer(){
      PersonBuilder pb1001 = /*...*/;
      // ...
      if(hasDatabase()){
        assertTrue(p1001Server.TESTING_deepEquals(FlensburgDAOFactory.getPersonDAO().selectPerson(p1001Server.getGemId()).get()));
        assertTrue(c_i_server.TESTING_deepEquals(FlensburgDAOFactory.getCarDAO().selectCar(c_i_server.getGemId()).get()));
      }
  }
}
```
This example demonstrates persisting a change to a `Human` object and verifies it in the database.

## E2E Testing using Simulated GUI

UMLP provides a way to perform end-to-end (E2E) testing using simulated GUIs.
These tests simulate user interactions with generated GUI components and verify the resulting state changes in the underlying objects and across contexts.

**Key Concepts:**

*   **GUI Model:**  The tests are based on GUI models that define the structure and behavior of the UI (see comment in `EditierePersonGeneratedGUITest.java`).
*   **Generated GUI Classes:**  UMLP generates Java classes representing the GUI components (e.g., `PersonEdit`).
*   **Simulating User Actions:**  The tests interact with the generated GUI classes to simulate user actions like setting text in input fields (`setGemTextInput...Entry`) or clicking buttons (`gemButton...LeftClick`).
*   **Observing State Changes:**  The tests verify that the simulated actions lead to the expected state changes in the underlying objects (both in the client and server contexts).

**Example ([EditierePersonGeneratedGUITest.java](examples/flensburg-test/backend/src/test/java/flensburg/gui/editierepersoncars/EditierePersonGeneratedGUITest.java) excerpt):**

```java
public class EditierePersonGeneratedGUITest extends AbstractGUITest {
  // ...

  @Test
  public void editPersonGUITest() {
    // ... object creation ...
    PersonEdit e = new PersonEdit(); // Create GUI instance
    e.setP(p1); // Link GUI to the Person object

    // Check initial GUI values
    assertEquals("theo", e.getGemTextInput4Entry());
    assertEquals("24", e.getGemTextInput5Entry());

    // Simulate changing the age in the GUI
    e.setGemTextInput5Entry("33");
    assertEquals(33, p1.getAge().intValue()); // Verify object change

    // Simulate clicking the decrease button
    e.gemButton6LeftClick(null);
    e.gemButton6LeftClick(null);
    assertEquals("31", e.getGemTextInput5Entry()); // Verify GUI change
    assertEquals(31, p1.getAge().intValue()); // Verify object change
  }
  //...

  @Test
  public void observePersonGUITest() {
    // ... setup multiple clients and GUI instances ...

    getClients(0).activate();
    // Simulate changing the age in GUI of client 1
    e1.setGemTextInput5Entry("33");
    assertEquals("33", e2.getGemTextInput5Entry()); // Verify change in GUI of client 2

    getClients(1).activate();
    // Simulate clicking button in GUI of client 2
    e2.gemButton6LeftClick(null);
    e2.gemButton6LeftClick(null);
    assertEquals("31", e1.getGemTextInput5Entry()); //verify change in GUI of client 1.
  }
}
```

This example demonstrates:

1.  Creating a `PersonEdit` GUI instance.
2.  Linking it to a `Person` object.
3.  Simulating user input by setting text in an input field.
4.  Verifying that the change is reflected in the underlying `Person` object.
5.  Simulating a button click and observing the effect on both the GUI and the object.
6.   Testing GUI updates across multiple clients.
