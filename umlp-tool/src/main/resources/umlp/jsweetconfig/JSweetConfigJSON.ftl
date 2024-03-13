<#-- (c) https://github.com/MontiCore/monticore -->
${signature("domName")}
{
  "adapters": [
    "umlp.jsweet.extension.UMLPJSweetAdapter",
    "umlp.jsweet.extension.domainAdapters.${domName}ImportAdapter"
  ],
  "@Replace('this.password = this.password;')": {
    "include": ["**.User_setPassword.init()"]
  },
  "@Replace('//Do nothing);')": {
    "include": [
      "**.PipelineInformationExtractor.init()",
      "**.PipelineInformationExtractor.extractPipelineDataOf(..)"
    ]
  },
  "domainName": "${domName}"
}
