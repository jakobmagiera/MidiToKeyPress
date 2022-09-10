package net.jakobmagiera.midi;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

class KeyPresser implements Runnable {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final int keyCode;
    private AtomicBoolean press = new AtomicBoolean(false);

    private Robot robot = null;

    public KeyPresser(int keyCode) {
        this.keyCode = keyCode;
    }

    public void start() {
        initRobot();
        press.set(true);
        executorService.submit(this);
    }

    private void initRobot() {
        if (robot == null) {
            try {
                robot = new Robot();
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        initRobot();
        press.set(false);
        robot.keyRelease(keyCode);
    }

    @Override
    public void run() {
        boolean exit = false;
        while (!exit) {
            if (press.get()) {
                robot.keyPress(keyCode);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                exit = true;
            }
        }
    }
}
