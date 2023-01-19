<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "manager", "assocList")}
${cd4c.constructor("public ${name}Builder(long gemId)")}

this.gemId = gemId;
<#list assocList as assoc>
<#assign name=assoc.getName()?remove_ending("Present")?cap_first>
    this._${name?uncap_first} = ${manager}.create${name}Builder(gemId, Collections.emptyList());
</#list>