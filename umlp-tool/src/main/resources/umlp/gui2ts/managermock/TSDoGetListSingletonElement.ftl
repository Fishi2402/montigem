<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected GemList<${name}> _doGet${name}List()")}
let ids: number[] = [];

ids.addAll(${name?uncap_first}GemList.getIDs());
let list: ${name?cap_first}GemList = new ${name?cap_first}GemList(ids);
return list.build();
