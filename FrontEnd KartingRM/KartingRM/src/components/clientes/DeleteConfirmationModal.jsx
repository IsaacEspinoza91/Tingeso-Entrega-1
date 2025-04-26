import React from 'react';
import './DeleteConfirmationModal.css';

const DeleteConfirmationModal = ({ cliente, onClose, onConfirm }) => {
  if (!cliente) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h3>Confirmar Eliminación</h3>
          <button onClick={onClose} className="close-button">&times;</button>
        </div>
        
        <div className="confirmation-message">
          ¿Estás seguro que deseas eliminar al cliente {cliente.nombre} {cliente.apellido} (RUT: {cliente.rut})?
        </div>

        <div className="modal-actions">
          <button type="button" onClick={onClose} className="cancel-button">
            Cancelar
          </button>
          <button 
            type="button" 
            onClick={() => onConfirm(cliente.id)} 
            className="delete-button"
          >
            Eliminar
          </button>
        </div>
      </div>
    </div>
  );
};

export default DeleteConfirmationModal;