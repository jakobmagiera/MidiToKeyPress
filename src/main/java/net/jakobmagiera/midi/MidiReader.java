package net.jakobmagiera.midi;

import javax.sound.midi.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class MidiReader {
    private final PedalPressListener pedalPressListener;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final Thread shutdownThread = new Thread(() -> {
        shutdown.set(true);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }
    });

    public MidiReader(PedalPressListener listener) {
        this.pedalPressListener = listener;
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    public void connectToMidi() {
        try {
            Optional<MidiDevice> swissonicDevice = getSwissonicDevice();
            do {
                if (swissonicDevice.isPresent()) {
                    if (!swissonicDevice.get().isOpen()) {
                        Transmitter inPortTrans = swissonicDevice.get().getTransmitter();
                        inPortTrans.setReceiver(createReceiver());
                        swissonicDevice.get().open();
                        System.out.println("Midi initialized.");
                    }
                    Thread.sleep(1000);
                } else {
                    System.err.println("Could not find MIDI device");
                    Thread.sleep(1000);
                }
                swissonicDevice = getSwissonicDevice();
            } while(!shutdown.get());
            System.out.println("Shutting down.");
            if (swissonicDevice.isPresent()) {
                swissonicDevice.get().close();
            }
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

    private Optional<MidiDevice> getSwissonicDevice() throws MidiUnavailableException {
        Optional<MidiDevice.Info> swissonicInfo = Arrays.stream(MidiSystem.getMidiDeviceInfo()).filter(
                info -> info.getName().toLowerCase().contains("swiss"))
                .filter(info -> {
                    try {
                        MidiSystem.getMidiDevice(info).getTransmitter();
                        return true;
                    } catch (MidiUnavailableException e) {
                        return false;
                    }
                }).findFirst();
        if (swissonicInfo.isPresent()) {
            return Optional.of(MidiSystem.getMidiDevice(swissonicInfo.get()));
        }
        return Optional.empty();
    }

    public static void main(String[] args) {
        new MidiReader(new PedalToKeyPressConverter()).connectToMidi();
    }

}
