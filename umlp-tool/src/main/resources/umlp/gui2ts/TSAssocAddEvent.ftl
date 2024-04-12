<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainName", "type", "name", "referencedAssoc")}
  return event => {
    <#if referencedAssoc.isMult()>
    ${referencedAssoc.getPrintedLinkSourceAccessor()}.add${referencedAssoc.getRoleName()?cap_first}2(${name});
    <#else>
    ${referencedAssoc.getPrintedLinkSourceAccessor()}.set{referencedAssoc.getRoleName()?cap_first}1(${name});
    </#if>
  };
