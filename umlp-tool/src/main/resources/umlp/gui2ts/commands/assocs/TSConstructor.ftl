<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "sourceName", "targetName")}
${cd4c.constructor("public constructor(long ${sourceName?uncap_first}GemId, long ${targetName?uncap_first}GemId,
                    int ${sourceName}Index, int ${targetName}Index)")}
public constructor(${sourceName?uncap_first}GemId: number, ${targetName?uncap_first}GemId: number,
                    ${sourceName}Index: number, ${targetName}Index: number) {
super("${packageName}.${classname}");
this.${sourceName?uncap_first}GemId = ${sourceName?uncap_first}GemId;
this.${targetName?uncap_first}GemId = ${targetName?uncap_first}GemId;
this.${sourceName}Index = ${sourceName}Index;
this.${targetName}Index = ${targetName}Index;
}