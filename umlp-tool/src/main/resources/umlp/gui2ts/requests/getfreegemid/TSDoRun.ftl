<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("modelName")}
${cd4c.method("public Promise<SimpleResult<Long>> doRun()")}
return ${modelName}Manager.getFreeGemId().then(v => new SimpleResult<number>().SimpleResult1(this.getId(), v, "java.lang.Long"));
