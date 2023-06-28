<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("protected GemList<${name}> _doGet${name}List()")}
List<Long> ids = new ArrayList<>();
<#list concreteSubclassesInclSelf as subclass>
  ids.addAll(this.${subclass.name?uncap_first}DAO.selectAllIds${subclass.name?cap_first}());
</#list>
GemListBuilder<${name}> list = new ${name?cap_first}GemList(ids);
return list.build().get();
