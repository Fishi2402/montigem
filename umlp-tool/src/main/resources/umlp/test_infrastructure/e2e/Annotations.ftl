<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "portConfigClassName")}

@SpringBootTest(classes = ${cdName}ServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "umlp.e2etest=true")
@Import(value = ${portConfigClassName}.class)
@jsweet.lang.Erased
