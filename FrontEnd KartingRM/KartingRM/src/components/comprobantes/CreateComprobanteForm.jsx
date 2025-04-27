import React, { useState } from 'react';
import { createComprobante } from '../../services/comprobanteService';
import './CreateComprobanteForm.css';

const CreateComprobanteForm = ({ onComprobanteCreated }) => {
  const [formData, setFormData] = useState({
    idReserva: '',
    feriado: false,
    descuentoExtra: 0
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value
    });
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.idReserva) newErrors.idReserva = 'ID de Reserva es requerido';
    if (formData.descuentoExtra < 0) newErrors.descuentoExtra = 'Descuento no puede ser negativo';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setIsSubmitting(true);
    try {
      await createComprobante(
        formData.idReserva,
        formData.feriado,
        formData.descuentoExtra
      );
      setSuccessMessage('Comprobante creado exitosamente!');
      setFormData({
        idReserva: '',
        feriado: false,
        descuentoExtra: 0
      });
      
      if (onComprobanteCreated) {
        onComprobanteCreated();
      }
      
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Error al crear comprobante:', error);
      setErrors({ submit: 'Error al crear comprobante. Verifique el ID de reserva.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="create-comprobante-form">
      <h3>Crear Nuevo Comprobante</h3>
      {successMessage && <div className="success-message">{successMessage}</div>}
      {errors.submit && <div className="error-message">{errors.submit}</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>ID de Reserva:</label>
          <input
            type="number"
            name="idReserva"
            value={formData.idReserva}
            onChange={handleChange}
            min="1"
          />
          {errors.idReserva && <span className="error">{errors.idReserva}</span>}
        </div>

        <div className="form-group">
          <label>
            <input
              type="checkbox"
              name="feriado"
              checked={formData.feriado}
              onChange={handleChange}
            />
            ¿Es feriado?
          </label>
        </div>

        <div className="form-group">
          <label>Descuento Extra:</label>
          <input
            type="number"
            name="descuentoExtra"
            value={formData.descuentoExtra}
            onChange={handleChange}
            min="0"
          />
          {errors.descuentoExtra && <span className="error">{errors.descuentoExtra}</span>}
        </div>

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Creando...' : 'Crear Comprobante'}
        </button>
      </form>
    </div>
  );
};

export default CreateComprobanteForm;