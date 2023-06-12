<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
${cd4c.constructor("public constructor(long ownerGemID, java.util.List<Long> ids)")}
public constructor(ownerGemID: number, ids?: Array<number>) {
  super(ids || []);
  this.ownerGemID = ownerGemID;
}