import React, { useState } from 'react';
import { createKart } from '../../services/kartService';
import './CreateKartForm.css';

const CreateKartForm = ({ onKartCreated }) => {
  const [formData, setFormData] = useState({
    modelo: '',
    estado: 'Disponible'
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

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
      await createKart(formData);
      setSuccessMessage('Kart creado exitosamente!');
      setFormData({
        modelo: '',
        estado: 'Disponible'
      });
      
      if (onKartCreated) {
        onKartCreated();
      }
      
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Error al crear kart:', error);
      setErrors({ submit: 'Error al crear kart. Por favor intente nuevamente.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="create-kart-form">
      <h3>Crear Nuevo Kart</h3>
      {successMessage && <div className="success-message">{successMessage}</div>}
      {errors.submit && <div className="error-message">{errors.submit}</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Modelo:</label>
          <input
            type="text"
            name="modelo"
            value={formData.modelo}
            onChange={handleChange}
            placeholder="Ej: Sodikart RT8"
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

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Creando...' : 'Crear Kart'}
        </button>
      </form>
    </div>
  );
};

export default CreateKartForm;