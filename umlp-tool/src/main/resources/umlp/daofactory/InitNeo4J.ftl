<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("daoFactoryName", "domainClasses", "assocNames", "allClasses")}
${cd4c.method("public static void initNeo4J()")}
${daoFactoryName} impl = new ${daoFactoryName}();

<#list allClasses as domainClass>
  umlp.backendrte.common.DatabaseAccessManager.executeStatement("CREATE INDEX range_gem_id_${domainClass.getName()} IF NOT EXISTS FOR (n:${domainClass.getName()}) ON (n.gemId)");
</#list>

<#list domainClasses as domainClass>
  impl.${domainClass.getName()?uncap_first}DAO = new ${domainClass.getName()}DAONeo4J();
</#list>
<#list assocNames as assocname>
  impl.assoc${assocname?cap_first}DAO = new Assoc${assocname?cap_first}DAONeo4J();
</#list>
${daoFactoryName}.instance = impl;

