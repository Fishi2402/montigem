<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "portConfigClassName")}

@SpringBootTest(classes = ${cdName}ServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(value = ${portConfigClassName}.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // restart backend for every method
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
