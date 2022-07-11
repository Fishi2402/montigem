<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "definition")}
${cd4c.method("public SimpleResult<LongList> doRun()")}

LongList res = new LongList();

for (${classname} ${classname?uncap_first} : ${definition}Manager.get${classname}List()) {
res.add(${classname?uncap_first}.getGemId());
}
return new SimpleResult<>(getId(), res, "umlp.command.LongList");