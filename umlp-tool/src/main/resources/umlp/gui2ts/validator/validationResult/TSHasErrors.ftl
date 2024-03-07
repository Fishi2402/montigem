<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "attributes")}
${cd4c.method("public boolean hasErrors()")}
let hasError = false;
<#list attributes as attribute>
hasError = hasError || this.${attribute}ValidationMessagesHasError();
</#list>
return hasError;
