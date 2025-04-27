import React, { useState, useEffect } from 'react';
import { getPlanes, deletePlan } from '../../services/planService';
import CreatePlanForm from './CreatePlanForm';
import PlanSearch from './PlanSearch';
import EditPlanModal from './EditPlanModal';
import DeleteConfirmationModal from './DeleteConfirmationModal';
import './PlanesList.css';

const PlanesList = () => {
  const [planes, setPlanes] = useState([]);
  const [filteredPlanes, setFilteredPlanes] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingPlan, setEditingPlan] = useState(null);
  const [deletingPlan, setDeletingPlan] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const fetchPlanes = async () => {
    try {
      const data = await getPlanes();
      setPlanes(data);
      setLoading(false);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPlanes();
  }, []);

  const handlePlanCreated = () => {
    fetchPlanes();
    setShowCreateForm(false);
  };

  const handleSearchResults = (results) => {
    setFilteredPlanes(results);
  };

  const handleUpdatePlan = (updatedPlan) => {
    setPlanes(planes.map(p => 
      p.idPlan === updatedPlan.idPlan ? updatedPlan : p
    ));
    
    if (filteredPlanes) {
      setFilteredPlanes(filteredPlanes.map(p => 
        p.idPlan === updatedPlan.idPlan ? updatedPlan : p
      ));
    }
  };

  const handleDeletePlan = async (idPlan) => {
    setIsDeleting(true);
    try {
      await deletePlan(idPlan);
      setPlanes(planes.filter(p => p.idPlan !== idPlan));
      
      if (filteredPlanes) {
        setFilteredPlanes(filteredPlanes.filter(p => p.idPlan !== idPlan));
      }
      
      setDeletingPlan(null);
    } catch (error) {
      console.error('Error al eliminar plan:', error);
    } finally {
      setIsDeleting(false);
    }
  };

  const displayedPlanes = filteredPlanes || planes;

  if (loading) return <div className="loading">Cargando planes...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="planes-container">
      <div className="planes-header">
        <h2>Lista de Planes</h2>
        <button 
          onClick={() => setShowCreateForm(!showCreateForm)}
          className="create-button"
        >
          {showCreateForm ? 'Cancelar' : 'Crear Nuevo Plan'}
        </button>
      </div>

      <PlanSearch onSearchResults={handleSearchResults} />

      {showCreateForm && (
        <CreatePlanForm onPlanCreated={handlePlanCreated} />
      )}

      {editingPlan && (
        <EditPlanModal 
          plan={editingPlan}
          onClose={() => setEditingPlan(null)}
          onUpdate={handleUpdatePlan}
        />
      )}

      {deletingPlan && (
        <DeleteConfirmationModal 
          item={deletingPlan}
          itemType="plan"
          onClose={() => setDeletingPlan(null)}
          onConfirm={handleDeletePlan}
        />
      )}

      <table className="planes-table">
        <thead>
          <tr>
            <th>ID Plan</th>
            <th>Descripción</th>
            <th>Duración Total (min)</th>
            <th>Precio Regular</th>
            <th>Precio Fin de Semana</th>
            <th>Precio Feriado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {displayedPlanes.length > 0 ? (
            displayedPlanes.map((plan) => (
              <tr key={plan.idPlan}>
                <td>{plan.idPlan}</td>
                <td>{plan.descripcion}</td>
                <td>{plan.duracionTotal}</td>
                <td>${plan.precioRegular.toLocaleString()}</td>
                <td>${plan.precioFinSemana.toLocaleString()}</td>
                <td>${plan.precioFeriado.toLocaleString()}</td>
                <td className="actions-cell">
                  <button 
                    onClick={() => setEditingPlan(plan)}
                    className="edit-button"
                  >
                    Editar
                  </button>
                  <button 
                    onClick={() => setDeletingPlan(plan)}
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
              <td colSpan="7" className="no-results">
                {filteredPlanes ? 'No se encontraron planes' : 'No hay planes registrados'}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default PlanesList;