<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "manager", "assocList")}
${cd4c.constructor("public constructor(long gemId)")}
public constructor(gemId: number) {
  super();
  this.gemId = gemId;
  <#list assocList as assoc>
  <#assign name=assoc.getName()?remove_ending("Present")?cap_first>
    ${manager}.create${name}Builder(gemId, []).then(v => this._${name?uncap_first} = v);
  </#list>
}