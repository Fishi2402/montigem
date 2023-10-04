${tc.signature("name", "types")}
${cd4c.attribute("public ${name}Observer updateObserver;") }
 = new class extends Empty${name}Observer{
  constructor(private ref: any) {
    super();
  }

<#list types as type>
  notify${type}Added(o: ${type}) { this.ref.${type?uncap_first}Added(o) }
  notify${type}Deleted(gemId: number) { this.ref.${type?uncap_first}Deleted(gemId) }
  getScope() { return NotificationScope.LOCAL }
</#list>
}(this)
