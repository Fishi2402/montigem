${signature("jsonObject", "attr")}
return LocalDate.parse( ${jsonObject}.getMember("${attr.getName()}").getAsJsonString().getValue());