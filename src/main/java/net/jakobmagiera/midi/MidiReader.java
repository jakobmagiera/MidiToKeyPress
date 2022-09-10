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
}
