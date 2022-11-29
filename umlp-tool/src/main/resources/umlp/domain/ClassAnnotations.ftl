<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName", "hasSuperclass", "hasTop")}
<#if hasTop>
  @MappedSuperclass
<#else>
  @Entity
  @Table(name = "${name?uncap_first}")
</#if>
<#if hasSuperclass>
  @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
</#if>
@Tuplizer(impl = ${managerName}Tuplizer.class)