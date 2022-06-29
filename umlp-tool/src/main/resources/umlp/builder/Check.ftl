<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "clazz")}
${cd4c.method("public boolean check${name?cap_first}Present()")}

if(!is${name?cap_first}Present){
  ${clazz}ErrorCollector.report${name?cap_first}Invalid(this);
}
return is${name?cap_first}Present;




