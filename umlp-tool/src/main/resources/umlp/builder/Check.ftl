<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "clazz")}
${cd4c.method("public boolean check${name?cap_first}()")}

if(!${name}){
  ${clazz}ErrorCollector.report${name?cap_first?remove_ending("Present")}Invalid(this);
}
return ${name};




