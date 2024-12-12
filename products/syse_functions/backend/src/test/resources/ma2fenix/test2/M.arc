/* (c) https://github.com/MontiCore/monticore */
import package1.*;

component M {
  port
    in int b,
    out int f,
    out boolean g,
    out boolean h;

  N n1, n2;

  b -> n1.c;
  n1.d -> n2.c;
  n1.e -> h;
  n2.d -> f;
  n2.e -> g;
}
