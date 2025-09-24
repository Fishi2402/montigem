<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "command")}
${cd4c.method("protected Optional<${command}> fromJsonObject(any jsonObject)")}

let o: ${initializationTypeName} = new ${initializationTypeName}DeSer().deserialize(jsonObject["${initializationTypeName?uncap_first}"]);
if(o == null){
  return null;
}
return new ${command}(o);
