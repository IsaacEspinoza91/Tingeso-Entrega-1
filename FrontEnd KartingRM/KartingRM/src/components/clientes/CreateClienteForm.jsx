import React, { useState } from 'react';
import { createCliente } from '../../services/clienteService';
import './CreateClienteForm.css';

const CreateClienteForm = ({ onClienteCreated }) => {
  const [formData, setFormData] = useState({
    rut: '',
    nombre: '',
    apellido: '',
    correo: '',
    telefono: '',
    fechaNacimiento: ''
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
    
    if (!formData.rut) newErrors.rut = 'RUT es requerido';
    if (!formData.nombre) newErrors.nombre = 'Nombre es requerido';
    if (!formData.apellido) newErrors.apellido = 'Apellido es requerido';
    if (!formData.correo) {
      newErrors.correo = 'Correo es requerido';
    } else if (!/^\S+@\S+\.\S+$/.test(formData.correo)) {
      newErrors.correo = 'Correo no válido';
    }
    if (!formData.telefono) newErrors.telefono = 'Teléfono es requerido';
    if (!formData.fechaNacimiento) newErrors.fechaNacimiento = 'Fecha de nacimiento es requerida';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setIsSubmitting(true);
    try {
      await createCliente(formData);
      setSuccessMessage('Cliente creado exitosamente!');
      setFormData({
        rut: '',
        nombre: '',
        apellido: '',
        correo: '',
        telefono: '',
        fechaNacimiento: ''
      });
      
      // Actualizar la lista de clientes
      if (onClienteCreated) {
        onClienteCreated();
      }
      
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Error al crear cliente:', error);
      setErrors({ submit: 'Error al crear cliente. Por favor intente nuevamente.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="create-cliente-form">
      <h3>Crear Nuevo Cliente</h3>
      {successMessage && <div className="success-message">{successMessage}</div>}
      {errors.submit && <div className="error-message">{errors.submit}</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>RUT:</label>
          <input
            type="text"
            name="rut"
            value={formData.rut}
            onChange={handleChange}
            placeholder="Ej: 12.345.678-9"
          />
          {errors.rut && <span className="error">{errors.rut}</span>}
        </div>

        <div className="form-group">
          <label>Nombre:</label>
          <input
            type="text"
            name="nombre"
            value={formData.nombre}
            onChange={handleChange}
          />
          {errors.nombre && <span className="error">{errors.nombre}</span>}
        </div>

        <div className="form-group">
          <label>Apellido:</label>
          <input
            type="text"
            name="apellido"
            value={formData.apellido}
            onChange={handleChange}
          />
          {errors.apellido && <span className="error">{errors.apellido}</span>}
        </div>

        <div className="form-group">
          <label>Correo:</label>
          <input
            type="email"
            name="correo"
            value={formData.correo}
            onChange={handleChange}
            placeholder="ejemplo@dominio.com"
          />
          {errors.correo && <span className="error">{errors.correo}</span>}
        </div>

        <div className="form-group">
          <label>Teléfono:</label>
          <input
            type="text"
            name="telefono"
            value={formData.telefono}
            onChange={handleChange}
          />
          {errors.telefono && <span className="error">{errors.telefono}</span>}
        </div>

        <div className="form-group">
          <label>Fecha de Nacimiento:</label>
          <input
            type="date"
            name="fechaNacimiento"
            value={formData.fechaNacimiento}
            onChange={handleChange}
          />
          {errors.fechaNacimiento && <span className="error">{errors.fechaNacimiento}</span>}
        </div>

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Creando...' : 'Crear Cliente'}
        </button>
      </form>
    </div>
  );
};

export default CreateClienteForm;