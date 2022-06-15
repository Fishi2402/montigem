<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "builder")}
<#assign simpleName=name?remove_beginning("is")?remove_ending("Present")?uncap_first>
${cd4c.method("public ${builder} ${simpleName}(${type} ${simpleName})")}

this.${simpleName} = ${simpleName};
${name} = true;
return this;



