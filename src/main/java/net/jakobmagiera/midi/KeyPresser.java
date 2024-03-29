package net.jakobmagiera.midi;

import java.awt.*;
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
        robot.keyPress(keyCode);
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
        try {
            boolean exit = false;
            Thread.sleep(500);
            while (!exit) {
                if (press.get()) {
                    robot.keyPress(keyCode);
                } else {
                    exit = true;
                }
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
