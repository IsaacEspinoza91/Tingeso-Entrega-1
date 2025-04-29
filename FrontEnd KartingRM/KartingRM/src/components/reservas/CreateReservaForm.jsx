import React, { useState } from 'react';
import { createReserva } from '../../services/reservaService';
import './CreateReservaForm.css';

const CreateReservaForm = ({ onReservaCreated }) => {
  const [formData, setFormData] = useState({
    estado: 'confirmada',
    totalPersonas: 1,
    fecha: '',
    horaInicio: ''
  });
  const [idCliente, setIdCliente] = useState('');
  const [idPlan, setIdPlan] = useState('');
  const [feriado, setFeriado] = useState(false);
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === 'totalPersonas' ? Number(value) : value
    });
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!idCliente) newErrors.idCliente = 'ID Cliente es requerido';
    if (!idPlan) newErrors.idPlan = 'ID Plan es requerido';
    if (!formData.estado) newErrors.estado = 'Estado es requerido';
    if (formData.totalPersonas <= 0) newErrors.totalPersonas = 'Debe haber al menos 1 persona';
    if (!formData.fecha) newErrors.fecha = 'Fecha es requerida';
    if (!formData.horaInicio) newErrors.horaInicio = 'Hora de inicio es requerida';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setIsSubmitting(true);
    try {
      await createReserva(idCliente, idPlan, feriado, formData);
      setSuccessMessage('Reserva creada exitosamente!');
      setFormData({
        estado: 'confirmada',
        totalPersonas: 1,
        fecha: '',
        horaInicio: '',
        horaFin: ''
      });
      setIdCliente('');
      setIdPlan('');
      setFeriado(false);
      
      if (onReservaCreated) {
        onReservaCreated();
      }
      
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Error al crear reserva:', error);
      setErrors({ submit: 'Error al crear reserva. Verifique los IDs, horario válido o intente nuevamente.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="create-reserva-form">
      <h3>Crear Nueva Reserva</h3>
      {successMessage && <div className="success-message">{successMessage}</div>}
      {errors.submit && <div className="error-message">{errors.submit}</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <div className="form-group">
            <label>ID Cliente Reservante:</label>
            <input
              type="number"
              value={idCliente}
              onChange={(e) => setIdCliente(e.target.value)}
              min="1"
            />
            {errors.idCliente && <span className="error">{errors.idCliente}</span>}
          </div>

          <div className="form-group">
            <label>ID Plan:</label>
            <input
              type="number"
              value={idPlan}
              onChange={(e) => setIdPlan(e.target.value)}
              min="1"
            />
            {errors.idPlan && <span className="error">{errors.idPlan}</span>}
          </div>

          <div className="form-group checkbox-group">
            <label>
              <input
                type="checkbox"
                checked={feriado}
                onChange={(e) => setFeriado(e.target.checked)}
              />
              ¿Es día feriado?
            </label>
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Estado:</label>
            <select
              name="estado"
              value={formData.estado}
              onChange={handleChange}
            >
              <option value="confirmada">Confirmada</option>
              <option value="cancelada">Cancelada</option>
              <option value="completada">Completada</option>
            </select>
            {errors.estado && <span className="error">{errors.estado}</span>}
          </div>

          <div className="form-group">
            <label>Total de Personas:</label>
            <input
              type="number"
              name="totalPersonas"
              value={formData.totalPersonas}
              onChange={handleChange}
              min="1"
            />
            {errors.totalPersonas && <span className="error">{errors.totalPersonas}</span>}
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Fecha:</label>
            <input
              type="date"
              name="fecha"
              value={formData.fecha}
              onChange={handleChange}
            />
            {errors.fecha && <span className="error">{errors.fecha}</span>}
          </div>

          <div className="form-group">
            <label>Hora Inicio:</label>
            <input
              type="time"
              name="horaInicio"
              value={formData.horaInicio}
              onChange={handleChange}
            />
            {errors.horaInicio && <span className="error">{errors.horaInicio}</span>}
          </div>
        </div>

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Creando...' : 'Crear Reserva'}
        </button>
      </form>
    </div>
  );
};

export default CreateReservaForm;