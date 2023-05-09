<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz", "htmlPath", "scssPaths")}
@Component({
  selector: '${clazz.getName()}',
  templateUrl: '${htmlPath}',
  styleUrls: ["<#list scssPaths as path>${path},</#list>"]
})
