<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "commandPackage")}
${cd4c.method("public boolean test(String type)")}

return type.equals("${commandPackage}.${type}_create")
       || type.equals("${commandPackage}.${type}_delete");
