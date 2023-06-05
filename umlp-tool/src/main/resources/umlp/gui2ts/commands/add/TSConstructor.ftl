<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "attrName", "attrType")}
${cd4c.constructor("public constructor(long gemId,
                                        ${attrType} ${attrName},
                                        int indexInList)")}
public constructor(gemId: number, ${attrName}: any, indexInList: number) {
  super("${packageName}.${classname}");
  this.gemId = gemId;
  this.${attrName} = ${attrName};
  this.indexInList = indexInList;
}
