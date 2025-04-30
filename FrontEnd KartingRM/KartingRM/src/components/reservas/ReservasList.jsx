import React, { useState, useEffect } from 'react';
import { getReservas, deleteReserva } from '../../services/reservaService';
import CreateReservaForm from './CreateReservaForm';
import ReservaSearch from './ReservaSearch';
import EditReservaModal from './EditReservaModal';
import IntegrantesModal from './IntegrantesModal';
import DeleteConfirmationModal from './DeleteConfirmationModal';
import './ReservasList.css';

const ReservasList = () => {
  const [reservas, setReservas] = useState([]);
  const [filteredReservas, setFilteredReservas] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingReserva, setEditingReserva] = useState(null);
  const [viewingIntegrantes, setViewingIntegrantes] = useState(null);
  const [deletingReserva, setDeletingReserva] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const fetchReservas = async () => {
    try {
      const data = await getReservas();
      setReservas(data);
      setLoading(false);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReservas();
  }, []);

  const handleReservaCreated = () => {
    fetchReservas();
    setShowCreateForm(false);
  };

  const handleSearchResults = (results) => {
    setFilteredReservas(results);
  };

  const handleUpdateReserva = (updatedReserva) => {
    setReservas(reservas.map(r => 
      r.idReserva === updatedReserva.idReserva ? updatedReserva : r
    ));
    
    if (filteredReservas) {
      setFilteredReservas(filteredReservas.map(r => 
        r.idReserva === updatedReserva.idReserva ? updatedReserva : r
      ));
    }
  };

  const handleDeleteReserva = async (idReserva) => {
    setIsDeleting(true);
    try {
      await deleteReserva(idReserva);
      setReservas(reservas.filter(r => r.idReserva !== idReserva));
      
      if (filteredReservas) {
        setFilteredReservas(filteredReservas.filter(r => r.idReserva !== idReserva));
      }
      
      setDeletingReserva(null);
    } catch (error) {
      console.error('Error al eliminar reserva:', error);
    } finally {
      setIsDeleting(false);
    }
  };

  const displayedReservas = filteredReservas || reservas;

  if (loading) return <div className="loading">Cargando reservas...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="reservas-container">
      <div className="reservas-header">
        <h2>Lista de Reservas</h2>
        <button 
          onClick={() => setShowCreateForm(!showCreateForm)}
          className="create-button"
        >
          {showCreateForm ? 'Cancelar' : 'CREAR RESERVA'}
        </button>
      </div>

      <ReservaSearch onSearchResults={handleSearchResults} />

      {showCreateForm && (
        <CreateReservaForm onReservaCreated={handleReservaCreated} />
      )}

      {editingReserva && (
        <EditReservaModal 
          reserva={editingReserva}
          onClose={() => setEditingReserva(null)}
          onUpdate={handleUpdateReserva}
        />
      )}

      {viewingIntegrantes && (
        <IntegrantesModal 
          reserva={viewingIntegrantes}
          onClose={() => setViewingIntegrantes(null)}
          onUpdate={handleUpdateReserva}
        />
      )}

      {deletingReserva && (
        <DeleteConfirmationModal 
          item={deletingReserva}
          itemType="reserva"
          onClose={() => setDeletingReserva(null)}
          onConfirm={handleDeleteReserva}
        />
      )}

      <table className="reservas-table">
        <thead>
          <tr>
            <th>ID Reserva</th>
            <th>Fecha</th>
            <th>Hora Inicio</th>
            <th>Hora Fin</th>
            <th>Estado</th>
            <th>Personas</th>
            <th>Plan</th>
            <th>Cliente</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {displayedReservas.length > 0 ? (
            displayedReservas.map((reserva) => (
              <tr key={reserva.idReserva}>
                <td>{reserva.idReserva}</td>
                <td>{new Date(reserva.fecha+"T00:00:00").toLocaleDateString()}</td>
                <td>{reserva.horaInicio}</td>
                <td>{reserva.horaFin || '-'}</td>
                <td>
                  <span className={`status-badge ${reserva.estado.toLowerCase()}`}>
                    {reserva.estado}
                  </span>
                </td>
                <td>{reserva.totalPersonas}</td>
                <td>
                  {reserva.plan.idPlan} - {reserva.plan.descripcion}
                </td>
                <td>
                  {reserva.reservante.id} - {reserva.reservante.nombre} {reserva.reservante.apellido}
                </td>
                <td className="actions-cell">
                  <button 
                    onClick={() => setEditingReserva(reserva)}
                    className="edit-button"
                  >
                    Editar
                  </button>
                  <button 
                    onClick={() => setViewingIntegrantes(reserva)}
                    className="view-button"
                  >
                    Integrantes
                  </button>
                  <button 
                    onClick={() => setDeletingReserva(reserva)}
                    className="delete-button"
                    disabled={isDeleting}
                  >
                    Eliminar
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="9" className="no-results">
                {filteredReservas ? 'No se encontraron reservas' : 'No hay reservas registradas'}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default ReservasList;