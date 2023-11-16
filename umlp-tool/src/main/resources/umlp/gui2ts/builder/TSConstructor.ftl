<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "assocToManagerMap", "initialAttributes")}
${cd4c.constructor("public constructor(long gemId)")}
public constructor(gemId: number) {
  super();
  this.gemId = gemId;
  <#list assocToManagerMap as assoc, manager>
    <#assign name=assoc.getName()?remove_ending("Present")?cap_first>
    this._${name?uncap_first} = ${manager}.create${name}Builder(gemId, []);
  </#list>
  <#list initialAttributes as i>
    this.${i[0]}(${i[1]});
  </#list>
}
