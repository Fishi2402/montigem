<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("daoFactoryName", "domainClasses", "assocNames")}
${cd4c.method("public static void initNeo4J()")}
${daoFactoryName} impl = new ${daoFactoryName}();
<#list domainClasses as domainClass>
  impl.${domainClass.getName()?uncap_first}DAO = new ${domainClass.getName()}DAONeo4J();
</#list>
<#list assocNames as assocname>
  impl.assoc${assocname?cap_first}DAO = new Assoc${assocname?cap_first}DAONeo4J();
</#list>
${daoFactoryName}.instance = impl;

