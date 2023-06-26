${signature("jsonObject", "attr")}
return LocalDateTime.parse( ${jsonObject}.getMember("${attr.getName()}").getAsJsonString().getValue());
