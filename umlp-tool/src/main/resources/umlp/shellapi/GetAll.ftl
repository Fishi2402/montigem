<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "initializationTypeName")}
<#--${cd4c.method("public void ${initializationTypeName?uncap_first}Get (@ShellOption(__val = \"--id\") long id)")}-->
${cd4c.method("public void ${initializationTypeName?uncap_first}GetAll ()")}
  GemList<${initializationTypeName}> ${initializationTypeName?uncap_first}List = ${cdName}Manager.get${initializationTypeName}List();
  Log.info(
      "Received ${initializationTypeName?uncap_first}s from server: " + ${initializationTypeName?uncap_first}List.getIDs().toString(),
      UMLP_SHELL_API);
