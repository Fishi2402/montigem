<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "desers")}
${cd4c.method("public static void init()")}

// @CBu This code ensures that we can DeSer types that are needed from an extended CD
Map<String, DeSer> oldBaseDeSers = null;

if(getInstance() != null) {
  oldBaseDeSers = getInstance().getBaseDeSers();
}

instance = new ${name}BaseClassObjectMapper();

if(oldBaseDeSers!=null) {
  oldBaseDeSers.forEach((oldKey,oldDeSer) -> {
    if(!getInstance().getBaseDeSers().containsKey(oldKey)) {
      getInstance().getBaseDeSers().put(oldKey, oldDeSer);
    }
  });
}

<#list desers as deser>
BaseClassObjectMapper.addDeSers(${deser}.getInstance().getDeSerMap());
</#list>
