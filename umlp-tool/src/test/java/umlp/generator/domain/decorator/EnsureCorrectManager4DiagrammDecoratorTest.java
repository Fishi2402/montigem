package umlp.generator.domain.decorator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnsureCorrectManager4DiagrammDecoratorTest {

  @Test
  void testGetAttributeName() {
    assertEquals("foo", EnsureCorrectManager4DiagrammDecorator.getAttributeName("Foo"));
    assertEquals("fooBar", EnsureCorrectManager4DiagrammDecorator.getAttributeName("FooBar"));
  }
}
