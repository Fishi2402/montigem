<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
${tc.signature("args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign isTpl = genHelper.isNgTemplate(ast.getId().getName())>
<#assign tag = genHelper.getComponentNameFromCamelCaseName(ast.getName())>
<#assign synchronizedSuffix = genHelper.getIsSynchronizedSuffix>

<#if isTpl>
  <ng-template #${genHelper.getNgTemplateActualName(ast.getId().getName())}>
</#if>
<${tag} class="${genHelper.getComponentActualName(ast.getId().getName())}"
  <#list ast.getGUINamedArgs().getGUINamedArgList() as namedArg>
    <#if genHelper.isEventArgument(ast, namedArg)>
      (${namedArg.getName()}) = '${genHelper.gui2cdEventCallName(ast, namedArg.getName())}.bind(this)($event<#list args as arg>, ${arg}</#list>)'
    <#elseif genHelper.isViewElement(ast, namedArg) || genHelper.isViewElementsCollection(ast, namedArg)>
      [${namedArg.getName()}] = '${genHelper.gui2htmlArgValue(namedArg.getGUIArgValue())}'
    <#elseif genHelper.hasGetter(ast, namedArg)>
      [${namedArg.getName()}] = '${genHelper.gui2cdAttrGetterNameTS(ast.getId().getName(), namedArg.getName())}(
        <#list args as arg>${arg}<#if !arg?is_last>,</#if></#list>)'
      <#if genHelper.hasSetter(ast, namedArg)>
        (${namedArg.getName()}Change) = '${genHelper.gui2cdAttrSetterName(ast.getId().getName(), namedArg.getName())}(
          $event<#list args as arg>,${arg}</#list>)'
      </#if>
    <#else>
      <#-- TODO AGe: iterator index -->
      <#-- [${namedArg.getName()}] = '${genHelper.gui2cdAttrGetterName(ast, namedArg.getName())}()' -->
      [${namedArg.getName()}] = '${genHelper.print(namedArg.getGUIArgValue())}${synchronizedSuffix}(
        <#list args as arg>${arg}<#if !arg?is_last>,</#if></#list>)'
    </#if>
  </#list>
>
</${tag}>
<#if isTpl>
  </ng-template>
</#if>
