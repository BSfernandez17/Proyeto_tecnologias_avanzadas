import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { type Usuario } from "../model";
import { useAuth } from "../context/AuthContex";
import { getUsuarios } from "../api/Usuarios.api"; // ✅ servicio corregido

export const ListaUsuarios = () => {
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>("");
  const { token } = useAuth();

  useEffect(() => {
    const fetchUsuarios = async () => {
      if (!token) {
        setError("Debes iniciar sesión para ver los usuarios.");
        return;
      }

      setLoading(true);
      setError("");

      try {
        const data = await getUsuarios(token); // ✅ llamada al servicio con token
        setUsuarios(data);
      } catch (err) {
        console.error("Error al obtener usuarios:", err);
        setError("Error al obtener usuarios (token inválido o expirado).");
      } finally {
        setLoading(false);
      }
    };

    fetchUsuarios();
  }, [token]);

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-lg font-semibold text-gray-800">Usuarios</h2>
        <div className="flex gap-2">
          <Link
            to="/usuarios/registrar"
            className="inline-flex items-center px-3 py-2 rounded-md bg-indigo-600 text-white text-sm hover:bg-indigo-700"
          >
            Registrar usuario
          </Link>
          <Link
            to="/admin/informe-camara-mas-archivos"
            className="inline-flex items-center px-3 py-2 rounded-md bg-blue-600 text-white text-sm hover:bg-blue-700"
          >
            Informe cámaras con más archivos
          </Link>
          <Link
            to="/admin/archivos-filtrados"
            className="inline-flex items-center px-3 py-2 rounded-md bg-green-600 text-white text-sm hover:bg-green-700"
          >
            Archivos filtrados
          </Link>
        </div>
      </div>
      {loading ? (
        <div className="flex items-center justify-center p-4">
          <svg
            className="animate-spin -ml-1 mr-3 h-5 w-5 text-gray-600"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            ></circle>
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
            ></path>
          </svg>
          <p className="text-gray-700">Cargando usuarios...</p>
        </div>
      ) : error ? (
        <p className="text-red-600 text-center">{error}</p>
      ) : usuarios.length === 0 ? (
        <p className="text-center text-gray-600">No hay usuarios disponibles.</p>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full border-collapse">
            <thead>
              <tr>
                {["id", "nombre", "email", "rol", "ip", "status", "acciones"].map((key) => (
                  <th
                    key={key}
                    className="text-left border-b border-gray-200 px-4 py-2 bg-gray-100 text-sm font-medium text-gray-700"
                  >
                    {key.charAt(0).toUpperCase() + key.slice(1)}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {usuarios.map((usuario) => (
                <tr
                  key={usuario.id}
                  className="odd:bg-white even:bg-gray-50 hover:bg-gray-100 transition"
                >
                  <td className="px-4 py-2 border-b text-sm text-gray-700">
                    {usuario.id}
                  </td>
                  <td className="px-4 py-2 border-b text-sm text-gray-700">
                    {usuario.nombre}
                  </td>
                  <td className="px-4 py-2 border-b text-sm text-gray-700">
                    {usuario.email}
                  </td>
                  <td className="px-4 py-2 border-b text-sm text-gray-700">
                    {usuario.rol}
                  </td>
                  <td className="px-4 py-2 border-b text-sm text-gray-700">
                    {usuario.ip}
                  </td>
                  <td className="px-4 py-2 border-b text-sm text-gray-700">
                    {usuario.status ? "activo" : "pendiente"}
                  </td>
                  <td className="px-4 py-2 border-b text-sm text-gray-700">
                    <Link 
                    to={`/usuarios/VerReporte/${usuario.id}`}
                    className="inline-flex items-center px-3 py-2 rounded-md bg-yellow-500 text-white text-sm hover:bg-green-600">
                      Ver Reporte
                    </Link>
                  </td>
                  <td className="px-4 py-2 border-b text-sm text-gray-700">
                    <Link
                      to={`/usuarios/editar/${usuario.id}`}
                      className="inline-flex items-center px-3 py-2 rounded-md bg-yellow-500 text-white text-sm hover:bg-yellow-600"
                      aria-label={`Editar usuario ${usuario.nombre}`}
                    >
                      Editar
                    </Link>
                  </td>
                </tr>

              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};
