package net.jakobmagiera.midi;

import javax.sound.midi.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class MidiReader {
    private final PedalPressListener pedalPressListener;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public MidiReader(PedalPressListener listener) {
        this.pedalPressListener = listener;
        Thread shutdownThread = new Thread(() -> {
            shutdown.set(true);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    public void connectToMidi(String midiDeviceName) {
        try {
            Optional<MidiDevice> midiDevice = findMidiDevice(midiDeviceName);
            do {
                if (midiDevice.isPresent()) {
                    if (!midiDevice.get().isOpen()) {
                        Transmitter inPortTrans = midiDevice.get().getTransmitter();
                        inPortTrans.setReceiver(createReceiver());
                        midiDevice.get().open();
                        System.out.println("Midi initialized.");
                    }
                } else {
                    System.err.println("Could not find MIDI device");
                }
                Thread.sleep(1000);
                midiDevice = findMidiDevice(midiDeviceName);
            } while(!shutdown.get());
            System.out.println("Shutting down.");
            midiDevice.ifPresent(MidiDevice::close);
        } catch (MidiUnavailableException e) {
            System.err.println("Midi is not available:" + e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Receiver createReceiver() {
        return new Receiver() {
            @Override
            public void send(MidiMessage message, long timeStamp) {
                if (message.getLength() < 3) {
                    return;
                }
                byte firstByte = message.getMessage()[0];
                byte thirdByte = message.getMessage()[2];
                if (firstByte == -80) {
                    if (thirdByte == 127) {
                        pedalPressListener.pedalDown();
                    }
                    if (thirdByte == 0) {
                        pedalPressListener.pedalUp();
                    }
                }
            }

            @Override
            public void close() {

            }
        };
    }

    private Optional<MidiDevice> findMidiDevice(String midiDeviceName) throws MidiUnavailableException {
        Optional<MidiDevice.Info> deviceInfo = Arrays.stream(MidiSystem.getMidiDeviceInfo()).filter(
                info -> info.getName().toLowerCase().contains(midiDeviceName.toLowerCase()))
                .filter(info -> {
                    try {
                        MidiSystem.getMidiDevice(info).getTransmitter();
                        return true;
                    } catch (MidiUnavailableException e) {
                        return false;
                    }
                }).findFirst();
        if (deviceInfo.isPresent()) {
            return Optional.of(MidiSystem.getMidiDevice(deviceInfo.get()));
        }
        return Optional.empty();
    }

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
        System.out.printf("MIDI to key mapping configuration:\n");
        System.out.printf("    Device: %s\n", config.getMidiDeviceName());
        System.out.printf("    Character: %s\n\n", KeyEvent.getKeyText(config.getKeyCode()).toLowerCase());
        return config;
    }

}
