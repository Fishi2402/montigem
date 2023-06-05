<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "attributename", "type")}
${cd4c.constructor("public constructor(long gemId, ${type} ${attributename})")}
public constructor(gemId: number, ${attributename}: any) {
  super("${packageName}.${classname}");
  this.gemId = gemId;
  this.${attributename} = ${attributename};
}
