<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdDefinitionName", "managerName", "superDelegateManagerNames")}
${cd4c.method("public static void init()")}
  // Init super-delegate managers
  <#list superDelegateManagerNames as sdm>
    ${sdm}.init();
  </#list>
  // Init itself
  ${cdDefinitionName}DAOFactory.init();
  ${managerName} impl = new ${managerName}();
  impl.${cdDefinitionName?uncap_first}Observers.add(new ${cdDefinitionName}DbObserver());
  ${managerName}.setInstance(impl);
