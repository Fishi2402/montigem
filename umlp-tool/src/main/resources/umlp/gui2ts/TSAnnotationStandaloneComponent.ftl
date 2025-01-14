<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("htmlPath", "scssPaths", "selector","importedComponents","hwcPath","generateAnnotation")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

import { CommonModule } from "@angular/common";

export const config = {
imports: [
  CommonModule,
<#list importedComponents as imp>
  <#assign simpleName = genHelper.getModuleName(imp)>
  ${simpleName},
</#list>
],
selector: '${selector}',
templateUrl: '${htmlPath}',
styles: [":host{ display: flex; flex-direction: column }"],
styleUrls: [<#list scssPaths as path>'${path}',</#list>]
}

<#if generateAnnotation>
@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
<#else>
@Directive()
</#if>
