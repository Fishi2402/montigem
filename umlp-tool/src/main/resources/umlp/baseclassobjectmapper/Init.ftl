<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "desers")}
${cd4c.method("public static void init()")}

instance = new ${name}BaseClassObjectMapper();

<#list desers as deser>
BaseClassObjectMapper.addDeSers(${deser}.getInstance().getDeSerMap());
</#list>
