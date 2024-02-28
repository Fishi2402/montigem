<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
${tc.signature("args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign htmlHelper = glex.getGlobalVar("htmlHelper")>
<#assign isNgTemplate = htmlHelper.isNgTemplate(ast)>
<#assign tag = genHelper.pascal2KebabCase(ast.getName())>
<#assign extraArgs = genHelper.getIterateItems(ast)>

<#if isNgTemplate>
  <ng-template #${ast.getId().getName()}Tpl>
</#if>
<${tag} class="${ast.getId().getName()}"
  <#list ast.getGUINamedArgs().getGUINamedArgList() as namedArg>
    <#if genHelper.isEventArgument(namedArg)>
      (${namedArg.getName()}) = '${genHelper.gui2cdEventCallName(ast, namedArg.getName())}.bind(this)($event<#list extraArgs as arg>, ${arg}</#list>)'
    <#elseif genHelper.isViewElement(ast, namedArg) || genHelper.isViewElementsCollection(ast, namedArg)>
      [${namedArg.getName()}] = '${genHelper.gui2htmlArgValue(namedArg.getGUIArgValue())}'
    <#elseif genHelper.hasGetter(ast, namedArg)>
      [${namedArg.getName()}] = '${genHelper.gui2cdAttrGetterNameTS(ast.getId().getName(), namedArg)}(
        <#list args as arg>${arg}<#if !arg?is_last>,</#if></#list>)'
      <#if genHelper.hasSetter(ast, namedArg)>
        (${namedArg.getName()}Change) = '${genHelper.gui2cdAttrSetterName(ast.getId().getName(), namedArg.getName())}(
          $event<#list args as arg>,${arg}</#list>)'
      </#if>
    <#else>
      <#-- TODO AGe: iterator index -->
      <#-- [${namedArg.getName()}] = '${genHelper.gui2cdAttrGetterName(ast, namedArg.getName())}()' -->
      [${namedArg.getName()}] = '${genHelper.print(namedArg.getGUIArgValue())}(
        <#list args as arg>${arg}<#if !arg?is_last>,</#if></#list>)'
    </#if>
  </#list>
>
</${tag}>
<#if isNgTemplate>
  </ng-template>
</#if>
