import React, { useState } from 'react';
import { 
  getReporteIngresosPorVueltas, 
  getReporteIngresosPorPersonas 
} from '../../services/reporteService';
import ReportesForm from '../../components/reportes/ReportesForm';
import ReportesTable from '../../components/reportes/ReportesTable';
import './Reportes.css';

const Reportes = () => {
  const [reporteData, setReporteData] = useState([]);
  const [tipoReporte, setTipoReporte] = useState('vueltas');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const generarReporte = async (formData) => {
    setLoading(true);
    setError(null);
    try {
      let data;
      const params = {
        mes_inicio: formData.mesInicio,
        anio_inicio: formData.anioInicio,
        mes_fin: formData.mesFin,
        anio_fin: formData.anioFin
      };

      if (formData.tipoReporte === 'vueltas') {
        data = await getReporteIngresosPorVueltas(params);
        setTipoReporte('vueltas');
      } else {
        data = await getReporteIngresosPorPersonas(params);
        setTipoReporte('personas');
      }

      setReporteData(data);
    } catch (err) {
      setError('Error al generar el reporte. Por favor intente nuevamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="reportes-page">
      <h1>Reportes</h1>
      
      <ReportesForm 
        onGenerarReporte={generarReporte} 
        loading={loading} 
      />

      {error && <div className="error-message">{error}</div>}

      {reporteData.length > 0 && (
        <ReportesTable 
          data={reporteData} 
          tipoReporte={tipoReporte} 
        />
      )}
    </div>
  );
};

export default Reportes;