import React, { useState } from 'react';
import { addIntegrante } from '../../services/reservaService';

const AddIntegranteModal = ({ reserva, onClose, onIntegranteAdded }) => {
  const [idCliente, setIdCliente] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!idCliente) {
      setError('Por favor ingrese un ID de cliente');
      return;
    }

    setIsSubmitting(true);
    setError(null);
    try {
      const updatedReserva = await addIntegrante(reserva.idReserva, idCliente);
      onIntegranteAdded(updatedReserva);
    } catch (err) {
      setError('Error al agregar integrante. Verifique el ID del cliente.');
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!reserva) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content add-integrante-modal">
        <div className="modal-header">
          <h3>Agregar Integrante a Reserva #{reserva.idReserva}</h3>
          <button onClick={onClose} className="close-button">&times;</button>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>ID del Cliente a agregar:</label>
            <input
              type="number"
              value={idCliente}
              onChange={(e) => setIdCliente(e.target.value)}
              min="1"
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <div className="modal-actions">
            <button type="button" onClick={onClose} className="cancel-button">
              Cancelar
            </button>
            <button type="submit" disabled={isSubmitting} className="save-button">
              {isSubmitting ? 'Agregando...' : 'Agregar Integrante'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddIntegranteModal;