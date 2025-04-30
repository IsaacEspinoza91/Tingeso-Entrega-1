import httpClient from '../http-common';

const URL_LOCAL = '/planes';

// Peticion GET de todos los planes
export const getPlanes = async () => {
  try {
    const response = await httpClient.get(`${URL_LOCAL}/`);
    return response.data;
  } catch (error) {
    console.error('Error al obtener planes:', error);
    throw error;
  }
};

/// Peticion GET de plan segun id
export const getPlanById = async (idPlan) => {
  try {
    const response = await httpClient.get(`${URL_LOCAL}/${idPlan}`);
    return response.data;
  } catch (error) {
    console.error(`Error al obtener plan con ID ${idPlan}:`, error);
    throw error;
  }
};

// Peticion POST para crear plan
export const createPlan = async (planData) => {
  try {
    const response = await httpClient.post(`${URL_LOCAL}/`, planData);
    return response.data;
  } catch (error) {
    console.error('Error al crear plan:', error);
    throw error;
  }
};

// Peticion PUT para update de plan, segun id y body
export const updatePlan = async (idPlan, planData) => {
  try {
    const response = await httpClient.put(`${URL_LOCAL}/${idPlan}`, planData);
    return response.data;
  } catch (error) {
    console.error(`Error al actualizar plan con ID ${idPlan}:`, error);
    throw error;
  }
};

// Peticion DELETE para eliminar plan
export const deletePlan = async (idPlan) => {
  try {
    await httpClient.delete(`${URL_LOCAL}/${idPlan}`);
    return idPlan;
  } catch (error) {
    console.error(`Error al eliminar plan con ID ${idPlan}:`, error);
    throw error;
  }
};