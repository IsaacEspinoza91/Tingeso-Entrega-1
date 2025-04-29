import React, { useState } from 'react';
import { createPlan } from '../../services/planService';
import './CreatePlanForm.css';

const CreatePlanForm = ({ onPlanCreated }) => {
  const [formData, setFormData] = useState({
    descripcion: '',
    duracionTotal: 30,
    precioRegular: 0,
    precioFinSemana: 0,
    precioFeriado: 0
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name.includes('precio') || name === 'duracionTotal' 
        ? Number(value) 
        : value
    });
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.descripcion.trim()) newErrors.descripcion = 'Descripción es requerida';
    if (formData.duracionTotal <= 0) newErrors.duracionTotal = 'Duración debe ser mayor a 0';
    if (formData.precioRegular <= 0) newErrors.precioRegular = 'Precio debe ser mayor a 0';
    if (formData.precioFinSemana <= 0) newErrors.precioFinSemana = 'Precio debe ser mayor a 0';
    if (formData.precioFeriado <= 0) newErrors.precioFeriado = 'Precio debe ser mayor a 0';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setIsSubmitting(true);
    try {
      await createPlan(formData);
      setSuccessMessage('Plan creado exitosamente!');
      setFormData({
        descripcion: '',
        duracionTotal: 0,
        precioRegular: 0,
        precioFinSemana: 0,
        precioFeriado: 0
      });
      
      if (onPlanCreated) {
        onPlanCreated();
      }
      
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Error al crear plan:', error);
      setErrors({ submit: 'Error al crear plan. Por favor intente nuevamente.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="create-plan-form">
      <h3>Crear Nuevo Plan</h3>
      {successMessage && <div className="success-message">{successMessage}</div>}
      {errors.submit && <div className="error-message">{errors.submit}</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Descripción:</label>
          <input
            type="text"
            name="descripcion"
            value={formData.descripcion}
            onChange={handleChange}
            placeholder="Ej: 20 vueltas o max 20 min"
          />
          {errors.descripcion && <span className="error">{errors.descripcion}</span>}
        </div>

        <div className="form-group">
          <label>Duración Total (minutos):</label>
          <input
            type="number"
            name="duracionTotal"
            value={formData.duracionTotal}
            onChange={handleChange}
            min="1"
          />
          {errors.duracionTotal && <span className="error">{errors.duracionTotal}</span>}
        </div>

        <div className="form-group">
          <label>Precio Regular:</label>
          <input
            type="number"
            name="precioRegular"
            value={formData.precioRegular}
            onChange={handleChange}
            min="1"
          />
          {errors.precioRegular && <span className="error">{errors.precioRegular}</span>}
        </div>

        <div className="form-group">
          <label>Precio Fin de Semana:</label>
          <input
            type="number"
            name="precioFinSemana"
            value={formData.precioFinSemana}
            onChange={handleChange}
            min="1"
          />
          {errors.precioFinSemana && <span className="error">{errors.precioFinSemana}</span>}
        </div>

        <div className="form-group">
          <label>Precio Feriado:</label>
          <input
            type="number"
            name="precioFeriado"
            value={formData.precioFeriado}
            onChange={handleChange}
            min="1"
          />
          {errors.precioFeriado && <span className="error">{errors.precioFeriado}</span>}
        </div>

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Creando...' : 'Crear Plan'}
        </button>
      </form>
    </div>
  );
};

export default CreatePlanForm;