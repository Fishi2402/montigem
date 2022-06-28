<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected String _inventString(int length)")}

StringBuilder builder = new StringBuilder();
for (int i = 0; i < length; i++) {
  builder.append(possibleCharacters[random.nextInt(possibleCharacters.length)]);
}
return builder.toString();



