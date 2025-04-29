import React, { useState, useEffect } from 'react';
import { 
  getDetallesByComprobante,
  deleteDetalle
} from '../../services/comprobanteService';
import EditDetalleModal from './EditDetalleModal';
import DeleteConfirmationModal from './DeleteConfirmationModal';
import './ComprobanteDetails.css';

const ComprobanteDetails = ({ comprobante, onUpdate }) => {
  const [detalles, setDetalles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingDetalle, setEditingDetalle] = useState(null);
  const [deletingDetalle, setDeletingDetalle] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const fetchDetalles = async () => {
    try {
      const data = await getDetallesByComprobante(comprobante.idComprobante);
      setDetalles(data);
      comprobante.detalles = data; // Agregar detalles al comprobante PDF
      setLoading(false);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDetalles();
  }, [comprobante.idComprobante]);

  const handleUpdateDetalle = (updatedDetalle) => {
    setDetalles(detalles.map(d => 
      d.idDetalle === updatedDetalle.idDetalle ? updatedDetalle : d
    ));
    onUpdate({ ...comprobante }); // Actualizar el comprobante padre si es necesario
  };

  const handleDeleteDetalle = async (idDetalle) => {
    setIsDeleting(true);
    try {
      await deleteDetalle(idDetalle);
      setDetalles(detalles.filter(d => d.idDetalle !== idDetalle));
      setDeletingDetalle(null);
      onUpdate({ ...comprobante }); // Actualizar el comprobante padre
    } catch (error) {
      console.error('Error al eliminar detalle:', error);
    } finally {
      setIsDeleting(false);
    }
  };

  if (loading) return <div className="loading">Cargando detalles...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="comprobante-details">
      <h3>Detalles del Comprobante #{comprobante.idComprobante}</h3>
      
      {editingDetalle && (
        <EditDetalleModal 
          detalle={editingDetalle}
          onClose={() => setEditingDetalle(null)}
          onUpdate={handleUpdateDetalle}
        />
      )}

      {deletingDetalle && (
        <DeleteConfirmationModal 
          item={deletingDetalle}
          itemType="detalle"
          onClose={() => setDeletingDetalle(null)}
          onConfirm={handleDeleteDetalle}
        />
      )}

      <table className="detalles-table">
        <thead>
          <tr>
            <th>Cliente</th>
            <th>Tarifa</th>
            <th>% D.G</th>
            <th>Desc. Grupo</th>
            <th>% D.E</th>
            <th>Desc. Especial</th>
            <th>Desc. Extra</th>
            <th>Monto Final</th>
            <th>IVA</th>
            <th>Total</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {detalles.length > 0 ? (
            detalles.map((detalle) => (
              <tr key={detalle.idDetalle}>
                <td>
                  {detalle.cliente.nombre} {detalle.cliente.apellido}
                  <br />
                  <small>(ID: {detalle.cliente.id})</small>
                </td>
                <td>${detalle.tarifa.toLocaleString()}</td>
                <td>{detalle.porcentajeDescuentoGrupo}%</td>
                <td>${detalle.descuentoGrupo.toLocaleString()}</td>
                <td>{detalle.porcentajeDescuentoEspecial}%</td>
                <td>${detalle.descuentoEspecial.toLocaleString()}</td>
                <td>${detalle.descuentoExtra.toLocaleString()}</td>
                <td>${detalle.montoFinal.toLocaleString()}</td>
                <td>${detalle.montoIva.toLocaleString()}</td>
                <td>${detalle.montoTotal.toLocaleString()}</td>
                <td className="actions-cell">
                  <button 
                    onClick={() => setEditingDetalle(detalle)}
                    className="edit-button"
                  >
                    Editar
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="9" className="no-results">
                No hay detalles registrados para este comprobante
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default ComprobanteDetails;