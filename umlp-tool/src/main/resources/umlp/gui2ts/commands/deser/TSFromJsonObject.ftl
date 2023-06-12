<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "command")}
${cd4c.method("protected Optional<${command}> fromJsonObject(any jsonObject)")}

let o: ${className} = new ${className}DeSer().deserialize(jsonObject["${className?uncap_first}"]);
if(o == null){
  return null;
}
return new ${command}(o);
