<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "startSide","endSide", "startType", "endType")}
${cd4c.method("public void ensureRemove${assocName?cap_first}Created()")}
if(remove${assocName?cap_first} == null) {
remove${assocName?cap_first} = DatabaseAccessManager.prepareStatement("MATCH(start:${startType} {gemId:?}) <#t>
<#lt>-[startToEnd:${endSide}]-> (end:${endType} {gemId:?}), (start) <-[endToStart:${startSide}]-(end) DELETE startToEnd, endToStart");
}
