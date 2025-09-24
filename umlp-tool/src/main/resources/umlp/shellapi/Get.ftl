<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "initializationTypeName")}
<#--${cd4c.method("public void ${initializationTypeName?uncap_first}Get (@ShellOption(__val = \"--id\") long id)")}-->
${cd4c.method("public void ${initializationTypeName?uncap_first}Get (long id)")}
  Optional<${initializationTypeName}> ${initializationTypeName?uncap_first} = ${cdName}Manager.${initializationTypeName?uncap_first}Opt(id);
  if (${initializationTypeName?uncap_first}.isPresent()) {
    Log.info(
      "Received ${initializationTypeName?uncap_first} from server: " + new ${initializationTypeName}DeSer().serialize(${initializationTypeName?uncap_first}.get()),
      UMLP_SHELL_API);
  }
  else {
    Log.warn("Could not receive ${initializationTypeName?uncap_first} with id " + id + " from server");
  }
