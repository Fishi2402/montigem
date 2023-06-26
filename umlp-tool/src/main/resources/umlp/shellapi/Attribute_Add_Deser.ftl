<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName", "fieldType", "isAssoc")}
${cd4c.method("public void ${className?uncap_first}Add${fieldName?cap_first} (long id, String value)")}
  sendCommandToServerAndReceiveResult(new ${className?cap_first}_add${fieldName?cap_first}(id, new ${fieldType?split(".")?last}DeSer().deserialize(value).get(), 0));
