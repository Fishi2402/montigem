<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superType", "extendingTypes", "typeToManagerMap")}
  ${cd4c.method("protected GemList<${superType}> doGet${superType}List()")}
  GemList<${superType}> list = super._doGet${superType}List();
  <#list extendingTypes as extendingType>
  list.addAll(${typeToManagerMap[extendingType]}.get${extendingType}List());
  </#list>

  return list;
