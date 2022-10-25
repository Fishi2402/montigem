<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "definition")}
${cd4c.method("public SimpleResult<LongList> doRun()")}

LongList res = new LongList(${definition}Manager.get${classname}List().getIDs());
return new SimpleResult<>(getId(), res, "umlp.command.LongList");