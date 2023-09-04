${tc.signature("name", "types")}
${cd4c.attribute("public ${name}Observer updateObserver;") }
 = new class extends Empty${name}Observer{
  constructor(private ref: any) {
    super();
  }

<#list types as type>
  notify${type}Added(o: ${type}) { this.ref.update${type}s() }
  notify${type}Deleted(gemId: number) { this.ref.update${type}s() }
</#list>
}(this)
