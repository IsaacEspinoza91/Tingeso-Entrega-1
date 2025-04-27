import React, { useState, useEffect } from 'react';
import { updateComprobante } from '../../services/comprobanteService';
import './EditComprobanteModal.css';

const EditComprobanteModal = ({ comprobante, onClose, onUpdate }) => {
  const [formData, setFormData] = useState({
    pagado: false,
    total: 0
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (comprobante) {
      setFormData({
        pagado: comprobante.pagado || false,
        total: comprobante.total || 0
      });
    }
  }, [comprobante]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : Number(value)
    });
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (formData.total < 0) newErrors.total = 'Total debe ser mayor o igual a 0';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setIsSubmitting(true);
    try {
      const updatedComprobante = await updateComprobante(comprobante.idComprobante, formData);
      onUpdate(updatedComprobante);
      onClose();
    } catch (error) {
      console.error('Error al actualizar comprobante:', error);
      setErrors({ submit: 'Error al actualizar comprobante. Por favor intente nuevamente.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!comprobante) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h3>Editar Comprobante #{comprobante.idComprobante}</h3>
          <button onClick={onClose} className="close-button">&times;</button>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>
              <input
                type="checkbox"
                name="pagado"
                checked={formData.pagado}
                onChange={handleChange}
              />
              Pagado
            </label>
          </div>

          <div className="form-group">
            <label>Total:</label>
            <input
              type="number"
              name="total"
              value={formData.total}
              onChange={handleChange}
              min="0"
              step="0.01"
            />
            {errors.total && <span className="error">{errors.total}</span>}
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

export default EditComprobanteModal;