/* (c) https://github.com/MontiCore/monticore */

component C {
  port sync in int a, sync out int b;

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
