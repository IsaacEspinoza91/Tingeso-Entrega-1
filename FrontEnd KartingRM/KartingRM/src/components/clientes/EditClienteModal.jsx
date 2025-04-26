import React, { useState, useEffect } from 'react';
import { updateCliente } from '../../services/clienteService';
import './EditClienteModal.css';

const EditClienteModal = ({ cliente, onClose, onUpdate }) => {
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

  useEffect(() => {
    if (cliente) {
      setFormData({
        rut: cliente.rut || '',
        nombre: cliente.nombre || '',
        apellido: cliente.apellido || '',
        correo: cliente.correo || '',
        telefono: cliente.telefono || '',
        fechaNacimiento: cliente.fechaNacimiento ? cliente.fechaNacimiento.split('T')[0] : ''
      });
    }
  }, [cliente]);

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
      const updatedCliente = await updateCliente(cliente.id, formData);
      onUpdate(updatedCliente);
      onClose();
    } catch (error) {
      console.error('Error al actualizar cliente:', error);
      setErrors({ submit: 'Error al actualizar cliente. Por favor intente nuevamente.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!cliente) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h3>Editar Cliente</h3>
          <button onClick={onClose} className="close-button">&times;</button>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>RUT:</label>
            <input
              type="text"
              name="rut"
              value={formData.rut}
              onChange={handleChange}
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

export default EditClienteModal;