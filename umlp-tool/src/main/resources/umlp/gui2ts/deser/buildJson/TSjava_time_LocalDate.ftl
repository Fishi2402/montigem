${signature("attr")}
<#-- TODO AGe: this one is tricky, probably should use moment.js -->
new Date(command.get${attr.getName()?cap_first}()).toISOString()