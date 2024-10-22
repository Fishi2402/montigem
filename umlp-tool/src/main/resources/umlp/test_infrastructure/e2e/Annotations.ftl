<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "portConfigClassName")}

@SpringBootTest(classes = ${cdName}ServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(value = ${portConfigClassName}.class)
@jsweet.lang.Erased
