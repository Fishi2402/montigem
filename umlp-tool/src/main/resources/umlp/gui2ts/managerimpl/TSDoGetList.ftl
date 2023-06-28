<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("public Promise<GemList<${name}>> _doGet${name}List()")}
let ids: number[] = [];
let list: GemListBuilder<${name}> = new ${name?cap_first}GemList(ids);
return Promise.resolve(list.build());