<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method")}

this.${method.getName()}(<#list method.getCDParameterList() as arg>${arg.getName()}<#sep>, </#list>);