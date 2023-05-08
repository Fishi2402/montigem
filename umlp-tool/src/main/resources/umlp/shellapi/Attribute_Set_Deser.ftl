<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "fieldName", "fieldType")}
${cd4c.method("public void ${className?uncap_first}Set${fieldName?cap_first} (long id, String value)")}
  sendCommandToServerAndReceiveResult(new ${className?cap_first}_set${fieldName?cap_first}(id, new ${fieldType?split(".")?last}DeSer().deserialize(value).get()));
