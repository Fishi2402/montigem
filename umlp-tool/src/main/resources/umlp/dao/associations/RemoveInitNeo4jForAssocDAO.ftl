<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "startSide","endSide", "startType", "endType")}
${cd4c.method("public void ensureRemove${assocName?cap_first}Created()")}
if(remove${assocName?cap_first} == null) {
remove${assocName?cap_first} = "MATCH(start:${startType} {gemId:$p1}) <#t>
<#lt>-[startToEnd:${endSide}]-> (end:${endType} {gemId:$p2}), (start) <-[endToStart:${startSide}]-(end) DELETE startToEnd, endToStart";
}
