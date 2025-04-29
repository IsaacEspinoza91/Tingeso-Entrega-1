import axios from 'axios';

const API_URL = 'http://localhost:8080/karts/';

// Peticion GET para obtener la lista de karts
export const getKarts = async () => {
  try {
    const response = await axios.get(API_URL);
    return response.data;
  } catch (error) {
    console.error('Error al obtener karts:', error);
    throw error;
  }
};

// Peticion GET de kart segun id
export const getKartById = async (idkart) => {
  try {
    const response = await axios.get(`${API_URL}/${idkart}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener kart con ID ${idkart}:`, error);
    throw error;
  }
};

// Peticion POST para crear kart 
export const createKart = async (kartData) => {
  try {
    const response = await axios.post(API_URL, kartData);
    return response.data;
  } catch (error) {
    console.error('Error al crear kart:', error);
    throw error;
  }
};

// Peticion PUT para update de kart
export const updateKart = async (idkart, kartData) => {
  try {
    const response = await axios.put(`${API_URL}/${idkart}`, kartData);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar kart con ID ${idkart}:`, error);
    throw error;
  }
};

// Peticion DELETE para eliminar kart
export const deleteKart = async (idkart) => {
  try {
    await axios.delete(`${API_URL}/${idkart}`);
    return idkart;
  } catch (error) {
    console.error(`Error al eliminar kart con ID ${idkart}:`, error);
    throw error;
  }
};