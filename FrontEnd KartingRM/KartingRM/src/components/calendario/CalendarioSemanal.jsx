import React, { useState, useEffect } from 'react';
import { getReservasSemana } from '../../services/calendarioService';
import DiaCalendario from './DiaCalendario';
import './CalendarioSemanal.css';

const diasSemana = [
  { key: 'lunes', nombre: 'Lunes' },
  { key: 'martes', nombre: 'Martes' },
  { key: 'miércoles', nombre: 'Miércoles' },
  { key: 'jueves', nombre: 'Jueves' },
  { key: 'viernes', nombre: 'Viernes' },
  { key: 'sábado', nombre: 'Sábado' },
  { key: 'domingo', nombre: 'Domingo' }
];

const CalendarioSemanal = () => {
  const [semanaOffset, setSemanaOffset] = useState(0);
  const [reservasSemana, setReservasSemana] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchReservas = async (offset) => {
    setLoading(true);
    setError(null);
    try {
      const data = await getReservasSemana(offset);
      setReservasSemana(data);
    } catch (err) {
      setError('Error al cargar las reservas. Por favor intente nuevamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReservas(semanaOffset);
  }, [semanaOffset]);

  const handleSemanaAnterior = () => {
    setSemanaOffset(prev => prev - 1);
  };

  const handleSemanaSiguiente = () => {
    setSemanaOffset(prev => prev + 1);
  };

  const handleHoy = () => {
    setSemanaOffset(0);
  };

  if (loading && !reservasSemana) return <div className="loading">Cargando calendario...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="calendario-semanal">
      <div className="calendario-header">
        <div className="rango-semana">
          {reservasSemana && (
            <>
              {new Date(reservasSemana.fechaInicioSemana).toLocaleDateString('es-CL', { 
                day: 'numeric', 
                month: 'long', 
                year: 'numeric' 
              })} - 
              {new Date(reservasSemana.fechaFinSemana).toLocaleDateString('es-CL', { 
                day: 'numeric', 
                month: 'long', 
                year: 'numeric' 
              })}
            </>
          )}
        </div>
        
        <div className="controles-navegacion">
          <button onClick={handleSemanaAnterior} disabled={loading}>
            &lt; Semana anterior
          </button>
          <button onClick={handleHoy} disabled={loading || semanaOffset === 0}>
            Actual
          </button>
          <button onClick={handleSemanaSiguiente} disabled={loading}>
            Semana siguiente &gt;
          </button>
        </div>
      </div>

      <div className="grid-calendario">
        {diasSemana.map(dia => {
          // Calcular la fecha para cada elemento día
          let fechaDia = null;
          if (reservasSemana && reservasSemana.fechaInicioSemana) {
            const fechaInicio = new Date(reservasSemana.fechaInicioSemana);
            const diaIndex = diasSemana.findIndex(d => d.key === dia.key);
            fechaDia = new Date(fechaInicio);
            fechaDia.setDate(fechaInicio.getDate() + diaIndex);
          }

          return (
            <DiaCalendario
              key={dia.key}
              nombreDia={dia.nombre}
              reservas={reservasSemana?.reservasPorDia[dia.key] || []}
              fecha={fechaDia}
            />
          );
        })}
      </div>
    </div>
  );
};

export default CalendarioSemanal;