<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "isInterface")}
${cd4c.method("protected GemList<${name}> _doGet${name}List()")}
let ids: number[] = [];
let list: GemListBuilder<${name}> = new ${name?cap_first}GemList(ids);
return list.build();