import React, { useState, useEffect } from 'react';
import { getComprobantes, deleteComprobante } from '../../services/comprobanteService';
import CreateComprobanteForm from './CreateComprobanteForm';
import ComprobanteSearch from './ComprobanteSearch';
import EditComprobanteModal from './EditComprobanteModal';
import ComprobanteDetails from './ComprobanteDetails';
import DeleteConfirmationModal from './DeleteConfirmationModal';
import PDFDownloadButton from './ComprobantePDF/PDFDownloadButton';
import './ComprobantesList.css';



const ComprobantesList = () => {
  const [comprobantes, setComprobantes] = useState([]);
  const [filteredComprobantes, setFilteredComprobantes] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingComprobante, setEditingComprobante] = useState(null);
  const [deletingComprobante, setDeletingComprobante] = useState(null);
  const [viewingDetails, setViewingDetails] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const fetchComprobantes = async () => {
    try {
      const data = await getComprobantes();
      setComprobantes(data);
      setLoading(false);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchComprobantes();
  }, []);

  const handleComprobanteCreated = () => {
    fetchComprobantes();
    setShowCreateForm(false);
  };

  const handleSearchResults = (results) => {
    setFilteredComprobantes(results);
  };

  const handleUpdateComprobante = (updatedComprobante) => {
    setComprobantes(comprobantes.map(c => 
      c.idComprobante === updatedComprobante.idComprobante ? updatedComprobante : c
    ));
    
    if (filteredComprobantes) {
      setFilteredComprobantes(filteredComprobantes.map(c => 
        c.idComprobante === updatedComprobante.idComprobante ? updatedComprobante : c
      ));
    }
  };

  const handleDeleteComprobante = async (idComprobante) => {
    setIsDeleting(true);
    try {
      await deleteComprobante(idComprobante);
      setComprobantes(comprobantes.filter(c => c.idComprobante !== idComprobante));
      
      if (filteredComprobantes) {
        setFilteredComprobantes(filteredComprobantes.filter(c => c.idComprobante !== idComprobante));
      }
      
      setDeletingComprobante(null);
    } catch (error) {
      console.error('Error al eliminar comprobante:', error);
    } finally {
      setIsDeleting(false);
    }
  };

  const toggleDetails = (idComprobante) => {
    setViewingDetails(viewingDetails === idComprobante ? null : idComprobante);
  };

  const displayedComprobantes = filteredComprobantes || comprobantes;

  if (loading) return <div className="loading">Cargando comprobantes...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="comprobantes-container">
      <div className="comprobantes-header">
        <h2>Lista de Comprobantes</h2>
        <button 
          onClick={() => setShowCreateForm(!showCreateForm)}
          className="create-button"
        >
          {showCreateForm ? 'Cancelar' : 'Crear Nuevo Comprobante'}
        </button>
      </div>

      <ComprobanteSearch onSearchResults={handleSearchResults} />

      {showCreateForm && (
        <CreateComprobanteForm onComprobanteCreated={handleComprobanteCreated} />
      )}

      {editingComprobante && (
        <EditComprobanteModal 
          comprobante={editingComprobante}
          onClose={() => setEditingComprobante(null)}
          onUpdate={handleUpdateComprobante}
        />
      )}

      {deletingComprobante && (
        <DeleteConfirmationModal 
          item={deletingComprobante}
          itemType="comprobante"
          onClose={() => setDeletingComprobante(null)}
          onConfirm={handleDeleteComprobante}
        />
      )}

      <table className="comprobantes-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Pagado</th>
            <th>Total</th>
            <th>Reserva ID</th>
            <th>Fecha</th>
            <th>Hora Inicio</th>
            <th>Plan</th>
            <th>Personas</th>
            <th>Cliente</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {displayedComprobantes.length > 0 ? (
            displayedComprobantes.map((comprobante) => (
              <React.Fragment key={comprobante.idComprobante}>
                <tr>
                  <td>{comprobante.idComprobante}</td>
                  <td>
                    <span className={`status-badge ${comprobante.pagado ? 'pagado' : 'pendiente'}`}>
                      {comprobante.pagado ? 'Sí' : 'No'}
                    </span>
                  </td>
                  <td>${comprobante.total.toLocaleString()}</td>
                  <td>{comprobante.reserva.idReserva}</td>
                  <td>{new Date(comprobante.reserva.fecha+"T00:00:00").toLocaleDateString()}</td>
                  <td>{comprobante.reserva.horaInicio.substring(0, 5)}</td>
                  <td>{comprobante.reserva.plan.descripcion}</td>
                  <td>{comprobante.reserva.totalPersonas}</td>
                  <td>
                    {comprobante.reserva.reservante.nombre} {comprobante.reserva.reservante.apellido}
                  </td>
                  <td className="actions-cell">
                    <button 
                      onClick={() => toggleDetails(comprobante.idComprobante)}
                      className="details-button"
                    >
                      {viewingDetails === comprobante.idComprobante ? 'Ocultar' : 'Ver'} Detalles
                    </button>
                    <PDFDownloadButton comprobante={comprobante} />
                    <button 
                      onClick={() => setEditingComprobante(comprobante)}
                      className="edit-button"
                    >
                      Editar
                    </button>
                    <button 
                      onClick={() => setDeletingComprobante(comprobante)}
                      className="delete-button"
                      disabled={isDeleting}
                    >
                      Eliminar
                    </button>
                  </td>
                </tr>
                {viewingDetails === comprobante.idComprobante && (
                  <tr>
                    <td colSpan="10">
                      <ComprobanteDetails 
                        comprobante={comprobante} 
                        onUpdate={handleUpdateComprobante}
                      />
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))
          ) : (
            <tr>
              <td colSpan="10" className="no-results">
                {filteredComprobantes ? 'No se encontraron comprobantes' : 'No hay comprobantes registrados'}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default ComprobantesList;