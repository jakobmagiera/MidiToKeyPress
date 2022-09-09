package net.jakobmagiera.midi;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

class KeyPresser implements Runnable {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private AtomicBoolean press = new AtomicBoolean(false);

    private Robot robot = null;

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
        robot.keyRelease(KeyEvent.VK_X);
    }

    @Override
    public void run() {
        boolean exit = false;
        while (!exit) {
            if (press.get()) {
                robot.keyPress(KeyEvent.VK_X);
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
