The Project tests loading data from the backend. 

Create (Build) Update (Adding / Deleting elements) 

Behavior of peristed and non persisted (build not finished) GemList

Attached (association) or General GemList (in Manager)

GUI Page 

| TestName | Create (Build) | Update (Adding) | Update (Delete) | Behavior of persisted | non persisted (build not finished) GemList | Attached (association) | General GemList (in Manager) | Same Level | Embedded
| TestBuilder::testUpdateWithBuilder                |   | + |   |   | + | + |   | - | - |
| TestBuilder::testUpdateWithNonBuilderAnimal       |   | + |   | + |   | + |   | - | - |
| TestUpdate::testUpdateOnOnePage                   |   | + |   | + |   |   | + | + |   |
| TestUpdate::testUpdateOnPersonsInBuilderPage      |   | + |   | + |   |   | + |   | Persons embedded |
| TestUpdate::testUpdateOnBuilderInPersonsPage      |   | + |   | + |   |   | + |   | Builder embedded |
| TestUpdate::testUpdateOnComposedPage              |   | + |   | + |   |   | + |   | Builder and Persons embedded |
| TestLoading::testLoadingAllPersonsAndNothingElse  |   |   |   | + |   |   | + | - | - |
| TestLoading::testLoadOnePersonAndAllButOneAnimals             |   |   |   | + |   | + |   | - | - |
| TestLoading::testUpdateAllAnimalsPageAfterNewAnimalWasCreated |   | + |   | + |   |   | + | - | - |
| TestLoading::testLoadAllComponentsInComposedPage              |   |   |   | + |   | + |   |   | List embedded |

Separate Test:
TestLoading::testLoadOnlyAnimals test lazy loading.
