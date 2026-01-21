# Creating a basic data structure 

The core of a MontiGem app is a data structure modeled with a class diagram. The
most basic one requires few elements to get started:

- class diagram
- class
- enum
- association

!!! note
    A complete list will be discussed later, you may also take a look at the
    [lecture slides](https://www.se-rwth.de/sle_material/SLE01.3.CD4Analysis/) and
    [CD4Analysis project](https://github.com/MontiCore/cd4analysis)

## Class diagram

MontiGem tool uses a single class diagram as a source of an app's data
structure. Rough structure of a class diagram:

```java
package mypackage.name;

import external.TypeName;
import another.external.TypeName2;

umlp ClassDiagramName {
  /* classes, associations, etc. */
}
```

It contains

- an optional single package declaration
- an optional list of imports
- a single class diagram declaration with a keyword `umlp` (or `classdiagram`)

!!! note 
    There is no difference between using `umlp` and `classdiagram` as of
    yet, but prefer `umlp` for describing the app's data structure.

- contents of the class diagram, e.g., classes, enums, associations, etc.

Car rental example (located in the `models` directory):

```java
import java.time.LocalDateTime;

umlp Carrental {
  /* contents */
}
```

!!! note 
    Following information about generated classes is useful for
    understanding the app when you want to override the default behavior.

A class diagram defines the base of the app. The generated files can be found in
`backend/build/umlp2java` and
`frontend/build/app/umlpJava2ts`. The generated files have
very similar implementations. Generated classes include:

* ServerApplication: `CarrentalServerApplication`, used as a main class for
  running the server. It is also a common place for dummy data initialization.
* Manager: `CarrentalManager`, used to get builder classes and has internal data
  management purposes.
* Manager mock: `CarrentalMock`, used in testing as a substitute of the real
  `CarrentalManager`.

Having an empty class diagram is not really meaningful, but it plays a certain
role in the generated code as will be explained further.

!!! note
    The generator runs using the already mentioned commands `gradle build`, `gradle :backend:run` and `gradle :frontend:run`.

## Class

A class specifies a type for an instantiable entity. Rough structure:

```java
class Name {
  Type1 attrName1;
  Type2 attrName2;
}
```

It contains

- a single class declaration with a keyword `class`
- an optional list of attributes

The car rental example already contains a single class:

```java
class Customer {
  String name;
  String email;
  int age;
}
```

Specifying classes and running the generator will produce a bunch of code that is
used for entities management:

* Domain classes: `Customer`, represent the domain objects.
* Builder classes: `CustomerBuilder`, used to build (i.e., instantiate) domain objects.
* Data Access Object (DAO): `CustomerDAO`, used to get/set data from/to the
  database.
* (De)serializer: `CustomerDeSer`, used to (de)serialize the domain objects for
  the transfer between backend and frontend (and other services).
* Observer: `CustomerObserver`, used to observe and immediately react to changes
  in the domain objects.
* Commands: `Customer_create`, `Customer_delete`, etc., used to execute
  corresponding operations.
* Other: there are many classes which are used internally or used as a
  supplement for the described infrastructure that are not yet relevant for this
  project.

The most relevant classes are domain and builder classes. Those are used to
create and update domain objects. 
Try it yourself. For instance, using the `initEntities()` method in the `CarrentalServerApplication` class.

- Create an object using a builder:
  ```java
  Customer jim = CarrentalManager.customerBuilder() // returns CustomerBuilder
    .name("Jim Douglas")
    .email("jim.double@example.com")
    .age(34)
    .build() // return Optional<Customer>
    .get();
  ```
- Read object attributes using `get` methods:
  ```java
  jim.getName();
  ```
- Update object attributes using `set` methods:
  ```java
  jim.setEmail("jim.douglas@example.com");
  ```
- Delete objects using `delete` methods of manager:
  ```java
  CarrentalManager.deleteCustomer(jim.getGemId());
  ```

## Enum

An enum specifies a set of named constants.
```java
enum Name {
  CONSTANT_1,
  CONSTANT_2;
}
```
It contains

- a single enum declaration with a keyword `enum`
- an optional list of constants

Add the following enum to the class diagram of your car rental example:
```java
enum RentableStatus {
  AVAILABLE,
  RENTED,
  MAINTENANCE;
}
```
Unlike a class, an enum always has one instance, and its values are accessed
directly:
```java
RentableStatus.AVAILABLE
```

## Associations

Associations describe a "has a" relation between domain objects. Rough
structure:
```
association [cardinality1] ClassName1 (roleName1) <-> (roleName2) ClassName2 [cardinality2];
```
The association has left and right sides separated with an arrow:

- cardinality defines how many items can be in the association, typical values
  are `[*]` (0 or more), `[1]`, `[0..1]` (0 or 1), `[1..*]` (1 or more)
- class name
- role name which the associated objects are referenced with
- direction that defines which side of the association is the owner in the
  "has a" relation, typical values are `<-`, `->`, `<->` (goes both ways) 

Let's add another class to the car rental example to give an example of such relation:
```java
class RentalTransaction {
  LocalDateTime rentalDate;
  LocalDateTime returnDate;
  float totalCost;
}
```
A `RentalTransaction` is created when a customer rents an item, e.g., a car. In
other words, a customer *has* (performs) rental transactions if the customer
rents one or several cars.

Add to the car rental example:
```
association [*] RentalTransaction <-> Customer [1];
```
This association means that

- A rental transaction has exactly one associated customer
- A customer may have 0, 1 or several rental transactions

The above association can be described differently:
```
association [*] RentalTransaction <- Customer [1]; // a customer has rental transactions
association [*] RentalTransaction -> Customer [1]; // rental transaction has a customer
```
or
```
association [*] RentalTransaction (rentalTransaction) <-> (customer) Customer [1];
```
or
```java
class Customer {
  /* attributes */
  List<RentalTransaction> rentalTransaction;
}

class RentalTransaction {
  /* attributes */
  Customer customer;
}
```

Role names are automatically derived from class names if not specified. The
cardinalities also have default values, however, it is recommended to specify
them. Defining associations as class attributes is possible, but is not
recommended as it is easier to, e.g., turn a uni-directional association into a
bidirectional and vice versa. It is common, however, to define enum-typed
attribute, as an enum cannot (shouldn't) have associated objects.

## Attributes and types

Previously we showed several example classes with attributes:
```java
class Customer {
  String name;
  String email;
  int age;
}

class RentalTransaction {
  LocalDateTime rentalDate;
  LocalDateTime returnDate;
  float totalCost;
}
```
The class declares two attributes of type `String` and one of type `int`. A class diagram has access to
different types:

- `String`, Java primitive types (`boolean`, `int`, `void`, etc.), and their
  boxed counterparts (`Boolean`, `Integer`, etc.)
- Types defined in the class diagram (`Customer customer`).
- Imported Java types `import java.time.LocalDateTime;`

There are, however, limitations to what types can be used. The biggest one is
that the generic types are not supported, other than `java.util.List` and
`java.util.Optional`.
