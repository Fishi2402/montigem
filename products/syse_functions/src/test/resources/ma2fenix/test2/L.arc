/* (c) https://github.com/MontiCore/monticore */
import package1.*;
component L {
  port in int a, out int i, out boolean j, out boolean k, k2;

  M m1;

  a -> m1.b;
  m1.f -> i;
  m1.g -> j;
  m1.h -> k;
  m1.h -> k2;
}
