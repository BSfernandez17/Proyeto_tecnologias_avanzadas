import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { obtenerImagenesPorCamara, obtenerVideosPorCamara } from "../api/Archivos.api";
import type { Imagen, Video } from "../api/Archivos.api";
import { useAuth } from "../context/AuthContex";

type Archivo =
  | (Imagen & { tipo: "imagen" })
  | (Video & { tipo: "video" });

const ArchivosPorCamara: React.FC = () => {
  const { camaraId } = useParams<{ camaraId: string }>();
  const { token } = useAuth();
  const [archivos, setArchivos] = useState<Archivo[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!camaraId || !token) return;
    setLoading(true);
    setError(null);
    Promise.all([
      obtenerImagenesPorCamara(camaraId, token),
      obtenerVideosPorCamara(camaraId, token)
    ])
      .then(([imagenes, videos]) => {
        setArchivos([
          ...imagenes.map(img => ({ ...img, tipo: "imagen" as const })),
          ...videos.map(vid => ({ ...vid, tipo: "video" as const }))
        ]);
      })
      .catch(e => setError(e.message))
      .finally(() => setLoading(false));
  }, [camaraId, token]);

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Archivos de la cámara {camaraId}</h2>
      {loading && <div>Cargando archivos...</div>}
      {error && <div className="text-red-600">{error}</div>}
      {!loading && archivos.length === 0 && <div>No hay archivos para esta cámara.</div>}
      {!loading && archivos.length > 0 && (
        <table className="min-w-full border">
          <thead>
            <tr>
              <th className="border px-2 py-1">Nombre</th>
              <th className="border px-2 py-1">Tipo</th>
              <th className="border px-2 py-1">Tamaño</th>
              <th className="border px-2 py-1">Fecha</th>
              <th className="border px-2 py-1">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {archivos.map((archivo) => (
              <tr key={archivo.id}>
                <td className="border px-2 py-1">{archivo.nombre}</td>
                <td className="border px-2 py-1">{archivo.tipo}</td>
                <td className="border px-2 py-1">{(archivo.tamano / 1024).toFixed(1)} KB</td>
                <td className="border px-2 py-1">{new Date(archivo.fecha).toLocaleString()}</td>
                <td className="border px-2 py-1">
                  <a href={archivo.url} target="_blank" rel="noopener noreferrer" className="text-blue-600 underline">Ver</a>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ArchivosPorCamara;
