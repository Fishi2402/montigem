statechart AmpelSC {
  initial state Rot;
  state Gelb;
  state Gruen;

  Rot -> Gruen timeout();
  Gruen -> Gelb timeout();
  Gelb -> Rot timeout();
}
