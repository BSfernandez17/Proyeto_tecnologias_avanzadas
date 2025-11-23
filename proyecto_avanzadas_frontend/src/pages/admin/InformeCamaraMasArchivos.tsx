import React, { useEffect, useState } from "react";
import { obtenerImagenesPorCamara, obtenerVideosPorCamara } from "../../api/Archivos.api";
import { useAuth } from "../../context/AuthContex";
import { getUsuarios } from "../../api/Usuarios.api";
import { obtenerCamarasPorUsuario } from "../../api/Camaras.api";

interface CamaraInfo {
  id: string | number;
  nombre: string;
  cantidad: number;
}

const InformeCamaraMasArchivos: React.FC = () => {
  const { token } = useAuth();
  const [camaraTop, setCamaraTop] = useState<CamaraInfo | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      if (!token) return;
      setLoading(true);
      setError(null);
      try {
        // Obtener todos los usuarios
        const usuarios = await getUsuarios(token);
        let camaras: { id: number; nombre: string }[] = [];
        // Obtener todas las cámaras de todos los usuarios
        for (const usuario of usuarios) {
          const camarasUsuario = await obtenerCamarasPorUsuario(token, usuario.id);
          camaras = camaras.concat(camarasUsuario.map(c => ({ id: c.id, nombre: c.nombre })));
        }
        // Para cada cámara, obtener cantidad de archivos
        const resultados: CamaraInfo[] = [];
        for (const camara of camaras) {
          const imagenes = await obtenerImagenesPorCamara(String(camara.id), token);
          const videos = await obtenerVideosPorCamara(String(camara.id), token);
          resultados.push({
            id: camara.id,
            nombre: camara.nombre,
            cantidad: imagenes.length + videos.length
          });
        }
        // Buscar la cámara con más archivos
        const top = resultados.reduce((max, c) => c.cantidad > max.cantidad ? c : max, resultados[0]);
        setCamaraTop(top);
      } catch (e) {
        const msg = e instanceof Error ? e.message : String(e);
        setError(msg || "Error al calcular informe.");
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [token]);

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Cámara con más archivos enviados</h2>
      {loading && <div>Cargando informe...</div>}
      {error && <div className="text-red-600">{error}</div>}
      {!loading && camaraTop && (
        <div className="border rounded p-4 bg-gray-50">
          <div className="font-semibold">{camaraTop.nombre}</div>
          <div>ID: {camaraTop.id}</div>
          <div>Cantidad de archivos enviados: <span className="font-bold">{camaraTop.cantidad}</span></div>
        </div>
      )}
      {!loading && !camaraTop && <div>No hay datos de cámaras.</div>}
    </div>
  );
};

export default InformeCamaraMasArchivos;
