# Simple MIDI <-> OSC Bridge

Listens to OSC for messages of the form

- /midi/note int:note int:velocity
- /midi/cc   int:number int:value

And converts them to MIDI notes or CC signals.  It also listens for MIDI, and converts incoming midi signals to the respective OSC signals.
