package org.example;

import java.io.IOException;

public class StreamViewer {

    // Starts a viewer process (ffplay) that connects directly to tcp://host:port.
    // Returns a handle that can be used to stop the viewer.
    public static ViewerHandle startViewer(String host, int port, String windowTitle) throws Exception {
        // Verify ffplay exists and is callable
        try {
            ProcessBuilder check = new ProcessBuilder("ffplay", "-version");
            check.redirectErrorStream(true);
            Process cp = check.start();
            boolean finished = cp.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished) cp.destroyForcibly();
            if (cp.exitValue() != 0) {
                throw new IllegalStateException("ffplay returned non-zero exit code; ensure ffplay (FFmpeg) is installed and in PATH");
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("ffplay no encontrado en PATH. Instala FFmpeg y asegúrate de que ffplay esté disponible.", ioe);
        }

        String url = "tcp://" + host + ":" + port;
        ProcessBuilder pb = new ProcessBuilder(
            "ffplay",
            url,
            "-window_title", windowTitle,
            "-autoexit"
        );
        Process proc;
        try {
            proc = pb.start();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo iniciar ffplay: " + e.getMessage(), e);
        }

        Thread waiter = new Thread(() -> {
            try {
                proc.waitFor();
            } catch (InterruptedException ignored) {}
            System.out.println("ffplay process ended for " + windowTitle + ", exit=" + proc.exitValue());
        }, "StreamViewerWait-" + windowTitle);
        waiter.setDaemon(true);
        waiter.start();

        System.out.println("ffplay iniciado, intentando conexión directa a " + url + " (windowTitle=" + windowTitle + ")");
        return new ViewerHandle(proc, waiter);
    }

    // Start a local viewer using scrcpy for the given adb device id.
    public static ViewerHandle startViewerForDevice(String deviceId, String windowTitle) throws Exception {
        // Check scrcpy is available
        try {
            ProcessBuilder check = new ProcessBuilder("scrcpy", "--version");
            check.redirectErrorStream(true);
            Process cp = check.start();
            boolean finished = cp.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished) cp.destroyForcibly();
            if (cp.exitValue() != 0) {
                throw new IllegalStateException("scrcpy returned non-zero exit code; ensure scrcpy is installed and in PATH");
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("scrcpy no encontrado en PATH. Instala scrcpy.", ioe);
        }

        ProcessBuilder pb = new ProcessBuilder(
                "scrcpy",
                "-s", deviceId,
                "--window-title=" + windowTitle,
                "--max-size=1280",
                "--video-bit-rate=8000000",
                "--no-audio"
        );
        pb.redirectErrorStream(true);
        Process proc;
        try {
            proc = pb.start();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo iniciar scrcpy: " + e.getMessage(), e);
        }

        Thread waiter = new Thread(() -> {
            try { proc.waitFor(); } catch (InterruptedException ignored) {}
            System.out.println("scrcpy process ended for " + windowTitle + ", exit=" + proc.exitValue());
        }, "ScrcpyViewerWait-" + windowTitle);
        waiter.setDaemon(true);
        waiter.start();

        System.out.println("scrcpy iniciado para device=" + deviceId + " (windowTitle=" + windowTitle + ")");
        return new ViewerHandle(proc, waiter);
    }

    public static class ViewerHandle {
        private final Process process;
        private final Thread waiterThread;

        private ViewerHandle(Process process, Thread waiterThread) {
            this.process = process;
            this.waiterThread = waiterThread;
        }

        public void stop() {
            try { if (process.isAlive()) process.destroy(); } catch (Exception ignored) {}
            try { waiterThread.join(2000); } catch (InterruptedException ignored) {}
        }
    }
}
