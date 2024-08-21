<#-- (c) https://github.com/MontiCore/monticore -->

<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign extraArgs = genHelper.findIterationItemSymbols(ast.getEnclosingScope())>
<#assign tag = genHelper.pascal2KebabCase(ast.getName())>

<${tag} class='${ast.getId().getName()}'
  <#list ast.getGUINamedArgs().getGUINamedArgList() as namedArg>
    <#if genHelper.isEventArgument(namedArg)>
      (${namedArg.getName()}) = '${genHelper.gui2cdEventCallName(ast, namedArg.getName())}.bind(this)($event<#list extraArgs as arg>, ${arg.getName()}</#list>)'
    <#elseif genHelper.isViewElement(ast, namedArg) || genHelper.isViewElementsCollection(ast, namedArg)>
      [${namedArg.getName()}] = '${genHelper.gui2htmlArgValue(namedArg.getGUIArgValue())}'
    <#elseif genHelper.hasGetter(ast, namedArg)>
      [${namedArg.getName()}] = '${genHelper.gui2cdAttrGetterNameTS(ast.getId().getName(), namedArg)}(
        <#list extraArgs as arg>${arg.getName()}<#if !arg?is_last>,</#if></#list>)'
      <#if genHelper.hasSetter(ast, namedArg)>
        (${namedArg.getName()}Change) = '${genHelper.gui2cdAttrSetterName(ast.getId().getName(), namedArg.getName())}(
          $event<#list extraArgs as arg>,${arg.getName()}</#list>)'
      </#if>
    <#else>
      <#-- TODO AGe: iterator index -->
      <#-- [${namedArg.getName()}] = '${genHelper.gui2cdAttrGetterName(ast, namedArg.getName())}()' -->
      [${namedArg.getName()}] = '${genHelper.print(namedArg.getGUIArgValue(), true)}(
        <#list extraArgs as arg>${arg.getName()}<#if !arg?is_last>,</#if></#list>)'
    </#if>
  </#list>
>
</${tag}>
