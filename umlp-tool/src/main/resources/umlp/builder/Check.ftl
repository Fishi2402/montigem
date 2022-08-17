<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "clazz")}
${cd4c.method("public boolean check${name?cap_first}Present()")}

if(!${name}Present){
  ${clazz}ErrorCollector.report${name?cap_first}Invalid(this);
}
return ${name}Present;




