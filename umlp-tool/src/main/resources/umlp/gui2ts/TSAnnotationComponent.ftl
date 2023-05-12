<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz", "htmlPath", "scssPaths")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
@Component({
  selector: '${genHelper.getComponentNameFromCamelCaseName(clazz.getName())}',
  templateUrl: '${htmlPath}',
  styleUrls: [<#list scssPaths as path>'${path}',</#list>]
})
