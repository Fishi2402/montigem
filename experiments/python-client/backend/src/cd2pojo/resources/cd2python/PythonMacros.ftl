<#--insert fake indentation lexems-->
<#--which are then turned into real indentation using a post-processing step-->
<#--Use like   <@indent> indented text here </@indent>-->

<#macro indent>
  [[[indent]]]
  <#nested>
  [[[deindent]]]
</#macro>
