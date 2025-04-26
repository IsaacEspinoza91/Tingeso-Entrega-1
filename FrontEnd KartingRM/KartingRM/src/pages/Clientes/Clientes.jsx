import React from 'react';
import ClientesList from '../../components/clientes/ClientesList';
import './Clientes.css';

const Clientes = () => {
  return (
    <div className="clientes-page">
      <h1>Administración de Clientes</h1>
      <ClientesList />
    </div>
  );
};

export default Clientes;