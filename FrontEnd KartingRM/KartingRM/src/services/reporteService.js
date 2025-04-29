import axios from 'axios';

const API_URL = 'http://localhost:8080/reportes/'; 

// Peticion Get para obtener los reportes de ingresos segun planes
export const getReporteIngresosPorVueltas = async (params) => {
  try {
    const response = await axios.get(`${API_URL}/ingresos-por-vueltas`, { params });
    return response.data;
  } catch (error) {
    console.error('Error al obtener reporte de ingresos por vueltas:', error);
    throw error;
  }
};

// Peticion Get para obtener los reportes de ingresos cantidad de personas por reserva
export const getReporteIngresosPorPersonas = async (params) => {
  try {
    const response = await axios.get(`${API_URL}/ingresos-por-personas`, { params });
    return response.data;
  } catch (error) {
    console.error('Error al obtener reporte de ingresos por personas:', error);
    throw error;
  }
};