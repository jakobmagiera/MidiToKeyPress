package net.jakobmagiera.midi;

public class PedalToKeyPressConverter implements PedalPressListener{
    private final KeyPresser keyPresser = new KeyPresser();

    @Override
    public void pedalDown() {
        System.out.println("Pedal down");
        keyPresser.start();
    }

    @Override
    public void pedalUp() {
        System.out.println("Pedal up");
        keyPresser.stop();
    }
}
