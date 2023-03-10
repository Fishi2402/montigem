<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign viewComponents = glex.getGlobalVar("viewComponents")>
<#assign component = genHelper.getComponentNameFromCamelCaseName(ast.getName())>
<#assign componentName = viewComponents.getComponentName(ast)>

<${component}
<#list ast.getGUINamedArgs().getGUINamedArgList() as namedArg>
  <#if genHelper.isEventArgument(ast, namedArg)>
    (${namedArg.getName()}) = "${ast.getName()?uncap_first}${componentName?cap_first}${namedArg.getName()?cap_first}.bind(this)()"
  <#else>
    [${namedArg.getName()}] = "get${ast.getName()}${componentName?cap_first}${namedArg.getName()?cap_first}()"
  </#if>
</#list>
>
</${component}>
