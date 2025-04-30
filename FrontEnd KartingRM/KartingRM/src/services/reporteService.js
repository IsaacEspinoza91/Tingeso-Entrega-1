import axios from 'axios';
import httpClient from '../http-common';

const URL_LOCAL = '/reportes/'; 

// Peticion Get para obtener los reportes de ingresos segun planes
export const getReporteIngresosPorVueltas = async (params) => {
  try {
    const response = await httpClient.get(`${URL_LOCAL}/ingresos-por-vueltas`, { params });
    return response.data;
  } catch (error) {
    console.error('Error al obtener reporte de ingresos por vueltas:', error);
    throw error;
  }
};

// Peticion Get para obtener los reportes de ingresos cantidad de personas por reserva
export const getReporteIngresosPorPersonas = async (params) => {
  try {
    const response = await httpClient.get(`${URL_LOCAL}/ingresos-por-personas`, { params });
    return response.data;
  } catch (error) {
    console.error('Error al obtener reporte de ingresos por personas:', error);
    throw error;
  }
};