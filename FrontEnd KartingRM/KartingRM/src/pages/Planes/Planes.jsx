import React from 'react';
import PlanesList from '../../components/planes/PlanesList';
import './Planes.css';

const Planes = () => {
  return (
    <div className="planes-page">
      <h1>Administración de Planes</h1>
      <PlanesList />
    </div>
  );
};

export default Planes;