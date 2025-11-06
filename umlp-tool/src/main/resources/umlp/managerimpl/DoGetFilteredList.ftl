<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName","name", "concreteSubclassesInclSelf")}
${cd4c.method("protected GemList<${name}> _doGetFiltered${name}List(String filter)")}
  List<Long> ids = new ArrayList<>();
  <#list concreteSubclassesInclSelf as subclass>
    ids.addAll(${cdName}DAOFactory.get${subclass.name?cap_first}DAO().selectFiltered${subclass.name?cap_first}List(filter));
  </#list>
  GemListBuilder<${name}> list = new ${name?cap_first}GemList(ids);
  return new umlp.backendrte.common.ReadOnlyGemList<>(list.build().get());
