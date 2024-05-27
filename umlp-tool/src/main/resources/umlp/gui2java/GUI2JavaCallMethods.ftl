<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodList")}

<#list methodList as method>
this.${method.getName()}(<#list method.getCDParameterList() as arg>${arg.getName()}<#sep>, </#list>);
</#list>