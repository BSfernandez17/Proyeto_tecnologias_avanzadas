package org.example.proyecto_ta;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FrameExtractor {

    /**
     * Extrae frames con ffmpeg:
     * ffmpeg -i input.mp4 outdir/frame_%05d.jpg
     */
    public static void extractFrames(Path video, Path outDir) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add("ffmpeg");
        cmd.add("-y"); // overwrite
        cmd.add("-i");
        cmd.add(video.toAbsolutePath().toString());
        // Ajusta la tasa si quieres más/menos frames: ejemplo "-vf", "fps=1" extrae 1 fps. Aquí extraemos todos los frames.
        cmd.add(outDir.resolve("frame_%05d.jpg").toAbsolutePath().toString());

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        // opcional: leer salida para logging
        new Thread(() -> {
            try (var in = p.getInputStream()) {
                byte[] b = new byte[1024];
                int r;
                while ((r = in.read(b)) != -1) {
                    // puedes imprimir si deseas: System.out.write(b,0,r);
                }
            } catch (Exception ignored) {}
        }).start();

        int code = p.waitFor();
        if (code != 0) {
            throw new RuntimeException("ffmpeg falló con código " + code);
        }
    }
}
