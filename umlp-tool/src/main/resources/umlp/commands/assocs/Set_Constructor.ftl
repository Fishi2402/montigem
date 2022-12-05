<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "sourceName", "targetName")}
${cd4c.constructor("public ${classname}(long old${sourceName}GemId, long old${targetName}GemId,
                                        long ${sourceName?uncap_first}GemId, long ${targetName?uncap_first}GemId,
                                        int indexIn${sourceName}List, int indexIn${targetName}List)")}

super("${packageName}.${classname}");
this.old${sourceName}GemId = old${sourceName}GemId;
this.old${targetName}GemId = old${targetName}GemId;
this.${sourceName?uncap_first}GemId = ${sourceName?uncap_first}GemId;
this.${targetName?uncap_first}GemId = ${targetName?uncap_first}GemId;
this.indexIn${sourceName}List = indexIn${sourceName}List;
this.indexIn${targetName}List = indexIn${targetName}List;
