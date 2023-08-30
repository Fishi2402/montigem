<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "leftType", "rightType", "leftName", "rightName", "navigableLeft", "navigableRight")}
${cd4c.method("public void ensureRemove${assocName?cap_first}Created()")}
if(remove${assocName?cap_first} == null) {
remove${assocName?cap_first} = DatabaseAccessManager.prepareStatement("MATCH(l:${leftType} {gemId:=?}) <#t>
<#lt>-[e:${assocName}]-> (m:${rightType} {gemId:?}) DELETE e");
}
