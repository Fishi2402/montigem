/* (c) https://github.com/MontiCore/monticore */

component A {
  port sync in int a, sync out int b, c, d;
  B b1;
  a -> b1.a;
  b1.b -> b;
  b1.c -> c, d;
}
