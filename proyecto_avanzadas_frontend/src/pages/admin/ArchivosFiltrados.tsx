import React, { useEffect, useState } from "react";
import { obtenerImagenesPorCamara, obtenerVideosPorCamara } from "../../api/Archivos.api";
import { getUsuarios } from "../../api/Usuarios.api";
import { obtenerCamarasPorUsuario } from "../../api/Camaras.api";
import { useAuth } from "../../context/AuthContex";

interface Archivo {
  id: string;
  nombre: string;
  url: string;
  tamano: number;
  fecha: string;
  tipo: "imagen" | "video";
  camaraId: string | number;
  camaraNombre: string;
  propietario: string;
}

const ArchivosFiltrados: React.FC = () => {
  const { token } = useAuth();
  const [archivos, setArchivos] = useState<Archivo[]>([]);
  const [usuarios, setUsuarios] = useState<any[]>([]);
  const [camaras, setCamaras] = useState<any[]>([]);
  const [filtros, setFiltros] = useState({ usuario: '', camara: '', tipo: '', propietario: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      if (!token) return;
      setLoading(true);
      setError(null);
      try {
        const usuariosData = await getUsuarios(token);
        setUsuarios(usuariosData);
        let camarasData: any[] = [];
        let archivosData: Archivo[] = [];
        for (const usuario of usuariosData) {
          const camarasUsuario = await obtenerCamarasPorUsuario(token, usuario.id);
          camarasData = camarasData.concat(camarasUsuario.map(c => ({ ...c, propietario: usuario.nombre })));
          for (const camara of camarasUsuario) {
            const imagenes = await obtenerImagenesPorCamara(String(camara.id), token);
            archivosData = archivosData.concat(imagenes.map(img => ({
              ...img,
              tipo: "imagen",
              camaraId: camara.id,
              camaraNombre: camara.nombre,
              propietario: usuario.nombre
            })));
            const videos = await obtenerVideosPorCamara(String(camara.id), token);
            archivosData = archivosData.concat(videos.map(vid => ({
              ...vid,
              tipo: "video",
              camaraId: camara.id,
              camaraNombre: camara.nombre,
              propietario: usuario.nombre
            })));
          }
        }
        setCamaras(camarasData);
        setArchivos(archivosData);
      } catch (e) {
        const msg = e instanceof Error ? e.message : String(e);
        setError(msg || "Error al cargar archivos.");
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [token]);

  // Filtrado
  const archivosFiltrados = archivos.filter(a =>
    (!filtros.usuario || a.propietario === filtros.usuario) &&
    (!filtros.camara || String(a.camaraId) === filtros.camara) &&
    (!filtros.tipo || a.tipo === filtros.tipo) &&
    (!filtros.propietario || a.propietario === filtros.propietario)
  );

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Listado de archivos con filtros</h2>
      <div className="mb-4 flex flex-wrap gap-4">
        <select value={filtros.usuario} onChange={e => setFiltros(f => ({ ...f, usuario: e.target.value }))} className="border px-2 py-1">
          <option value="">Todos los usuarios</option>
          {usuarios.map(u => <option key={u.id} value={u.nombre}>{u.nombre}</option>)}
        </select>
        <select value={filtros.camara} onChange={e => setFiltros(f => ({ ...f, camara: e.target.value }))} className="border px-2 py-1">
          <option value="">Todas las cámaras</option>
          {camaras.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
        </select>
        <select value={filtros.tipo} onChange={e => setFiltros(f => ({ ...f, tipo: e.target.value }))} className="border px-2 py-1">
          <option value="">Todos los tipos</option>
          <option value="imagen">Imagen</option>
          <option value="video">Video</option>
        </select>
        <select value={filtros.propietario} onChange={e => setFiltros(f => ({ ...f, propietario: e.target.value }))} className="border px-2 py-1">
          <option value="">Todos los propietarios</option>
          {usuarios.map(u => <option key={u.id} value={u.nombre}>{u.nombre}</option>)}
        </select>
      </div>
      {loading && <div>Cargando archivos...</div>}
      {error && <div className="text-red-600">{error}</div>}
      {!loading && archivosFiltrados.length === 0 && <div>No hay archivos que coincidan con los filtros.</div>}
      {!loading && archivosFiltrados.length > 0 && (
        <table className="min-w-full border">
          <thead>
            <tr>
              <th className="border px-2 py-1">Nombre</th>
              <th className="border px-2 py-1">Tipo</th>
              <th className="border px-2 py-1">Tamaño</th>
              <th className="border px-2 py-1">Fecha</th>
              <th className="border px-2 py-1">Cámara</th>
              <th className="border px-2 py-1">Propietario</th>
              <th className="border px-2 py-1">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {archivosFiltrados.map((archivo) => (
              <tr key={archivo.id}>
                <td className="border px-2 py-1">{archivo.nombre}</td>
                <td className="border px-2 py-1">{archivo.tipo}</td>
                <td className="border px-2 py-1">{(archivo.tamano / 1024).toFixed(1)} KB</td>
                <td className="border px-2 py-1">{new Date(archivo.fecha).toLocaleString()}</td>
                <td className="border px-2 py-1">{archivo.camaraNombre}</td>
                <td className="border px-2 py-1">{archivo.propietario}</td>
                <td className="border px-2 py-1">
                  <a href={archivo.url} target="_blank" rel="noopener noreferrer" className="text-blue-600 underline">Ver</a>
                  <a href={archivo.url} download className="ml-2 text-green-600 underline">Descargar</a>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ArchivosFiltrados;
