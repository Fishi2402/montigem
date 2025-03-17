<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName","name", "concreteSubclassesInclSelf")}
${cd4c.method("protected GemList<${name}> _doGetFiltered${name}List(String filter)")}
  List<${name}> res = new ArrayList<>();
  <#list concreteSubclassesInclSelf as subclass>
    res.addAll(${cdName}DAOFactory.get${subclass.name?cap_first}DAO().selectFiltered${subclass.name?cap_first}List(filter));
  </#list>
  GemListBuilder<${name}> list = new ${name?cap_first}GemList();
  list.addAll(res);
  return new umlp.backendrte.common.ReadOnlyGemList<>(list.build().get());
