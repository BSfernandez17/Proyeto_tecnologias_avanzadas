import type { ReactNode } from "react";
import React, { createContext, useContext, useEffect, useState } from "react";

interface AuthContextType {
  token: string | null;
  rol: string | null;
  login: (token: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Función para decodificar el JWT y obtener el rol
const decodeToken = (token: string) => {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.rol || null; // Asumiendo que el rol está en el payload como 'rol'
  } catch (error) {
    console.error("Error decodificando token:", error);
    return null;
  }
};

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem("token"));
  const [rol, setRol] = useState<string | null>(() => {
    const storedToken = localStorage.getItem("token");
    return storedToken ? decodeToken(storedToken) : null;
  });

  const login = (newToken: string) => {
    setToken(newToken);
    localStorage.setItem("token", newToken);
    const decodedRol = decodeToken(newToken);
    setRol(decodedRol);
  };

  const logout = () => {
    setToken(null);
    setRol(null);
    localStorage.removeItem("token");
  };

  // Actualizar rol si el token cambia (por si acaso)
  useEffect(() => {
    if (token) {
      const decodedRol = decodeToken(token);
      setRol(decodedRol);
    } else {
      setRol(null);
    }
  }, [token]);

  return React.createElement(
    AuthContext.Provider,
    { value: { token, rol, login, logout } },
    children
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth debe usarse dentro de AuthProvider");
  return context;
};
