<#-- (c) https://github.com/MontiCore/monticore -->
${signature("domName")}
{
  "adapters": [
    "umlp.jsweet.extension.UMLPJSweetDomainAdapter",
    "umlp.jsweet.extension.domainAdapters.${domName}ImportAdapter"
  ],
  "domainName": "${domName}",
  "@Erased": {
    "include": [
      "**.spliterator*()",
      "**.stream*()",
      "**.parallelStream*()",
      "**.toArray*(*)",
      "**.hashCode*()",
      "**.equals*(*)"
    ]
  }
}
