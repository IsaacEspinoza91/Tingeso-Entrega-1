import React from 'react';
import './DeleteConfirmationModal.css';

const DeleteConfirmationModal = ({ item, itemType = 'comprobante', onClose, onConfirm }) => {
  if (!item) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h3>Confirmar Eliminación</h3>
          <button onClick={onClose} className="close-button">&times;</button>
        </div>
        
        <div className="confirmation-message">
          ¿Estás seguro que deseas eliminar el {itemType} {item.modelo || `ID ${item.idComprobante}`}?
        </div>

        <div className="modal-actions">
          <button type="button" onClick={onClose} className="cancel-button">
            Cancelar
          </button>
          <button 
            type="button" 
            onClick={() => onConfirm(item.idComprobante)}
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