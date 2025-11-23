import React, { createContext, useContext, useState } from "react";
import type { ReactNode } from "react";

interface AuthContextType {
  token: string | null;
  login: (token: string) => void;
  logout: () => void;
  isAdmin: () => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem("token"));

  const login = (newToken: string | null | undefined) => {
    if (!newToken) {
      setToken(null);
      localStorage.removeItem("token");
      return;
    }
    setToken(newToken);
    localStorage.setItem("token", newToken);
  };

  const logout = () => {
    setToken(null);
    localStorage.removeItem("token");
  };

  // Decodifica el token y verifica el campo 'admin'
  const isAdmin = () => {
    if (!token) return false;
    try {
      const base64Payload = token.split('.')[1];
      const padded = base64Payload.padEnd(base64Payload.length + (4 - base64Payload.length % 4) % 4, '=');
      const payload = JSON.parse(atob(padded.replace(/-/g, '+').replace(/_/g, '/')));
      return !!payload.admin;
    } catch {
      return false;
    }
  };

  return React.createElement(
    AuthContext.Provider,
    { value: { token, login, logout, isAdmin } },
    children
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth debe usarse dentro de AuthProvider");
  return context;
};
