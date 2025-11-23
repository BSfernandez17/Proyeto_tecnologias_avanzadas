import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../api/Auth"; // ✅ Usa el servicio de login creado antes
import { useAuth } from "../context/AuthContex"; // ✅ Usa el contexto global de autenticación

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [error, setError] = useState("");

  // Limpia el error al cambiar email o contraseña
  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEmail(e.target.value);
    setError("");
  };
  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setContrasena(e.target.value);
    setError("");
  };
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login, logout } = useAuth(); // viene del contexto global

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    // Siempre limpiar el token antes de intentar login
    login("");
    try {
      const { token } = await loginUser({ email, contrasena });
      if (!token) {
        login("");
        setError('No se recibió token en el login');
        setLoading(false);
        return;
      }
      // Decodificar el token JWT y validar el campo 'admin'
      const decodeJwtPayload = (jwt: string) => {
        try {
          const base64Payload = jwt.split('.')[1];
          const padded = base64Payload.padEnd(base64Payload.length + (4 - base64Payload.length % 4) % 4, '=');
          const payload = JSON.parse(atob(padded.replace(/-/g, '+').replace(/_/g, '/')));
          return payload;
        } catch (e) {
          console.error('Error decodificando JWT:', e);
          return null;
        }
      };
      const payload = decodeJwtPayload(token);
      console.log('JWT payload:', payload);
      if (!payload || payload.rol !== "ADMIN") {
        login("");
        setError('Solo los usuarios ADMIN pueden iniciar sesión.');
        setLoading(false);
        return;
      }
      // Login exitoso: limpiar error y actualizar contexto
      setError("");
      login(token);
      setEmail("");
      setContrasena("");
      navigate("/home");
    } catch (err: unknown) {
      login("");
      console.error("Error en login:", err);
      const defaultMsg = "Credenciales incorrectas o error en el servidor.";
      type ErrorResponse = { response?: { data?: { message?: string } } };
      const isErrorResponse = (e: unknown): e is ErrorResponse =>
        typeof e === "object" && e !== null && "response" in e;
      let msg = defaultMsg;
      if (isErrorResponse(err) && err.response?.data?.message) {
        msg = err.response.data.message;
      } else if (err instanceof Error && err.message) {
        msg = err.message;
      }
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="max-w-md w-full bg-white rounded-lg shadow-md p-8">
        <h2 className="text-2xl font-semibold text-gray-800 mb-6 text-center">
          Iniciar sesión
        </h2>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Correo
            </label>
              <input
                type="email"
                placeholder="tucorreo@ejemplo.com"
                value={email}
                onChange={handleEmailChange}
                required
                className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Contraseña
            </label>
              <input
                type="password"
                placeholder="Contraseña"
                value={contrasena}
                onChange={handlePasswordChange}
                required
                className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              />
          </div>

          <div>
            <button
              type="submit"
              disabled={loading}
              className="w-full inline-flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              {loading ? "Entrando..." : "Entrar"}
            </button>
          </div>
          <div>
            <div className="text-center">
              <button
                type="button"
                onClick={() => navigate("usuarios/registrar")}
                className="text-sm text-indigo-600 hover:text-indigo-800 underline"
              >
                ¿No tienes una cuenta? Regístrate
              </button>
            </div>
          </div>
        </form>

        {error && (
          <p className="mt-4 text-sm text-red-600 text-center">{error}</p>
        )}
      </div>
    </div>
  );
}
