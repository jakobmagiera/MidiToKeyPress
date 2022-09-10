package net.jakobmagiera.midi;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    public static final Map<String, Integer> KEY_MAPPING = new HashMap<>();
    static {
        KEY_MAPPING.put("CTRL", KeyEvent.VK_CONTROL);
        KEY_MAPPING.put("ALT", KeyEvent.VK_ALT);
        KEY_MAPPING.put("SHIFT", KeyEvent.VK_SHIFT);
        KEY_MAPPING.put("F1", KeyEvent.VK_F1);
        KEY_MAPPING.put("F2", KeyEvent.VK_F2);
        KEY_MAPPING.put("F3", KeyEvent.VK_F3);
        KEY_MAPPING.put("F4", KeyEvent.VK_F4);
        KEY_MAPPING.put("F5", KeyEvent.VK_F5);
        KEY_MAPPING.put("F6", KeyEvent.VK_F6);
        KEY_MAPPING.put("F7", KeyEvent.VK_F7);
        KEY_MAPPING.put("F8", KeyEvent.VK_F8);
        KEY_MAPPING.put("F9", KeyEvent.VK_F9);
        KEY_MAPPING.put("F10", KeyEvent.VK_F10);
        KEY_MAPPING.put("F11", KeyEvent.VK_F11);
        KEY_MAPPING.put("F12", KeyEvent.VK_F12);
    }
    private int keyCode = KeyEvent.VK_X;
    private String midiDeviceName = "swissonic";

    public int getKeyCode() {
        return keyCode;
    }

    public String getMidiDeviceName() {
        return midiDeviceName;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public void setMidiDeviceName(String midiDeviceName) {
        this.midiDeviceName = midiDeviceName;
    }
}
