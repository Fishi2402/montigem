${signature("jsonObject", "attr")}
return LocalTime.parse( ${jsonObject}.getMember("${attr.getName()}").getAsJsonString().getValue());
