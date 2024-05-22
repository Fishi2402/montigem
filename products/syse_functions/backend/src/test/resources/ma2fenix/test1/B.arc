/* (c) https://github.com/MontiCore/monticore */

component B {
  port in int a, out int b, c;
  C c1;
  a -> c1.a;
  c1.b -> b,c;
}
