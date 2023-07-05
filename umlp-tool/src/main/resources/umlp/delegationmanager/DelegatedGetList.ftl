<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superType", "extendingTypes", "extendingManagerName", "delegatedManager")}
  ${cd4c.method("protected GemList<${superType}> doGet${superType}List()")}
  GemList<${superType}> list = ${delegatedManager?uncap_first}._doGet${superType}List();
  <#list extendingTypes as extendingType>
  list.addAll(${extendingManagerName}.get${extendingType}List());
  </#list>

  return list;
