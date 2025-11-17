package org.example;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ScrcpyRecorder {

    /**
     * Inicia scrcpy grabando a archivo. Devuelve el proceso.
     * Usa: scrcpy -s <device> --record=<file> --no-audio --no-control --max-size=1280 --video-bit-rate=8M
     */
    public static Process startRecording(String deviceId, String outputFile) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "scrcpy",
                "-s", deviceId,
                "--record=" + outputFile,
                "--no-audio",
                "--no-control",
                "--max-size=1280",
                "--video-bit-rate=8M"
        );
        pb.redirectErrorStream(true);
        return pb.start();
    }

    /** Detiene y espera al cierre real (esperar a que scrcpy finalize el MP4) */
    public static void stopRecording(Process process) {
        if (process == null) return;
        try {
            process.destroy();         // solicita cierre
            process.waitFor(10, TimeUnit.SECONDS); // espera hasta 10s
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}