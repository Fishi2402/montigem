<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "attributename", "typename", "packagename")}
${cd4c.constructor("public constructor(long gemId)")}
public constructor(gemId: number) {
  super("<#if packagename != "">${packagename}.</#if>${classname?cap_first}${typename}${attributename?uncap_first}");
  this.gemId = gemId;
}
