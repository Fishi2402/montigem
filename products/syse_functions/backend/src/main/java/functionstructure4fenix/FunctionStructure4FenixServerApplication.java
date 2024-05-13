/* (c) https://github.com/MontiCore/monticore */
package functionstructure4fenix;

import functionstructure4fenix.ma2fenix.MA2FenixTool;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@SpringBootApplication(
    // We have our own data management
    exclude={ DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages={"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "functionstructure4fenix.service"}
)
@jsweet.lang.Erased
public class FunctionStructure4FenixServerApplication extends FunctionStructure4FenixServerApplicationTOP {

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    this.createCustomFunctions();
    this.importFunctions();
  }

  private void importFunctions() {
    Path input1 = Paths.get("src/test/resources/ma2fenix/test1");
    Path input2 = Paths.get("src/test/resources/ma2fenix/test2");
    MA2FenixTool.main(new String[] {
        "-mp", input1.toAbsolutePath().toString(),
        "-main", "A"
    });
    MA2FenixTool.main(new String[] {
        "-mp", input2.toAbsolutePath().toString(),
        "-main", "L"
    });
  }

  private void createCustomFunctions() {
    FunctionStructure4FenixManager.functionBuilder()
        .name("EmptyFunction")
        .build();

    Optional<Category> doubleCategory = FunctionStructure4FenixManager.categoryBuilder()
        .continuous(Optional.of(false))
        .kind(FunctionStructure4FenixManager.dataBuilder().build().get())
        .type(FunctionStructure4FenixManager.typeBuilder().mctype("Double").build().get())
        .build();

    Optional<Function> subAdd = FunctionStructure4FenixManager.functionBuilder()
        .name("Add")
        .inAdd(
            FunctionStructure4FenixManager.portBuilder()
                .name("summand1")
                .channel(
                    FunctionStructure4FenixManager.channelBuilder()
                        .category(doubleCategory.get())
                        .functionStream(
                            FunctionStructure4FenixManager.functionStreamBuilder()
                                .category(doubleCategory.get())
                                .valueAdd(
                                    FunctionStructure4FenixManager.doubleValueBuilder()
                                        .content(2.4)
                                        .build().get()
                                )
                                .valueAdd(
                                    FunctionStructure4FenixManager.doubleValueBuilder()
                                        .content(9.1)
                                        .build().get()
                                )
                                .build().get()
                        )
                        .build().get()
                ).build().get()
        ).inAdd(
            FunctionStructure4FenixManager.portBuilder()
                .name("summand2")
                .channel(
                    FunctionStructure4FenixManager.channelBuilder()
                        .category(doubleCategory.get())
                        .functionStream(
                            FunctionStructure4FenixManager.functionStreamBuilder()
                                .category(doubleCategory.get())
                                .valueAdd(
                                    FunctionStructure4FenixManager.doubleValueBuilder()
                                        .content(-5.1)
                                        .build().get()
                                )
                                .valueAdd(
                                    FunctionStructure4FenixManager.doubleValueBuilder()
                                        .content(0.3)
                                        .build().get()
                                )
                                .build().get()
                        )
                        .build().get()
                ).build().get()
        ).outAdd(
            FunctionStructure4FenixManager.portBuilder()
                .name("sum")
                .channel(
                    FunctionStructure4FenixManager.channelBuilder()
                        .category(doubleCategory.get())
                        .functionStream(
                            FunctionStructure4FenixManager.functionStreamBuilder()
                                .category(doubleCategory.get())
                                .valueAdd(
                                    FunctionStructure4FenixManager.doubleValueBuilder()
                                        .content(-2.7)
                                        .build().get()
                                )
                                .valueAdd(
                                    FunctionStructure4FenixManager.doubleValueBuilder()
                                        .content(9.4)
                                        .build().get()
                                )
                                .build().get()
                        )
                        .build().get()
                ).build().get()
        ).build();

    FunctionStructure4FenixManager.functionBuilder()
        .name("AddWrapper")
        .inAdd(
            FunctionStructure4FenixManager.portBuilder()
                .name("summand1")
                .channel(
                    subAdd.get().getInChannel("summand1").get()
                ).build().get()
        ).inAdd(
            FunctionStructure4FenixManager.portBuilder()
                .name("summand2")
                .channel(
                    subAdd.get().getInChannel("summand2").get()
                ).build().get()
        ).outAdd(
            FunctionStructure4FenixManager.portBuilder()
                .name("sum")
                .channel(
                    subAdd.get().getOutChannel("sum").get()
                ).build().get()
        )
        .subFunctionAdd(subAdd.get())
        .build();
  }
}
