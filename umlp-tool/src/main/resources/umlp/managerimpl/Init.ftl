<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cDName", "managerName")}
${cd4c.method("public static void init()")}
  ${managerName}Impl impl = new ${managerName}Impl();
  impl.${cdName?uncap_first}Observers.add(new ${cdName}DbObserver());
  ${managerName}.setInstance(impl);