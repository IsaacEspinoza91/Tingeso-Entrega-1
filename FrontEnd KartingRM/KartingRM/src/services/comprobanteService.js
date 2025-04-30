import httpClient from '../http-common';

const URL_LOCAL = '/comprobantes';

// Peticion GET para obtener todos los comprobantes
export const getComprobantes = async () => {
  try {
    const response = await httpClient.get(`${URL_LOCAL}/`);
    return response.data;
  } catch (error) {
    console.error('Error al obtener comprobantes:', error);
    throw error;
  }
};

// Peticion Get para obtener comrpoabten segun id
export const getComprobanteById = async (idComprobante) => {
  try {
    const response = await httpClient.get(`${URL_LOCAL}/${idComprobante}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener comprobante con ID ${idComprobante}:`, error);
    throw error;
  }
};

// Peticion Post para crear comprobante segun id de reserva, booleano de dia feriado y descuento extra
export const createComprobante = async (idReserva, feriado, descuentoExtra) => {
  try {
    const response = await httpClient.post(
      `${URL_LOCAL}/segun-reserva/${idReserva}?feriado=${feriado}&descuento_extra=${descuentoExtra}`
    );
    return response.data;
  } catch (error) {
    console.error('Error al crear comprobante:', error);
    throw error;
  }
};

// Peticion PUT para update de comprobante
export const updateComprobante = async (idComprobante, comprobanteData) => {
  try {
    const response = await httpClient.put(`${URL_LOCAL}/${idComprobante}`, comprobanteData);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar comprobante con ID ${idComprobante}:`, error);
    throw error;
  }
};

// Peticion PUT para update de la reserva de comprobante
export const updateComprobanteReserva = async (idComprobante, idReserva) => {
  try {
    const response = await httpClient.put(`${URL_LOCAL}/${idComprobante}/reserva?id_reserva=${idReserva}`);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar reserva del comprobante ${idComprobante}:`, error);
    throw error;
  }
};

// Peticion Delete de comprobante segun id
export const deleteComprobante = async (idComprobante) => {
  try {
    await httpClient.delete(`${URL_LOCAL}/${idComprobante}`);
    return idComprobante;
  } catch (error) {
    console.error(`Error al eliminar comprobante con ID ${idComprobante}:`, error);
    throw error;
  }
};





// Operaciones de detalles

// Peticion GET de detalles para un comprobante segun id
export const getDetallesByComprobante = async (idComprobante) => {
  try {
    const response = await httpClient.get(`${URL_LOCAL}/detalles/comprobante/${idComprobante}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener detalles del comprobante ${idComprobante}:`, error);
    throw error;
  }
};

// Peticion PUT para update de detalle segun body
export const updateDetalle = async (idDetalle, detalleData) => {
  try {
    const response = await httpClient.put(`${URL_LOCAL}/detalles/update/${idDetalle}`, detalleData);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar detalle con ID ${idDetalle}:`, error);
    throw error;
  }
};

// Peticion PUT para update del cliente de detalle
export const updateDetalleCliente = async (idDetalle, idCliente) => {
  try {
    const response = await httpClient.put(`${URL_LOCAL}/detalles/update/${idDetalle}/cliente?id_cliente=${idCliente}`);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar cliente del detalle ${idDetalle}:`, error);
    throw error;
  }
};

// Peticion DELETE para detalle segun id
export const deleteDetalle = async (idDetalle) => {
  try {
    await httpClient.delete(`${URL_LOCAL}/detalles/delete/${idDetalle}`);
    return idDetalle;
  } catch (error) {
    console.error(`Error al eliminar detalle con ID ${idDetalle}:`, error);
    throw error;
  }
};