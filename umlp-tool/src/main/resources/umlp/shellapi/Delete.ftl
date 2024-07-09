<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className")}
<#--${cd4c.method("public void ${className?uncap_first}Delete (@ShellOption(__val = \"--id\") long id)")}-->
${cd4c.method("public void ${className?uncap_first}Delete (long id)")}
  if (${cdName}Manager.delete${className}(id)) {
    Log.info("Deletion of ${className} with id " + id + " was successfull", UMLP_SHELL_API);
  }
  else {
    Log.warn("Deletion of ${className} with id " + id + " was not successfull");
  }
