import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import netP5.*; 
import oscP5.*; 
import themidibus.*; 
import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class MidiOSC extends PApplet {






ControlP5 cp5;
MidiBus midi;
DropdownList midiInDropdown, midiOutDropdown;
Textarea logArea;

OscP5 osc;
NetAddress loc;

public void setup() {
  
  MidiBus.list();
  
  osc = new OscP5(this, 8001);
  loc = new NetAddress("127.0.0.1", 8000);
  osc.plug(this, "oscToMidiNote", "/midi/note");
  osc.plug(this, "oscToMidiCC", "/midi/cc");
  
  midi = new MidiBus(this, 0, 1);
  cp5 = new ControlP5(this);
  
  cp5.addTextfield("inputPort")
    .setPosition(20, nextY())
    .setSize(200,20)
    .setValue("8000")
    .setAutoClear(false);
    
  cp5.addTextfield("outputPort")
    .setPosition(20, nextY())
    .setSize(200, 20)
    .setValue("8001")
    .setAutoClear(false);
    
  midiInDropdown = cp5.addDropdownList("midiIn")
    .setPosition(20, nextY())
    .setSize(200, 50);
    
    
  midiOutDropdown = cp5.addDropdownList("midiOut")
    .setPosition(20, nextY())
    .setSize(200, 50);
    
  String[] inputs = MidiBus.availableInputs();
  for (int i = 0; i < inputs.length; i++) {
    midiInDropdown.addItem(inputs[i], i);
  }
  midiInDropdown.setValue(0);
  midiInDropdown.setOpen(false);
  
  String[] outputs = MidiBus.availableOutputs();
  for (int i = 0; i < outputs.length; i++) {
    midiOutDropdown.addItem(outputs[i], i);
  }
  midiInDropdown.setValue(1);
  midiOutDropdown.setOpen(false);
  
  cp5.addButton("Update")
    .setValue(0)
    .setPosition(20, nextY())
    .setSize(200, 20);
    
  logArea = cp5.addTextarea("Logs")
    .setPosition(20, nextY())
    .setSize(200, 200);
  
}
int y = 20;
private int nextY() {
  int tempY = y;
  y += 50;
  return tempY;
}

public void draw() {
  background(0);
  fill(255);
}

public void Update() {
  if (midi != null) {
    midi.clearAll();
  }
  midi = new MidiBus(this, (int)midiInDropdown.getValue(), (int)midiOutDropdown.getValue());
  midi.sendNoteOn(0, 44, 127);
}

// OSC -> MIDI

public void oscToMidiNote(int note, int value) {
  log("Got OSC Note: " + note + " Velocity: " + value); 
  midi.sendNoteOn(0, note, value);
}

public void oscToMidiCC(int control, int value) {
  log("Got OSC CC: " + control + " Value: " + value);
   midi.sendControllerChange(0, control, value);
}

// MIDI -> OSC

public void noteOn(int channel, int pitch, int velocity) {
  log("Got MIDI Note On: " + pitch + " Velocity: " + velocity);
  OscMessage msg = new OscMessage("/midi/note");
  msg.add(pitch);
  msg.add(velocity);
  osc.send(msg, loc);
}

public void noteOff(int channel, int pitch, int velocity) {
  log("Got MIDI Note Off: " + pitch + " Velocity: " + velocity);
  OscMessage msg = new OscMessage("/midi/note");
  msg.add(pitch);
  msg.add(velocity);
  osc.send(msg, loc);
}

public void controllerChange(int channel, int number, int value) {
  log("Got MIDI CC: " + number + " Velocity: " + value);
  OscMessage msg = new OscMessage("/midi/note");
  msg.add(number);
  msg.add(value);
  osc.send(msg, loc);
}

private void log(String msg) {
  println("Debug: " + msg);
  logArea.setText(msg + "\n" + logArea.getText());
}
  public void settings() {  size(240, 500); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "MidiOSC" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
