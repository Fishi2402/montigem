${signature("jsonObject", "attr")}
<#-- TODO AGe: not sure about this one -->
return ${jsonObject}.getMember("${attr.getName()}").getAsJsonNumber().getNumberAsInteger();