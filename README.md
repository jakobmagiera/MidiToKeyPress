# MidiToKeyPress
A command line tool that converts a MIDI sustain pedal event to a key press event.

It can be used in computer games to trigger in game functions like 
running, jumping, or pushing the gas pedal. For example, in the game, you
could map the key 'R' to make your character run fast. You can run MidiToKeyPress 
to map the sustain pedal to 'R'. 

When you play the game now, pressing the sustain pedal will cause
your character to run just like pressing 'R' on the keyboard.

This tool is written in java, making it suitable for all common operating systems,
e.g. Windows, Linux, and MacOS.

## Installation

Ensure you have [Java](https://java.com) installed.
Download the [MidiToKeyPress-1.1.jar](https://github.com/jakobmagiera/MidiToKeyPress/releases) file.

## Running

On the command line, enter ```java -jar MidiToKeyPress-1.1.jar [MIDIDeviceName] [KeyToPress]```

### Example

```java -jar MidiToKeyPress-1.1.jar swissonic u```

This will try to identify a MIDI device which contains the word "swissonic" and map pressing the sustain pedal
to pressing the letter 'u'.

## Special Keys
The following special keys are supported by passing their name on the command line (e. g. ```java -jar MidiToKeyPress-1.1.jar swissonic F10```).
```
CTRL
ALT
SHIFT
F1
F2
F3
F4
F5
F6
F7
F8
F9
F10
F11
F12
```
