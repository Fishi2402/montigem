<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "assocToManagerMap", "initialAttributes")}
${cd4c.constructor("public constructor(long gemId)")}
public constructor(gemId: number) {
  super();
  this.gemId = gemId;
  <#list assocToManagerMap as assoc, manager>
    <#assign assocName=assoc.getSymbol().getOtherSide().getType().print()+"_"+assoc.getName()?cap_first>
    this._${assoc.getName()} = ${manager}.create${assocName}Builder(gemId, []);
  </#list>
  <#list initialAttributes as i>
    this.${i[0]}(${i[1]});
  </#list>
}
