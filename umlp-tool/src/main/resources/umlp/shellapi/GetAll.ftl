<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className")}
<#--${cd4c.method("public void ${className?uncap_first}Get (@ShellOption(__val = \"--id\") long id)")}-->
${cd4c.method("public void ${className?uncap_first}GetAll ()")}
  GemList<${className}> ${className?uncap_first}List = ${cdName}Manager.get${className}List();
  Log.info(
      "Received ${className?uncap_first}s from server: " + ${className?uncap_first}List.getIDs().toString(),
      UMLP_SHELL_API);
