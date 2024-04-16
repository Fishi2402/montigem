<#assign genHelper = glex.getGlobalVar("genHelper")>

${tc.signature("name", "types", "params")}
${cd4c.attribute("public ${name}Observer updateObserver;") }
 = new class extends Empty${name}Observer{
  constructor(private ref: any) {
    super();
  }

<#list types as type>
  notify${type}Added(o: ${type}) {
  <#list params as param>
    <#if genHelper.getDomainListInnerType(param) == type>
    this.ref.${param.getName()}Added(o);
    </#if>
  </#list>
  }
  notify${type}Deleted(gemId: number) {
  <#list params as param>
    <#if genHelper.getDomainListInnerType(param) == type>
    this.ref.${param.getName()}Deleted(gemId);
    </#if>
  </#list>
  }
</#list>
getScope() { return NotificationScope.LOCAL }
}(this)
