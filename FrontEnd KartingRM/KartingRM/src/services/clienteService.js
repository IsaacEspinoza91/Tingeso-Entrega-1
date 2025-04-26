import axios from 'axios';

const API_URL = 'api/clientes/';

// Peticion GET de obtener todos los clientes
export const getClientes = async () => {
  try {
    const response = await axios.get(API_URL);
    return response.data;
  } catch (error) {
    console.error('Error al obtener clientes:', error);
    throw error;
  }
};

// Peticion POST de creacion de cliente
export const createCliente = async (clienteData) => {
  try {
    const response = await axios.post(API_URL, clienteData);
    return response.data;
  } catch (error) {
    console.error('Error al crear cliente:', error);
    throw error;
  }
};

// Peticion GET de cliente segun id
export const getClienteById = async (idKart) => {
  try {
    const response = await axios.get(`${API_URL}/${idKart}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener cliente con ID ${idKart}:`, error);
    throw error;
  }
};

// Peticion GET de cliente segun rut
export const getClienteByRut = async (rut) => {
  try {
    const response = await axios.get(`${API_URL}/rut/${rut}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener cliente con RUT ${rut}:`, error);
    throw error;
  }
};

// Peticion GET de cliente/s segun nombre y apellido
export const getClientesByNombreApellido = async (nombre, apellido) => {
  try {
    const response = await axios.get(`${API_URL}/nombre/${nombre}/${apellido}`);
    return response.data;
  } catch (error) {
    console.error(`Error al buscar clientes por nombre ${nombre} y apellido ${apellido}:`, error);
    throw error;
  }
};

// Peticion PUT de update de cliente segun id y body
export const updateCliente = async (idKart, clienteData) => {
  try {
    const response = await axios.put(`${API_URL}/${idKart}`, clienteData);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar cliente con ID ${idKart}:`, error);
    throw error;
  }
};

// Peticion DELETE para eliminar un cliente 
export const deleteCliente = async (idKart) => {
  try {
    await axios.delete(`${API_URL}/${idKart}`);
    return id; // Retornamos el ID eliminado para referencia
  } catch (error) {
    console.error(`Error al eliminar cliente con ID ${idKart}:`, error);
    throw error;
  }
};