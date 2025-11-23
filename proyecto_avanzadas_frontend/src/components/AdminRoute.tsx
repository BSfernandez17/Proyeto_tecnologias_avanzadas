import React from "react";
import { useAuth } from "../context/AuthContex";
import { Navigate } from "react-router-dom";

interface AdminRouteProps {
  children: React.ReactNode;
}

const AdminRoute: React.FC<AdminRouteProps> = ({ children }) => {
  const { token } = useAuth();
  // Decodifica el token y valida el rol
  const isAdmin = () => {
    if (!token) return false;
    try {
      const base64Payload = token.split('.')[1];
      const padded = base64Payload.padEnd(base64Payload.length + (4 - base64Payload.length % 4) % 4, '=');
      const payload = JSON.parse(atob(padded.replace(/-/g, '+').replace(/_/g, '/')));
      return payload.rol === "ADMIN";
    } catch {
      return false;
    }
  };
  if (!isAdmin()) {
    // Puedes cambiar esto por un <Navigate to="/login" /> si prefieres redirigir
    return <div className="text-center text-red-600 mt-8">No tienes permisos para acceder a esta página.</div>;
  }
  return <>{children}</>;
};

export default AdminRoute;
