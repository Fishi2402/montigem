${signature("typeName", "index", "attr")}
insert${typeName}.set${attr.printType()?cap_first}(${index}, o.get${attr.getName()?cap_first}());