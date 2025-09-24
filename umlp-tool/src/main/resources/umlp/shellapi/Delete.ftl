<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "initializationTypeName")}
<#--${cd4c.method("public void ${initializationTypeName?uncap_first}Delete (@ShellOption(__val = \"--id\") long id)")}-->
${cd4c.method("public void ${initializationTypeName?uncap_first}Delete (long id)")}
  if (${cdName}Manager.delete${initializationTypeName}(id)) {
    Log.info("Deletion of ${initializationTypeName} with id " + id + " was successfull", UMLP_SHELL_API);
  }
  else {
    Log.warn("Deletion of ${initializationTypeName} with id " + id + " was not successfull");
  }
