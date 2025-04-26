import React, { useState, useEffect } from 'react';
import { updatePlan } from '../../services/planService';
import './EditPlanModal.css';

const EditPlanModal = ({ plan, onClose, onUpdate }) => {
  const [formData, setFormData] = useState({
    descripcion: '',
    duracionTotal: 0,
    precioRegular: 0,
    precioFinSemana: 0,
    precioFeriado: 0
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (plan) {
      setFormData({
        descripcion: plan.descripcion || '',
        duracionTotal: plan.duracionTotal || 0,
        precioRegular: plan.precioRegular || 0,
        precioFinSemana: plan.precioFinSemana || 0,
        precioFeriado: plan.precioFeriado || 0
      });
    }
  }, [plan]);

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
      const updatedPlan = await updatePlan(plan.idPlan, formData);
      onUpdate(updatedPlan);
      onClose();
    } catch (error) {
      console.error('Error al actualizar plan:', error);
      setErrors({ submit: 'Error al actualizar plan. Por favor intente nuevamente.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!plan) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h3>Editar Plan</h3>
          <button onClick={onClose} className="close-button">&times;</button>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Descripción:</label>
            <input
              type="text"
              name="descripcion"
              value={formData.descripcion}
              onChange={handleChange}
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

          {errors.submit && <div className="error-message">{errors.submit}</div>}

          <div className="modal-actions">
            <button type="button" onClick={onClose} className="cancel-button">
              Cancelar
            </button>
            <button type="submit" disabled={isSubmitting} className="save-button">
              {isSubmitting ? 'Guardando...' : 'Guardar Cambios'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditPlanModal;