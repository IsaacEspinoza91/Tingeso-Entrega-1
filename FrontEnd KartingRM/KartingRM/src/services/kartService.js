import axios from 'axios';
import httpClient from '../http-common';

const URL_LOCAL = '/karts/';

// Peticion GET para obtener la lista de karts
export const getKarts = async () => {
  try {
    const response = await httpClient.get(URL_LOCAL);
    return response.data;
  } catch (error) {
    console.error('Error al obtener karts:', error);
    throw error;
  }
};

// Peticion GET de kart segun id
export const getKartById = async (idkart) => {
  try {
    const response = await httpClient.get(`${URL_LOCAL}/${idkart}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener kart con ID ${idkart}:`, error);
    throw error;
  }
};

// Peticion POST para crear kart 
export const createKart = async (kartData) => {
  try {
    const response = await httpClient.post(URL_LOCAL, kartData);
    return response.data;
  } catch (error) {
    console.error('Error al crear kart:', error);
    throw error;
  }
};

// Peticion PUT para update de kart
export const updateKart = async (idkart, kartData) => {
  try {
    const response = await httpClient.put(`${URL_LOCAL}/${idkart}`, kartData);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar kart con ID ${idkart}:`, error);
    throw error;
  }
};

// Peticion DELETE para eliminar kart
export const deleteKart = async (idkart) => {
  try {
    await httpClient.delete(`${URL_LOCAL}/${idkart}`);
    return idkart;
  } catch (error) {
    console.error(`Error al eliminar kart con ID ${idkart}:`, error);
    throw error;
  }
};