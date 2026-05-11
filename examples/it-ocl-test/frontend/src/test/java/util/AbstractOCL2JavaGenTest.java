package util;

import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import mydomain.test_infrastructure.MydomainSQLDBTestSetup;
import org.junit.jupiter.api.BeforeEach;
import umlp.backendrte.common.GemIdentifiable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class AbstractOCL2JavaGenTest<T extends GemIdentifiable> extends MydomainSQLDBTestSetup implements IOCL2XTest<T> {
  protected Map<String, Predicate<T>> constraints = new HashMap<>();

  public AbstractOCL2JavaGenTest() {
    // No clients needed at the moment
    super(0);
  }

  // TODO: replace
  @Override
  public List<Long> translateAndExecQuery(String constraint) throws IOException, SQLException {
    Predicate<T> f = constraints.getOrDefault(constraint, person -> {
      assumeTrue(false, "Can not find generated java code for constraint\n" + constraint);
      return false;
    });
    f = f.negate();
    return getContextObjects().stream().filter(f).map(GemIdentifiable::getGemId).collect(Collectors.toList());
  }

  @Override
  public List<Long> _translateAndExecQuery(ASTOCLCompilationUnit ocl) {
    throw new IllegalStateException("Not used");
  }

  protected abstract List<T> getContextObjects();

  @BeforeEach
  protected void initConstraints(){
    _initConstraints();
  }

  protected void _initConstraints() {
    constraints.putAll(getConstraintToChecker());
  }

  public List<Long> translateAndExecQueryFromPath(String path) throws IOException, SQLException {
    return translateAndExecQuery(path);
  }
}
