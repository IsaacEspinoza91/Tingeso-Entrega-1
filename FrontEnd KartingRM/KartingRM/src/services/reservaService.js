import httpClient from '../http-common';

const URL_LOCAL = '/reservas';

// Peticion GET de todas las reservas
export const getReservas = async () => {
  try {
    const response = await httpClient.get(`${URL_LOCAL}/`);
    return response.data;
  } catch (error) {
    console.error('Error al obtener reservas:', error);
    throw error;
  }
};

// Peticion Get de reserva segun id
export const getReservaById = async (idReserva) => {
  try {
    const response = await httpClient.get(`${URL_LOCAL}/${idReserva}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener reserva con ID ${idReserva}:`, error);
    throw error;
  }
};

// Peticion Get de reservas de un cliente segun id
export const getReservasByCliente = async (idCliente) => {
  try {
    const response = await httpClient.get(`${URL_LOCAL}/por-cliente/${idCliente}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener reservas del cliente ${idCliente}:`, error);
    throw error;
  }
};

// Peticion POST para crear reserva indicando id de cliente, id de plan, booleano que indica si 
//  es dia feriado, y el cuerpo de la reserva
export const createReserva = async (idCliente, idPlan, feriado, reservaData) => {
  try {
    const response = await httpClient.post(
      `${URL_LOCAL}?id_cliente=${idCliente}&id_plan=${idPlan}&feriado=${feriado}`,
      reservaData
    );
    return response.data;
  } catch (error) {
    console.error('Error al crear reservaxx:', error);
    throw error;
  }
};

// Peticion PUT para update de reserva segun cuerpo
export const updateReserva = async (idReserva, reservaData) => {
  try {
    const response = await httpClient.put(`${URL_LOCAL}/${idReserva}`, reservaData);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar reserva con ID ${idReserva}:`, error);
    throw error;
  }
};

// Peticion PUT para update del cliente reservante de una reserva
export const updateClienteReserva = async (idReserva, idCliente) => {
  try {
    const response = await httpClient.put(`${URL_LOCAL}/${idReserva}/cliente?id_cliente=${idCliente}`);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar cliente en reserva ${idReserva}:`, error);
    throw error;
  }
};

// Peticion PUT para update del plan de una reserva
export const updatePlanReserva = async (idReserva, idPlan) => {
  try {
    const response = await httpClient.put(`${URL_LOCAL}/${idReserva}/plan?id_plan=${idPlan}`);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar plan en reserva ${idReserva}:`, error);
    throw error;
  }
};

// Peticion PATCH para relacionar cliente integrante con reserva
export const addIntegrante = async (idReserva, idCliente) => {
  try {
    const response = await httpClient.patch(`${URL_LOCAL}/${idReserva}/integrantes/agregar/${idCliente}`);
    return response.data;
  } catch (error) {
    console.error(`Error al agregar integrante a reserva ${idReserva}:`, error);
    throw error;
  }
};

// Peticion PATCH para quitar un cliente como integrante de una reserva
export const removeIntegrante = async (idReserva, idCliente) => {
  try {
    const response = await httpClient.patch(`${URL_LOCAL}/${idReserva}/integrantes/quitar/${idCliente}`);
    return response.data;
  } catch (error) {
    console.error(`Error al quitar integrante de reserva ${idReserva}:`, error);
    throw error;
  }
};

// Peticion DELETE para eliminar una reserva
export const deleteReserva = async (idReserva) => {
  try {
    await httpClient.delete(`${URL_LOCAL}/${idReserva}`);
    return idReserva;
  } catch (error) {
    console.error(`Error al eliminar reserva con ID ${idReserva}:`, error);
    throw error;
  }
};