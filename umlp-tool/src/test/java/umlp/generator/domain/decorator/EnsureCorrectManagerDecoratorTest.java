package umlp.generator.domain.decorator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnsureCorrectManagerDecoratorTest {

  @Test
  void testGetAttributeName() {
    assertEquals("foo", EnsureCorrectManagerDecorator.getAttributeName("Foo"));
    assertEquals("fooBar", EnsureCorrectManagerDecorator.getAttributeName("FooBar"));
  }
}
