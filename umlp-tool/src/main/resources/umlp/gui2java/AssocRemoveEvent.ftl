<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainName", "type", "name", "referencedAssoc")}
return event -> {
   // TODO: Param type is underspecified
  <#if referencedAssoc.isMult()>
     ${referencedAssoc.getPrintedLinkSourceAccessor()}.remove${referencedAssoc.getRoleName()?cap_first}(
       (${type?remove_beginning("List<")?remove_ending(">")}) /* TODO: Remove type casting and replace it with param types */
         ${name});
  <#else>
     ${referencedAssoc.getPrintedLinkSourceAccessor()}.set{referencedAssoc.getRoleName()?cap_first}Absent();
  </#if>
};

