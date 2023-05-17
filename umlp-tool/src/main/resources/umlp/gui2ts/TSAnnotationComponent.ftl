<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("htmlPath", "scssPaths")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
@Component({
  selector: '${genHelper.getComponentNameFromCamelCaseName(ast.getName())}',
  templateUrl: '${htmlPath}',
  styleUrls: [<#list scssPaths as path>'${path}',</#list>]
})
