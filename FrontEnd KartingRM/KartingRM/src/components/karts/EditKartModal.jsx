import React, { useState, useEffect } from 'react';
import { updateKart } from '../../services/kartService';
import './EditKartModal.css';

const EditKartModal = ({ kart, onClose, onUpdate }) => {
  const [formData, setFormData] = useState({
    modelo: '',
    estado: 'Disponible'
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (kart) {
      setFormData({
        modelo: kart.modelo || '',
        estado: kart.estado || 'Disponible'
      });
    }
  }, [kart]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.modelo.trim()) newErrors.modelo = 'Modelo es requerido';
    if (!formData.estado) newErrors.estado = 'Estado es requerido';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setIsSubmitting(true);
    try {
      const updatedKart = await updateKart(kart.idkart, formData);
      onUpdate(updatedKart);
      onClose();
    } catch (error) {
      console.error('Error al actualizar kart:', error);
      setErrors({ submit: 'Error al actualizar kart. Por favor intente nuevamente.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!kart) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h3>Editar Kart</h3>
          <button onClick={onClose} className="close-button">&times;</button>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Modelo:</label>
            <input
              type="text"
              name="modelo"
              value={formData.modelo}
              onChange={handleChange}
            />
            {errors.modelo && <span className="error">{errors.modelo}</span>}
          </div>

          <div className="form-group">
            <label>Estado:</label>
            <select
              name="estado"
              value={formData.estado}
              onChange={handleChange}
            >
              <option value="Disponible">Disponible</option>
              <option value="No Disponible">No Disponible</option>
            </select>
            {errors.estado && <span className="error">{errors.estado}</span>}
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

export default EditKartModal;