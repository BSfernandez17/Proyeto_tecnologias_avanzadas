package org.example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import org.example.Services.CamaraServicio;
import org.example.ConexionApi.CamaraApi;
import org.example.Model.Usuario;
 
 

public class CameraManager {
    private static final CameraManager INSTANCE = new CameraManager();
    private static final int INGEST_PORT = 9000;

    // registered cameras
    private final List<Camera> cameras = Collections.synchronizedList(new ArrayList<>());

    // running services by camera id: store AdbStreamHandle so we can stop streams
    private final Map<String, org.example.TCPClient.AdbStreamHandle> running = new ConcurrentHashMap<>();
    private final Map<String, Thread> runningThreads = new ConcurrentHashMap<>();
    private final Map<String, org.example.StreamViewer.ViewerHandle> viewers = new ConcurrentHashMap<>();
    private final Map<String, org.example.TCPClient.AdbSegmentHandle> segmentedRunning = new ConcurrentHashMap<>();

    private String token;

    private CameraManager() {}

    public static CameraManager get() { return INSTANCE; }

    public void register(Camera cam) {
        cameras.add(cam);
    }

    public List<Camera> list() {
        return new ArrayList<>(cameras);
    }

    // Refresh local camera list from remote API using token stored in AppContext
    public synchronized void refreshFromApi() {
        String token = AppContext.getInstance().getToken();
        Usuario usuario = AppContext.getInstance().getUsuario();
        if (token == null || usuario == null || usuario.getId() == null) {
            System.err.println("No hay token o usuario en contexto; no se puede obtener cámaras desde API.");
            return;
        }
        try {
            CamaraServicio servicio = new CamaraServicio(new CamaraApi());
            java.util.List<org.example.Model.Camara> remote = servicio.obtenerCamarasPorUsuario(usuario.getId());
            cameras.clear();
            if (remote != null) {
                for (org.example.Model.Camara rc : remote) {
                    String host = rc.getServerHost();
                    // Always use server port 9000 for ingest
                    int port = INGEST_PORT;
                    boolean ok = false;
                    if (host != null && port > 0) {
                        try (java.net.Socket s = new java.net.Socket()) {
                            s.connect(new java.net.InetSocketAddress(host, port), 1000);
                            ok = true;
                        } catch (Exception ignored) {
                        }
                    }

                    // We always target INGEST_PORT (9000). Optionally verify reachability.
                    if (!ok && host != null) {
                        try (java.net.Socket s2 = new java.net.Socket()) {
                            s2.connect(new java.net.InetSocketAddress(host, INGEST_PORT), 1000);
                            ok = true;
                        } catch (Exception ignored) {
                        }
                    }

                    if (!ok) {
                        System.out.println("Advertencia: no se pudo conectar al servidor de la cámara " + rc.getId() + " en " + host + ":" + rc.getServerPort());
                    }

                    Camera runtime = new Camera(rc.getId(), rc.getNombre(), host, INGEST_PORT);
                    cameras.add(runtime);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener cámaras desde API: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized boolean startCamera(String cameraId) {
        if (running.containsKey(cameraId)) return false;
        Camera cam = cameras.stream().filter(c -> c.getId().equals(cameraId)).findFirst().orElse(null);
        if (cam == null) return false;
        // Verify backend TCP server is reachable before starting
        String host = cam.getServerHost();
        int port = cam.getServerPort();
        try (java.net.Socket test = new java.net.Socket()) {
            test.connect(new java.net.InetSocketAddress(host, port), 2000);
        } catch (Exception ex) {
            System.err.println("No se puede conectar al servidor " + host + ":" + port + " — " + ex.getMessage());
            return false;
        }

        // Verify adb device is reachable
        try {
            Process p = new ProcessBuilder("adb", "-s", cam.getId(), "get-state").start();
            boolean finished = p.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished || p.exitValue() != 0) {
                System.err.println("Dispositivo ADB no disponible o no responde: " + cam.getId());
                return false;
            }
        } catch (Exception ex) {
            System.err.println("Error comprobando ADB para deviceId=" + cam.getId() + ": " + ex.getMessage());
            return false;
        }

        // Start live adb -> TCP stream using TCPClient.startAdbStream
        Thread t = new Thread(() -> {
                try {
                org.example.TCPClient tcpClient = new org.example.TCPClient(cam.getServerHost(), cam.getServerPort());
                // start segmented uploads: every 60 seconds a video file will be sent to server
                org.example.TCPClient.AdbSegmentHandle handle = tcpClient.startSegmentedAdbStream(cam.getId(), cam.getId(), 60);
                segmentedRunning.put(cameraId, handle);
                System.out.println("Stream en vivo iniciado para cámara: " + cam.getName());
                // start viewer window (connect to viewer port 9001)
                try {
                    // Open a local scrcpy window for the adb device instead of connecting to a viewer TCP port
                    org.example.StreamViewer.ViewerHandle vhandle = StreamViewer.startViewerForDevice(cam.getId(), "Cam: " + cam.getName());
                    viewers.put(cameraId, vhandle);
                } catch (Exception ex) {
                    System.err.println("No se pudo iniciar el visor local (scrcpy): " + ex.getMessage());
                }
                // Wait until the process ends
                try {
                    // wait for segmented adb process to finish (if it is the one running)
                    org.example.TCPClient.AdbSegmentHandle seg = segmentedRunning.get(cameraId);
                    if (seg != null) {
                        seg.waitFor();
                    }
                } catch (InterruptedException ignored) {}
            } catch (Exception e) {
                System.err.println("Error iniciando stream para cámara: " + cam.getName());
                e.printStackTrace();
            } finally {
                running.remove(cameraId);
                segmentedRunning.remove(cameraId);
                // stop and remove viewer if any
                org.example.StreamViewer.ViewerHandle v = viewers.remove(cameraId);
                if (v != null) v.stop();
                runningThreads.remove(cameraId);
            }
        }, "CameraStream-" + cameraId);
        t.setDaemon(true);
        runningThreads.put(cameraId, t);
        t.start();
        return true;
    }

    public synchronized boolean stopCamera(String cameraId) {
        org.example.TCPClient.AdbStreamHandle handle = running.remove(cameraId);
        Thread t = runningThreads.remove(cameraId);
        org.example.StreamViewer.ViewerHandle vhandle = viewers.remove(cameraId);
        if (handle != null) {
            handle.stop();
        }
        if (vhandle != null) vhandle.stop();
        if (t != null) {
            try { t.join(3000); } catch (InterruptedException ignored) {}
        }
        return handle != null;
    }

    public boolean isRunning(String cameraId) {
        return running.containsKey(cameraId);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}