import React, { useState, useEffect } from 'react';
import { updateDetalle } from '../../services/comprobanteService';
import './EditDetalleModal.css';

const EditDetalleModal = ({ detalle, onClose, onUpdate }) => {
  const [formData, setFormData] = useState({
    tarifa: 0,
    descuentoGrupo: 0,
    descuentoEspecial: 0,
    descuentoExtra: 0,
    montoFinal: 0,
    montoIva: 0,
    montoTotal: 0,
    porcentajeDescuentoEspecial: 0,
    porcentajeDescuentoGrupo: 0
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (detalle) {
      setFormData({
        tarifa: detalle.tarifa || 0,
        descuentoGrupo: detalle.descuentoGrupo || 0,
        descuentoEspecial: detalle.descuentoEspecial || 0,
        descuentoExtra: detalle.descuentoExtra || 0,
        montoFinal: detalle.montoFinal || 0,
        montoIva: detalle.montoIva || 0,
        montoTotal: detalle.montoTotal || 0,
        porcentajeDescuentoEspecial: detalle.porcentajeDescuentoEspecial || 0,
        porcentajeDescuentoGrupo: detalle.porcentajeDescuentoGrupo || 0
      });
    }
  }, [detalle]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: Number(value)
    });
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (formData.tarifa < 0) newErrors.tarifa = 'Tarifa debe ser mayor o igual a 0';
    if (formData.descuentoGrupo < 0) newErrors.descuentoGrupo = 'Descuento debe ser mayor o igual a 0';
    if (formData.descuentoEspecial < 0) newErrors.descuentoEspecial = 'Descuento debe ser mayor o igual a 0';
    if (formData.descuentoExtra < 0) newErrors.descuentoExtra = 'Descuento debe ser mayor o igual a 0';
    if (formData.montoFinal < 0) newErrors.montoFinal = 'Monto debe ser mayor o igual a 0';
    if (formData.montoIva < 0) newErrors.montoIva = 'IVA debe ser mayor o igual a 0';
    if (formData.montoTotal < 0) newErrors.montoTotal = 'Total debe ser mayor o igual a 0';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setIsSubmitting(true);
    try {
      const updatedDetalle = await updateDetalle(detalle.idDetalle, formData);
      onUpdate(updatedDetalle);
      onClose();
    } catch (error) {
      console.error('Error al actualizar detalle:', error);
      setErrors({ submit: 'Error al actualizar detalle. Por favor intente nuevamente.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!detalle) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h3>Editar Detalle #{detalle.idDetalle}</h3>
          <button onClick={onClose} className="close-button">&times;</button>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Tarifa:</label>
            <input
              type="number"
              name="tarifa"
              value={formData.tarifa}
              onChange={handleChange}
              min="0"
              step="1"
            />
            {errors.tarifa && <span className="error">{errors.tarifa}</span>}
          </div>

          <div className="form-group">
            <label>Porcentaje Descuento Grupo:</label>
            <input
              type="number"
              name="descuentoGrupo"
              value={formData.porcentajeDescuentoGrupo}
              onChange={handleChange}
              min="0"
              step="1"
            />
            {errors.descuentoGrupo && <span className="error">{errors.descuentoGrupo}</span>}
          </div>

          <div className="form-group">
            <label>Porcentaje Descuento Especial:</label>
            <input
              type="number"
              name="descuentoEspecial"
              value={formData.porcentajeDescuentoEspecial}
              onChange={handleChange}
              min="0"
              step="1"
            />
            {errors.descuentoEspecial && <span className="error">{errors.descuentoEspecial}</span>}
          </div>

          <div className="form-group">
            <label>Descuento Extra:</label>
            <input
              type="number"
              name="descuentoExtra"
              value={formData.descuentoExtra}
              onChange={handleChange}
              min="0"
              step="1"
            />
            {errors.descuentoExtra && <span className="error">{errors.descuentoExtra}</span>}
          </div>

          <p>Los demás valores se calculan automáticamente</p>

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

export default EditDetalleModal;