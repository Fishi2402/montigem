<!-- (c) https://github.com/MontiCore/monticore -->

In MontiGem/UMLP/Fenix, some stereotypes and tags can be used interchangeably.
The [TagRelated](../umlp-tool/src/main/java/umlp/TagRelated.java) class contains a visitor that turns stereotypes into tags,
 which then are used by the GuiGenerator.

* `<<singleton>> class` -> Marks a class as a singleton
* `<<command>> type method()` -> Marks a method as a command
* modifier `derived` -> Marks the element as derived

Tags are used instead of stereotypes,
 as they provide additional information about symbols, enabling information exchange along the symbol table.

These tag-information are retrieved using the [TagLookUp](../umlp-tool/src/main/java/umlp/generator/util/TagLookUp.java) class.
