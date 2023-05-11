<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className")}
<#--${cd4c.method("public void ${className?uncap_first}Get (@ShellOption(value = \"--id\") long id)")}-->
${cd4c.method("public void ${className?uncap_first}Get (long id)")}
  Optional<${className}> ${className?uncap_first} = ${cdName}Manager.${className?uncap_first}Opt(id);
  if (${className?uncap_first}.isPresent()) {
    Log.info(
      "Received ${className?uncap_first} from server: " + new ${className}DeSer().serialize(${className?uncap_first}.get()),
      UMLP_SHELL_API);
  }
  else {
    Log.warn("Could not receive ${className?uncap_first} with id " + id + " from server");
  }
