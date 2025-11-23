import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
// import { obtenerArchivosPorCamara } from "../api/Archivos.api"; // Asume que tienes esta función en tu API

interface Archivo {
  id: string;
  nombre: string;
  tipo: string; // "video" | "imagen"
  tamano: number;
  fecha: string;
  url: string;
}

const ArchivosPorCamara: React.FC = () => {
  const { camaraId } = useParams<{ camaraId: string }>();
  const [archivos, setArchivos] = useState<Archivo[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!camaraId) return;
    setLoading(true);
    setError(null);
    // Simulación de llamada a la API
    // Reemplaza esto por tu llamada real a la API
    setTimeout(() => {
      setArchivos([
        {
          id: "1",
          nombre: "video1.mp4",
          tipo: "video",
          tamano: 1048576,
          fecha: "2025-11-22T10:00:00Z",
          url: "#"
        },
        {
          id: "2",
          nombre: "imagen1.jpg",
          tipo: "imagen",
          tamano: 204800,
          fecha: "2025-11-22T11:00:00Z",
          url: "#"
        }
      ]);
      setLoading(false);
    }, 1000);
    // obtenerArchivosPorCamara(camaraId).then(setArchivos).catch(e => setError(e.message)).finally(() => setLoading(false));
  }, [camaraId]);

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
                  {/* Puedes agregar botón de descarga aquí */}
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
