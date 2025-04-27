import React, { useState } from 'react';
import { addIntegrante, removeIntegrante } from '../../services/reservaService';
import './IntegrantesModal.css';

const IntegrantesModal = ({ reserva, onClose, onUpdate }) => {
  const [newIntegranteId, setNewIntegranteId] = useState('');
  const [isAdding, setIsAdding] = useState(false);
  const [isRemoving, setIsRemoving] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [currentIntegrantes, setCurrentIntegrantes] = useState(reserva.integrantes);

  const handleAddIntegrante = async () => {
    if (!newIntegranteId) {
      setError('Por favor ingrese un ID de cliente');
      return;
    }

    setIsAdding(true);
    setError('');
    setSuccessMessage('');

    try {
      const updatedReserva = await addIntegrante(reserva.idReserva, newIntegranteId);
      setCurrentIntegrantes(updatedReserva.integrantes);
      onUpdate(updatedReserva);
      setNewIntegranteId('');
      setSuccessMessage('Integrante agregado correctamente');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err) {
      const errorMsg = err.response?.data?.message || 'Error al agregar integrante';
      setError(errorMsg);
    } finally {
      setIsAdding(false);
    }
  };

  const handleRemoveIntegrante = async (idCliente) => {
    setIsRemoving(true);
    setError('');
    setSuccessMessage('');

    try {
      const updatedReserva = await removeIntegrante(reserva.idReserva, idCliente);
      setCurrentIntegrantes(updatedReserva.integrantes);
      onUpdate(updatedReserva);
      setSuccessMessage('Integrante eliminado correctamente');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err) {
      const errorMsg = err.response?.data?.message || 'Error al quitar integrante';
      setError(errorMsg);
    } finally {
      setIsRemoving(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="integrantes-modal">
        <div className="modal-header">
          <h3>Integrantes de la Reserva #{reserva.idReserva}</h3>
          <button onClick={onClose} className="close-button">&times;</button>
        </div>
        
        <div className="integrantes-container">
          <div className="add-integrante">
            <input
              type="number"
              value={newIntegranteId}
              onChange={(e) => setNewIntegranteId(e.target.value)}
              placeholder="ID del cliente a agregar"
              min="1"
            />
            <button 
              onClick={handleAddIntegrante}
              disabled={isAdding}
              className="add-button"
            >
              {isAdding ? 'Agregando...' : 'Agregar'}
            </button>
          </div>

          {error && <div className="error-message">{error}</div>}
          {successMessage && <div className="success-message">{successMessage}</div>}

          <h4>Integrantes actuales ({currentIntegrantes.length}):</h4>
          <div className="table-container">
            <table className="integrantes-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>RUT</th>
                  <th>Correo</th>
                  <th>Teléfono</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                {currentIntegrantes.map((integrante) => (
                  <tr key={integrante.id}>
                    <td>{integrante.id}</td>
                    <td>{integrante.nombre} {integrante.apellido}</td>
                    <td>{integrante.rut}</td>
                    <td>{integrante.correo}</td>
                    <td>{integrante.telefono}</td>
                    <td>
                      <button
                        onClick={() => handleRemoveIntegrante(integrante.id)}
                        disabled={isRemoving}
                        className="remove-button"
                      >
                        {isRemoving ? 'Quitando...' : 'Quitar'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="modal-actions">
          <button type="button" onClick={onClose} className="close-modal-button">
            Cerrar
          </button>
        </div>
      </div>
    </div>
  );
};

export default IntegrantesModal;