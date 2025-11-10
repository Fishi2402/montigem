<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tslib", "qnames")}
{
  "${tslib}": [
  <#list qnames as qname>
    "${qname}"<#sep>,
  </#list>
  ]
}