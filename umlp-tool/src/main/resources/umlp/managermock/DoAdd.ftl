<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName", "assocs")}
${cd4c.method("protected void _doAdd${name}(${name} o)")}

${name?uncap_first}s.put(o.getGemId(), o);
${name?uncap_first}GemList.add(o);
if (!o.containsObserver(the${name}CommandObserver)) {
  o.addObserver(the${name}CommandObserver);
}

<#list assocs as assoc>
  <#assign type = assoc.getSymbol().getType().print()>
  <#assign otherSide = assoc.getSymbol().getOtherSide().getAstNode().getName()?cap_first>
  /* TODO
  for (Long initialId : o._internal_get${assoc.getName()?cap_first}GemList().getIDs(0, o.size${assoc.getName()?cap_first}s())) {
    ${type} ${type?uncap_first} = ${managerName}Manager.get${type}Sure(initialId);
    ${type?uncap_first}._internal_add${otherSide}(${type?uncap_first}.size${otherSide}s(), Collections.singletonList(o.gemId), NotificationScope.MINIMAL);
  }
  */

</#list>

${managerName?uncap_first}Observers.forEach(obs -> obs.notify${name}Added(o.getGemId()));