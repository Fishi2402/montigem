<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName","name", "concreteSubclassesInclSelf")}
${cd4c.method("protected GemList<${name}> _doGet${name}List()")}
List<Long> ids = new ArrayList<>();
<#list concreteSubclassesInclSelf as subclass>
  ids.addAll(${cdName}DAOFactory.get${subclass.name?cap_first}DAO().selectAllIds${subclass.name?cap_first}());
</#list>
GemListBuilder<${name}> list = new ${name?cap_first}GemList(ids);
return new umlp.backendrte.common.ReadOnlyGemList<>(list.build().get());
