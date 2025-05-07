<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "desers")}
${cd4c.method("public static void init()")}

// @CBu This code ensures that we can DeSer types that are needed from an extended CD
// It is written in a way compatible to JSweet transpilation
HashMap<String, DeSer<?>> oldBaseDeSers = new HashMap<>();
if(getInstance() != null) {
  getInstance().getBaseDeSers().keySet().forEach(k -> {
    oldBaseDeSers.put(k, getInstance().getBaseDeSers().get(k));
  });
}

instance = new ${name}BaseClassObjectMapper();

oldBaseDeSers.keySet().forEach(oldKey -> {
  if(!getInstance().getBaseDeSers().containsKey(oldKey)) {
    getInstance().getBaseDeSers().put(oldKey, oldBaseDeSers.get(oldKey));
  }
});

<#list desers as deser>
BaseClassObjectMapper.addDeSers(${deser}.getInstance().getDeSerMap());
</#list>
