import React, { useEffect, useState } from 'react'
import type { CamaraDTO } from '../api/Camaras.api'
import * as CamarasApi from '../api/Camaras.api'
import { useAuth } from '../context/AuthContex'

type CamarasPorUsuarioProps = {
  id?: string
}

export const CamarasPorUsuario: React.FC<CamarasPorUsuarioProps> = ({ id }) => {
  const { token } = useAuth();
  const [camaras, setCamaras] = useState<CamaraDTO[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    if (!token) {
      setError('No autenticado. Inicia sesión para ver las cámaras.');
      return;
    }

    const fetchCamaras = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await CamarasApi.obtenerCamarasPorUsuario(token, Number(id));
        setCamaras(data);
      } catch (e: unknown) {
        const msg = e instanceof Error ? e.message : String(e);
        setError(msg || 'Error al obtener cámaras.');
      } finally {
        setLoading(false);
      }
    };

    fetchCamaras();
  }, [id, token]);

  return (
    <div>
      <h2 className="text-lg font-semibold mb-2">Cámaras del usuario {id ?? ''}</h2>

      {!token && (<div className="text-yellow-700">Debes iniciar sesión para ver las cámaras.</div>)}

      {loading && <div>Cargando cámaras...</div>}
      {error && <div className="text-red-600">{error}</div>}

      {!loading && camaras && camaras.length === 0 && <div>No hay cámaras registradas para este usuario.</div>}

      {!loading && camaras && camaras.length > 0 && (
        <ul className="space-y-2">
          {camaras.map((c) => (
            <li key={c.id} className="border rounded p-2">
              <div className="font-medium">{c.nombre}</div>
              <div>IP: {c.ip}</div>
              <div>Ubicación: {c.ubicacion ?? '-'}</div>
              <div>Estado: {c.estado ? 'Activo' : 'Inactivo'}</div>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

export default CamarasPorUsuario
