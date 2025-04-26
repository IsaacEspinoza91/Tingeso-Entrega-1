import React from 'react';
import CalendarioSemanal from '../../components/calendario/CalendarioSemanal';
import './Calendario.css';

const Calendario = () => {
  return (
    <div className="calendario-page">
      <h1>Calendario de Reservas</h1>
      <CalendarioSemanal />
    </div>
  );
};

export default Calendario;