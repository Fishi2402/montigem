<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "sourceName", "targetName")}
${cd4c.constructor("public constructor(long old${sourceName?cap_first}GemId, long old${targetName?cap_first}GemId,
                                        long ${sourceName?uncap_first}GemId, long ${targetName?uncap_first}GemId,
                                        int ${sourceName}Index, int ${targetName}Index)")}
public constructor(old${sourceName?cap_first}GemId: number, old${targetName?cap_first}GemId: number,
                                        ${sourceName?uncap_first}GemId: number, ${targetName?uncap_first}GemId: number,
                                        ${sourceName}Index: number, ${targetName}Index: number) {
super("${packageName}.${classname}");
this.old${sourceName?cap_first}GemId = old${sourceName?cap_first}GemId;
this.old${targetName?cap_first}GemId = old${targetName?cap_first}GemId;
this.${sourceName}GemId = ${sourceName}GemId;
this.${targetName}GemId = ${targetName}GemId;
this.${sourceName}Index = ${sourceName}Index;
this.${targetName}Index = ${targetName}Index;
}
