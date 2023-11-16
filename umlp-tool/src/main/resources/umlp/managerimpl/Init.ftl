<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdDefinitionName", "managerName")}
${cd4c.method("public static void init()")}
  ${cdDefinitionName}DAOFactory.init();
  ${managerName} impl = new ${managerName}();
  impl.${cdDefinitionName?uncap_first}Observers.add(new ${cdDefinitionName}DbObserver());
  ${managerName}.setInstance(impl);
