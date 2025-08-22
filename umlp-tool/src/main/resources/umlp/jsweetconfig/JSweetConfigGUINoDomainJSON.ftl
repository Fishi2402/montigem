<#-- (c) https://github.com/MontiCore/monticore -->
{
  "adapters": [
    "umlp.jsweet.extension.UMLPJSweetGUIAdapter",
    "umlp.jsweet.extension.domainAdapters.DefaultImportAdapter"
  ],
  "@Erased": {
    "include": [
      "**.spliterator*()",
      "**.stream*()",
      "**.parallelStream*()",
      "**.toArray*(*)",
      "**.hashCode*()",
      "**.equals*?\\(*\\)"
    ]
  }
}
