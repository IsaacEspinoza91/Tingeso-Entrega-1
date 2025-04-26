import React, { useState } from 'react';
import './ReportesForm.css';

const meses = [
  { value: 1, label: 'Enero' },
  { value: 2, label: 'Febrero' },
  { value: 3, label: 'Marzo' },
  { value: 4, label: 'Abril' },
  { value: 5, label: 'Mayo' },
  { value: 6, label: 'Junio' },
  { value: 7, label: 'Julio' },
  { value: 8, label: 'Agosto' },
  { value: 9, label: 'Septiembre' },
  { value: 10, label: 'Octubre' },
  { value: 11, label: 'Noviembre' },
  { value: 12, label: 'Diciembre' }
];

const ReportesForm = ({ onGenerarReporte, loading }) => {
  const currentYear = new Date().getFullYear();
  const [formData, setFormData] = useState({
    tipoReporte: 'vueltas',
    mesInicio: 1,
    anioInicio: currentYear,
    mesFin: 12,
    anioFin: currentYear
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name.includes('anio') ? parseInt(value) : value
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onGenerarReporte(formData);
  };

  return (
    <div className="reportes-form">
      <h3>Generar Reporte</h3>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Tipo de Reporte:</label>
          <select
            name="tipoReporte"
            value={formData.tipoReporte}
            onChange={handleChange}
          >
            <option value="vueltas">Ingresos por vueltas</option>
            <option value="personas">Ingresos por personas</option>
          </select>
        </div>

        <div className="date-range">
          <div className="form-group">
            <label>Fecha Inicio:</label>
            <div className="date-inputs">
              <select
                name="mesInicio"
                value={formData.mesInicio}
                onChange={handleChange}
              >
                {meses.map(mes => (
                  <option key={mes.value} value={mes.value}>{mes.label}</option>
                ))}
              </select>
              <input
                type="number"
                name="anioInicio"
                value={formData.anioInicio}
                onChange={handleChange}
                min="2000"
                max="2100"
              />
            </div>
          </div>

          <div className="form-group">
            <label>Fecha Fin:</label>
            <div className="date-inputs">
              <select
                name="mesFin"
                value={formData.mesFin}
                onChange={handleChange}
              >
                {meses.map(mes => (
                  <option key={mes.value} value={mes.value}>{mes.label}</option>
                ))}
              </select>
              <input
                type="number"
                name="anioFin"
                value={formData.anioFin}
                onChange={handleChange}
                min="2000"
                max="2100"
              />
            </div>
          </div>
        </div>

        <button type="submit" disabled={loading}>
          {loading ? 'Generando...' : 'Generar Reporte'}
        </button>
      </form>
    </div>
  );
};

export default ReportesForm;