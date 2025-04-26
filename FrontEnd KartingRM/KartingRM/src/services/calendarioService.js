import axios from 'axios';

const API_URL = '/api/rackSemanal';

export const getReservasSemana = async (semanaOffset = 0) => {
  try {
    // semanaOffset indica de que semana obtener las reservas.
    //  0 -> semana actual
    // -1 -> semana anterior
    //  1 -> semana siguiente
    const response = await axios.get(`${API_URL}/${semanaOffset}`);
    return response.data;
  } catch (error) {
    console.error('Error al obtener reservas semanales:', error);
    throw error;
  }
};