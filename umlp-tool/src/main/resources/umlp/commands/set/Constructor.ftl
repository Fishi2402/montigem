<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packageName", "attributename", "type")}
${cd4c.constructor("public ${classname}(long gemId, ${type} ${attributename})")}

super(
"${packageName}.${classname}"
);
this.gemId = gemId;
this.${attributename} = ${attributename};
