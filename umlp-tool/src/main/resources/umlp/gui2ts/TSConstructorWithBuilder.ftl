<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("diagrammName", "name", "attributes", "guiDSLPrinter", "paramDefaultValues")}
<#assign router="router">
public constructor(${router}: Router) {
  super();
  super.${router} = ${router};

  <#list paramDefaultValues as pName, pDefault>
    <#if pDefault.isPresent()>
  this.${pName} = ${pDefault.get()};
    </#if>
  </#list>
}
