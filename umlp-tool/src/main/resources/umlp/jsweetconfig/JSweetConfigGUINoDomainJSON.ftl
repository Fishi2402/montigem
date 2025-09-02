<#-- (c) https://github.com/MontiCore/monticore -->
${signature("hwcPaths")}
<#-- @ftlvariable name="hwcPaths" type="java.util.List<java.lang.String>"-->
{
  "adapters": [
    "umlp.jsweet.extension.UMLPGUIRTEModuleAdapter"
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
  },
  "hwc": [
    <#list hwcPaths as hwc>"${hwc?json_string}"<#sep>,</#list>
  ]
}
