import React from 'react';
import ReservasList from '../../components/reservas/ReservasList';
import './Reservas.css';

const Reservas = () => {
  return (
    <div className="reservas-page">
      <h1>Administración de Reservas</h1>
      <ReservasList />
    </div>
  );
};

export default Reservas;