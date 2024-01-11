<!-- (c) https://github.com/MontiCore/monticore -->
# 

Derived classes are now (sort of) kept in sync between frontend and backend.
The backend generates a `${name}Impl` class.
The TOP mechanism shall be used to implement the methods within this class.


```
/ class DerivedInfo {
  int someNumber;
  String someString;
  MessageOfTheDay randomMSG;
}
/ association DerivedInfo -> (manyStrings) java.lang.String [*];
/ association [1] DerivedInfo -> (monthlyMeetings) Meeting [*];
/ association [1] DerivedInfo -> (importantMeeting) Meeting [1];
```

The manager exposes a `DerivedInfo getDerivedInfo()` method,
 of which the backend manager will return a new instance of the `DerivedInfoImpl` implementation class.
A client manager will return a new instance of the `DerivedInfoClientImpl`
  client class instead.

For every attribute or association, 
 a command is generated to fetch the value.

The client implementation class then uses these commands 
 to retrieve the values.

The java client implementation fetches the values on demand,
while the TypeScript implementation performs the fetching after the initialization.
(Due to the missing `async` keyword, we are unable to await the return of the command.)
