${signature("jsonObject", "attr")}
return ${jsonObject}.getMember("${attr.getName()}").getAsJsonNumber().getNumberAsInteger();