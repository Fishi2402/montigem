<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdDefinitionName", "managerName", "superDelegateManagerNames")}
${cd4c.method("public static void initNeo4J()")}
  // Init super-delegate managers
  <#list superDelegateManagerNames as sdm>
    ${sdm}.initNeo4J();
  </#list>
  ${cdDefinitionName}DAOFactory.initNeo4J();
  ${managerName} impl = new ${managerName}();
  impl.${cdDefinitionName?uncap_first}Observers.add(new ${cdDefinitionName}DbObserver());
  ${managerName}.setInstance(impl);
