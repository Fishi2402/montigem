<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("public Promise<GemList<${name}>> _doGet${name}List()")}
let ids: number[] = [];
<#list concreteSubclassesInclSelf as subclass>
  ids = ids.concat(this.${subclass.name?uncap_first}GemList.getIDs());
</#list>
let list: ${name?cap_first}GemList = new ${name?cap_first}GemList(ids);
return Promise.resolve(list.build());
