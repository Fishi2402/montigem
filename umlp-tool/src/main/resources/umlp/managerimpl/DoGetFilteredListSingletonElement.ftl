<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected GemList<${name}> _doGetFiltered${name}List(String filter)")}
<#--This method is providing compatibility-->
List<Long> ids = new ArrayList<>();
ids.add(get${name}().getGemId());
GemListBuilder<${name}> list = new ${name?cap_first}GemList(ids);
return list.build().get();
