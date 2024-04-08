/* (c) https://github.com/MontiCore/monticore */

component C {
  port in int a, out int b;

    <<sync>> automaton{
      initial state Idle;
      state Second;

      Idle -> Idle [a < 2]/{
        b = 42;
      };

      Idle -> Second [a >= 2]/{
         b = 69;
      };
    }
}
