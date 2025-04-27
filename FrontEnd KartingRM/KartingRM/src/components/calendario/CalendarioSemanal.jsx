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
  const [semana, setSemana] = useState(0);
  const [datosSemana, setDatosSemana] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const cargarReservas = async () => {
      try {
        setLoading(true);
        const data = await getReservasSemana(semana);
        setDatosSemana(data);
        setError(null);
      } catch (err) {
        setError('Error al cargar las reservas');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    cargarReservas();
  }, [semana]);


  const cambiarSemana = (valor) => {
    setSemana(valor);
  };


  if (loading && !datosSemana) return <div className="loading">Cargando calendario...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="calendario-semanal">
      <div className="calendario-header">
        <div className="rango-semana">
          {datosSemana && (
            <>
              {new Date(datosSemana.fechaInicioSemana+"T00:00:00").toLocaleDateString('es-CL', { 
                day: 'numeric', 
                month: 'long', 
                year: 'numeric' 
              })} -
              {new Date(datosSemana.fechaFinSemana+"T00:00:00").toLocaleDateString('es-CL', { 
                day: 'numeric', 
                month: 'long', 
                year: 'numeric' 
              })}
            </>
          )}
        </div>
        
        <div className="controles-navegacion">
        <button onClick={() => cambiarSemana(semana - 1)} disabled={loading}>
            &lt; Semana anterior
          </button>
          <button onClick={() => cambiarSemana(0)} disabled={loading || semana === 0}>
            Actual
          </button>
          <button onClick={() => cambiarSemana(semana + 1)} disabled={loading}>
            Semana siguiente &gt;
          </button>
        </div>
      </div>

      <div className="grid-calendario">
        {diasSemana.map(dia => {
          // Calcular la fecha para cada elemento día
          let fechaDia = null;
          if (datosSemana && datosSemana.fechaInicioSemana) {
            const fechaInicio = new Date(datosSemana.fechaInicioSemana+"T00:00:00");
            const diaIndex = diasSemana.findIndex(d => d.key === dia.key);
            fechaDia = new Date(fechaInicio);
            fechaDia.setDate(fechaInicio.getDate() + diaIndex);
          }

          return (
            <DiaCalendario
              key={dia.key}
              nombreDia={dia.nombre}
              reservas={datosSemana?.reservasPorDia[dia.key] || []}
              fecha={fechaDia}
            />
          );
        })}
      </div>
    </div>
  );
};

export default CalendarioSemanal;