<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "clazz")}
<#assign simpleName=name?remove_beginning("is")?remove_ending("Present")>
${cd4c.method("public boolean check${simpleName}Present()")}

if(!${name}){
  ${clazz}ErrorCollector.report${simpleName}Invalid(this);
}
return ${name};




