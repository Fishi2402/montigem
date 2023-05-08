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
  <#elseif genHelper.isComponentArgument(ast, namedArg.getGUIArgValue())>
    [${namedArg.getName()}] = "${viewComponents.getComponentName(ast)}${viewComponents.getTemplateName(namedArg.getGUIArgValue())}"
  <#elseif genHelper.isComponentListArgument(ast, namedArg.getGUIArgValue())>
    [${namedArg.getName()}] = "[
    <#list genHelper.getComponentListArguments(ast, namedArg.getGUIArgValue()) as arg>
    ${viewComponents.getComponentName(ast)}${viewComponents.getTemplateName(arg)},
    </#list>
    ]"
  <#else>
    [${namedArg.getName()}] = "get${ast.getName()}${componentName?cap_first}${namedArg.getName()?cap_first}()"
  </#if>
</#list>
>
</${component}>

<#list genHelper.getComponentArguments(ast) as namedArg>
  <#assign nestedComponent = genHelper.getNestedComponent(namedArg)>
<ng-template #${viewComponents.getComponentName(ast)}${viewComponents.getTemplateName(namedArg)}>
  ${tc.include("umlp.gui2html.Component", nestedComponent)}
</ng-template>
</#list>
