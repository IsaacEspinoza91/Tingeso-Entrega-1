import React from 'react';
import ComprobantesList from '../../components/comprobantes/ComprobantesList';
import './Comprobantes.css';

const Comprobantes = () => {
  return (
    <div className="comprobantes-page">
      <h1>Administración de Comprobantes</h1>
      <ComprobantesList />
    </div>
  );
};

export default Comprobantes;