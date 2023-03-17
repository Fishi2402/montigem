<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "isInterface")}
${cd4c.method("protected GemList<${name}> _doGet${name}List()")}
<#if isInterface>
List<Long> ids = new ArrayList<>();
<#else>
<#--List<Long> ids = new ArrayList<>(this.${name?uncap_first}DAO.selectAll${name?cap_first}Ids);-->
List<Long> ids = new ArrayList<>();
</#if>
GemListBuilder<${name}> list = new ${name?cap_first}GemList(ids);
return list.build().get();