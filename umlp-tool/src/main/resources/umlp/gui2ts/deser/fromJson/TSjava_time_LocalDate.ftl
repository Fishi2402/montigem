${signature("jsonObject", "attr")}
<#-- TODO AGe: this one is tricky, probably should use moment.js -->
return new Date(${jsonObject}.getMember("${attr.getName()}").getAsJsonString().getValue());