# MidiToKeyPress
A command line tool that converts a MIDI sustain pedal event to a key press event.

## Installation

Ensure you have [Java](https://java.com) installed.
Download the [MidiToKeyPress-1.0.jar](release/MidiToKeyPress-1.0.jar) file.

## Running

On the command line, enter ```java -jar MidiToKeyPress-1.0.jar [MIDIDeviceName] [KeyToPress]```

### Example

```java -jar MidiToKeyPress-1.0.jar swissonic u```

This will try to identify a MIDI device which contains the word "swissonic" and map pressing the sustain pedal
to pressing the letter 'u'.

## Special Keys
The following special keys are supported by passing their name on the command line (e. g. ```java -jar MidiToKeyPress-1.0.jar swissonic F10```).
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
