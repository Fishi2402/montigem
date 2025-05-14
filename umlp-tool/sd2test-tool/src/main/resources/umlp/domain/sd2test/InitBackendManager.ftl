<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("protected void initBackendManager()")}
  var instance = new ${cdName}ManagerImplMonitor();
  ${cdName}Manager.setInstance(instance);
