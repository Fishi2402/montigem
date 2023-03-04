/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte;

import org.junit.jupiter.api.Test;
import umlp.backendrte.common.BaseClassObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

class BaseClassObjectMapperTest {

  @Test
  public void testBooleanSer(){
    assertEquals("true", BaseClassObjectMapper.serialize(true, "java.lang.Boolean").get());
    assertEquals("false", BaseClassObjectMapper.serialize(false, "java.lang.Boolean").get());
  }

  @Test
  public void testBooleanDeser(){
    assertTrue(BaseClassObjectMapper.<Boolean>deserialize("true", "java.lang.Boolean").get());
    assertFalse(BaseClassObjectMapper.<Boolean>deserialize("false", "java.lang.Boolean").get());
  }

  @Test
  public void testCharacterSer(){
    for(char i = 0; i < 1000; i++){
      assertEquals(Character.toString(i), BaseClassObjectMapper.serialize(i, "java.lang.Character").get());
    }
  }

  @Test
  public void testCharacterDeser(){
    for(char i = 0; i < 1000; i++){
      assertEquals(i, BaseClassObjectMapper.<Character>deserialize(Integer.toString(i), "java.lang.Character").get());
    }
  }
  
  @Test
  public void testShortSer(){
    for(short i = -1000; i < 1000; i++){
      assertEquals(Short.toString(i), BaseClassObjectMapper.serialize(i, "java.lang.Short").get());
    }
  }

  @Test
  public void testShortDeser(){
    for(short i = -1000; i < 1000; i++){
      assertEquals(i, BaseClassObjectMapper.<Short>deserialize(Short.toString(i), "java.lang.Short").get());
    }
  }
  
  @Test
  public void testIntegerSer(){
    for(int i = -1000; i < 1000; i++){
      assertEquals(Integer.toString(i), BaseClassObjectMapper.serialize(i, "java.lang.Integer").get());
    }
  }

  @Test
  public void testIntegerDeser(){
    for(int i = -1000; i < 1000; i++){
      assertEquals(i, BaseClassObjectMapper.<Integer>deserialize(Integer.toString(i), "java.lang.Integer").get());
    }
  }

  @Test
  public void testLongSer(){
    for(long i = -1000; i < 1000; i++){
      assertEquals(Long.toString(i), BaseClassObjectMapper.serialize(i, "java.lang.Long").get());
    }
  }

  @Test
  public void testLongDeser(){
    for(long i = -1000; i < 1000; i++){
      assertEquals(i, BaseClassObjectMapper.<Long>deserialize(Long.toString(i), "java.lang.Long").get());
    }
  }

  @Test
  public void testFloatSer(){
    for(float i = -1000; i < 1000; i += 0.1){
      assertEquals(Float.toString(i), BaseClassObjectMapper.serialize(i, "java.lang.Float").get());
    }
  }

  @Test
  public void testFloatDeser(){
    for(float i = -1000; i < 1000; i += 0.1){
      assertEquals(i, BaseClassObjectMapper.<Float>deserialize(Float.toString(i), "java.lang.Float").get());
    }
  }

  @Test
  public void testDoubleSer(){
    for(double i = -1000; i < 1000; i += 0.1){
      assertEquals(Double.toString(i), BaseClassObjectMapper.serialize(i, "java.lang.Double").get());
    }
  }

  @Test
  public void testDoubleDeser(){
    for(double i = -1000; i < 1000; i += 0.1){
      assertEquals(i, BaseClassObjectMapper.<Double>deserialize(Double.toString(i), "java.lang.Double").get());
    }
  }

  @Test
  public void testStringSer(){
    assertEquals("\"Test\"", BaseClassObjectMapper.serialize("Test", "java.lang.String").get());
    assertEquals("\"\"", BaseClassObjectMapper.serialize("", "java.lang.String").get());
  }

  @Test
  public void testStringDeser(){
     assertEquals("Test", BaseClassObjectMapper.<Double>deserialize("\"Test\"", "java.lang.String").get());
     assertEquals("", BaseClassObjectMapper.<Double>deserialize("\"\"", "java.lang.String").get());
  }
}
