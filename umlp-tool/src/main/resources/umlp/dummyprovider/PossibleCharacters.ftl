<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
= Streams.concat(
  IntStream.range('a', 'z'),
  IntStream.range('A', 'Z'),
  IntStream.range('0', '9'),
  IntStream.of(' ')
)
.mapToObj(i -> (char) i)
.collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString))
.toCharArray()



