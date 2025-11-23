import axios from "axios";

export interface Imagen {
  id: string;
  nombre: string;
  url: string;
  tamano: number;
  fecha: string;
}

export interface Video {
  id: string;
  nombre: string;
  url: string;
  tamano: number;
  fecha: string;
}

// Informe: cantidad de archivos por cámara
export interface ConteoArchivosPorCamara {
  camaraId: string;
  nombreCamara: string;
  cantidad: number;
}

export const obtenerImagenesPorCamara = async (camaraId: string, token?: string) => {
  const res = await axios.get(`/api/imagenes/ImagenPorCamara/${camaraId}`,
    token ? { headers: { Authorization: `Bearer ${token}` } } : undefined
  );
  return res.data as Imagen[];
};

export const obtenerVideosPorCamara = async (camaraId: string, token?: string) => {
  const res = await axios.get(`/api/videos/VideoPorCamara/${camaraId}`,
    token ? { headers: { Authorization: `Bearer ${token}` } } : undefined
  );
  return res.data as Video[];
};

export const obtenerConteoArchivosPorCamara = async (token: string) => {
  const res = await axios.get('/api/archivos/ConteoPorCamara', {
    headers: { Authorization: `Bearer ${token}` }
  });
  return res.data as ConteoArchivosPorCamara[];
};
