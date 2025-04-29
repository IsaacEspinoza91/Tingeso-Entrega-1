import React, { useState, useEffect } from 'react';
import { getClientes, deleteCliente } from '../../services/clienteService';
import CreateClienteForm from './CreateClienteForm';
import ClienteSearch from './ClienteSearch';
import EditClienteModal from './EditClienteModal';
import DeleteConfirmationModal from './DeleteConfirmationModal';
import './ClientesList.css';

const ClientesList = () => {
  const [clientes, setClientes] = useState([]);
  const [filteredClientes, setFilteredClientes] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingCliente, setEditingCliente] = useState(null);
  const [deletingCliente, setDeletingCliente] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const fetchClientes = async () => {
    try {
      const data = await getClientes();
      setClientes(data);
      setLoading(false);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchClientes();
  }, []);

  const handleClienteCreated = () => {
    fetchClientes();
    setShowCreateForm(false);
  };

  const handleSearchResults = (results) => {
    setFilteredClientes(results);
  };

  const handleUpdateCliente = (updatedCliente) => {
    setClientes(clientes.map(c => 
      c.id === updatedCliente.id ? updatedCliente : c
    ));
    
    if (filteredClientes) {
      setFilteredClientes(filteredClientes.map(c => 
        c.id === updatedCliente.id ? updatedCliente : c
      ));
    }
  };

  const handleDeleteCliente = async (id) => {
    setIsDeleting(true);
    try {
      await deleteCliente(id);    // Llama a la api
      setClientes(clientes.filter(c => c.id !== id)); // Actualiza el estado local
      
      if (filteredClientes) {
        setFilteredClientes(filteredClientes.filter(c => c.id !== id));
      }
      
      setDeletingCliente(null);   // Cierra modal de confiramcion
    } catch (error) {
      console.error('Error al eliminar cliente:', error);
    } finally {
      setIsDeleting(false);
    }
  };

  const displayedClientes = filteredClientes || clientes;

  if (loading) return <div className="loading">Cargando clientes...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="clientes-container">
      <div className="clientes-header">
        <h2>Lista de Clientes</h2>
        <button 
          onClick={() => setShowCreateForm(!showCreateForm)}
          className="create-button"
        >
          {showCreateForm ? 'Cancelar' : 'Crear Nuevo Cliente'}
        </button>
      </div>

      <ClienteSearch onSearchResults={handleSearchResults} />

      {showCreateForm && (
        <CreateClienteForm onClienteCreated={handleClienteCreated} />
      )}

      {editingCliente && (
        <EditClienteModal 
          cliente={editingCliente}
          onClose={() => setEditingCliente(null)}
          onUpdate={handleUpdateCliente}
        />
      )}

      {deletingCliente && (
        <DeleteConfirmationModal 
          item={deletingCliente}
          itemType="cliente"
          onClose={() => setDeletingCliente(null)}
          onConfirm={handleDeleteCliente}
          isDeleting={isDeleting}
        />
      )}

      <table className="clientes-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>RUT</th>
            <th>Nombre</th>
            <th>Apellido</th>
            <th>Correo</th>
            <th>Teléfono</th>
            <th>Fecha Nacimiento</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {displayedClientes.length > 0 ? (
            displayedClientes.map((cliente) => (
              <tr key={cliente.id}>
                <td>{cliente.id}</td>
                <td>{cliente.rut}</td>
                <td>{cliente.nombre}</td>
                <td>{cliente.apellido}</td>
                <td>{cliente.correo}</td>
                <td>{cliente.telefono}</td>
                <td>{new Date(cliente.fechaNacimiento+"T00:00:00").toLocaleDateString()}</td>
                <td className="actions-cell">
                  <button 
                    onClick={() => setEditingCliente(cliente)}
                    className="edit-button"
                  >
                    Editar
                  </button>
                  <button 
                    onClick={() => setDeletingCliente(cliente)}
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
              <td colSpan="8" className="no-results">
                {filteredClientes ? 'No se encontraron clientes' : 'No hay clientes registrados'}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default ClientesList;