import React, { useState, useEffect } from 'react';
import { getKarts, deleteKart } from '../../services/kartService';
import CreateKartForm from './CreateKartForm';
import KartSearch from './KartSearch';
import EditKartModal from './EditKartModal';
import DeleteConfirmationModal from './DeleteConfirmationModal';
import './KartsList.css';

const KartsList = () => {
  const [karts, setKarts] = useState([]);
  const [filteredKarts, setFilteredKarts] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingKart, setEditingKart] = useState(null);
  const [deletingKart, setDeletingKart] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const fetchKarts = async () => {
    try {
      const data = await getKarts();
      setKarts(data);
      setLoading(false);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchKarts();
  }, []);

  const handleKartCreated = () => {
    fetchKarts();
    setShowCreateForm(false);
  };

  const handleSearchResults = (results) => {
    setFilteredKarts(results);
  };

  const handleUpdateKart = (updatedKart) => {
    setKarts(karts.map(k => 
      k.idkart === updatedKart.idkart ? updatedKart : k
    ));
    
    if (filteredKarts) {
      setFilteredKarts(filteredKarts.map(k => 
        k.idkart === updatedKart.idkart ? updatedKart : k
      ));
    }
  };

  const handleDeleteKart = async (idkart) => {
    setIsDeleting(true);
    try {
      await deleteKart(idkart);
      setKarts(karts.filter(k => k.idkart !== idkart));
      
      if (filteredKarts) {
        setFilteredKarts(filteredKarts.filter(k => k.idkart !== idkart));
      }
      
      setDeletingKart(null);
    } catch (error) {
      console.error('Error al eliminar kart:', error);
    } finally {
      setIsDeleting(false);
    }
  };

  const displayedKarts = filteredKarts || karts;

  if (loading) return <div className="loading">Cargando karts...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="karts-container">
      <div className="karts-header">
        <h2>Lista de Karts</h2>
        <button 
          onClick={() => setShowCreateForm(!showCreateForm)}
          className="create-button"
        >
          {showCreateForm ? 'Cancelar' : 'Crear Nuevo Kart'}
        </button>
      </div>

      <KartSearch onSearchResults={handleSearchResults} />

      {showCreateForm && (
        <CreateKartForm onKartCreated={handleKartCreated} />
      )}

      {editingKart && (
        <EditKartModal 
          kart={editingKart}
          onClose={() => setEditingKart(null)}
          onUpdate={handleUpdateKart}
        />
      )}

      {deletingKart && (
        <DeleteConfirmationModal 
          item={deletingKart}
          itemType="kart"
          onClose={() => setDeletingKart(null)}
          onConfirm={handleDeleteKart}
        />
      )}

      <table className="karts-table">
        <thead>
          <tr>
            <th>ID Kart</th>
            <th>Modelo</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {displayedKarts.length > 0 ? (
            displayedKarts.map((kart) => (
              <tr key={kart.idkart}>
                <td>{kart.idkart}</td>
                <td>{kart.modelo}</td>
                <td>
                  <span className={`status-badge ${kart.estado.toLowerCase()}`}>
                    {kart.estado}
                  </span>
                </td>
                <td className="actions-cell">
                  <button 
                    onClick={() => setEditingKart(kart)}
                    className="edit-button"
                  >
                    Editar
                  </button>
                  <button 
                    onClick={() => setDeletingKart(kart)}
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
              <td colSpan="4" className="no-results">
                {filteredKarts ? 'No se encontraron karts' : 'No hay karts registrados'}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default KartsList;