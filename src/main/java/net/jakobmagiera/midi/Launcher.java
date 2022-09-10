package net.jakobmagiera.midi;

import java.awt.event.KeyEvent;

public class Launcher {
    public static void main(String[] args) {
        Configuration config = parseArgs(args);
        new MidiReader(new PedalToKeyPressConverter(config.getKeyCode())).connectToMidi(config.getMidiDeviceName());
    }

    private static Configuration parseArgs(String[] args) {
        Configuration config = new Configuration();
        if (args.length > 0) {
            config.setMidiDeviceName(args[0]);
            if (args.length > 1) {
                String character = args[1];
                if (Configuration.KEY_MAPPING.containsKey(character)) {
                    config.setKeyCode(Configuration.KEY_MAPPING.get(character));
                } else if (character.length() == 1 && Character.isLetterOrDigit(character.codePointAt(0))) {
                    config.setKeyCode(KeyEvent.getExtendedKeyCodeForChar(character.codePointAt(0)));
                } else {
                    System.err.printf("Could not identify character '%s', defaulting to %s\n", character, KeyEvent.getKeyText(config.getKeyCode()));
                }
            }
        }
        System.out.print("MIDI to key mapping configuration:\n");
        System.out.printf("    Device: %s\n", config.getMidiDeviceName());
        System.out.printf("    Key: %s\n\n", KeyEvent.getKeyText(config.getKeyCode()).toLowerCase());
        return config;
    }

}
