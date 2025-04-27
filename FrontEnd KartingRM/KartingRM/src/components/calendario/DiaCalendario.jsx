import React from 'react';
import './DiaCalendario.css';

const DiaCalendario = ({ nombreDia, reservas, fecha }) => {
  const fechaFormateada = fecha   // Formatear la fecha
    ? new Date(fecha).toLocaleDateString('es-CL', { 
        day: 'numeric', 
        month: 'long' 
      })
    : '';

  return (
    <div className="dia-calendario">
      <div className="dia-header">
        <h3>{nombreDia}</h3>
        <span className="fecha">{fechaFormateada}</span>
      </div>
      
      <div className="reservas-container">
        {reservas && reservas.length > 0 ? (
          reservas.map((reserva) => (
            <div key={reserva.codigoReserva} className="reserva">
              <div className="reserva-horario">
                {reserva.horaInicio.substring(0, 5)} - {reserva.horaFin.substring(0, 5)}
              </div>
              <div className="reserva-info">
                <strong>{reserva.nombreReservante}</strong>
                <span>Reserva {reserva.codigoReserva}</span>
              </div>
            </div>
          ))
        ) : (
          <div className="sin-reservas">No hay reservas</div>
        )}
      </div>
    </div>
  );
};

export default DiaCalendario;