<#-- (c) https://github.com/MontiCore/monticore -->
${signature("domName")}
{
  "adapters": [
    "umlp.jsweet.extension.GUIAdapter",
    "umlp.jsweet.extension.umlp.${domName}ImportAdapter"
  ],
  "domainName": "${domName}",
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
