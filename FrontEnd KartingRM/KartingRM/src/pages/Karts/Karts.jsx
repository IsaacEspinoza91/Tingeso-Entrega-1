import React from 'react';
import KartsList from '../../components/karts/KartsList';
import './Karts.css';

const Karts = () => {
  return (
    <div className="karts-page">
      <h1>Administración de Karts</h1>
      <KartsList />
    </div>
  );
};

export default Karts;