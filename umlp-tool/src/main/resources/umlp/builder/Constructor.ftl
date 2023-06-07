<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "manager", "assocList")}
${cd4c.constructor("public ${name}Builder(long gemId)")}

this.gemId = gemId;
<#list assocList as assoc>
<#assign assocName=assoc.getSymbol().getOtherSide().getType().print()+"_"+assoc.getName()?cap_first>
    this._${assoc.getName()} = ${manager}.create${assocName}Builder(gemId, new ArrayList<>());
</#list>
