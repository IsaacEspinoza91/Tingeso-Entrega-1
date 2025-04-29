import React from 'react';
import './ReportesTable.css';

const ReportesTable = ({ data, tipoReporte }) => {
  if (!data || data.length === 0) {
    return <div className="no-data">No hay datos para mostrar</div>;
  }

  // Obtener todos los meses de todos los reportes
  const allMonths = new Set();
  data.forEach(item => {
    Object.keys(item.ingresosPorMes).forEach(month => allMonths.add(month));
  });
  const months = Array.from(allMonths).sort((a, b) => {
    const [monthA, yearA] = a.split('-');  // Ordenar los meses cronologicamente
    const [monthB, yearB] = b.split('-');
    return new Date(`${monthA} 1, ${yearA}`) - new Date(`${monthB} 1, ${yearB}`);
  });

  return (
    <div className="reportes-table-container">
      <table className="reportes-table">
        <thead>
          <tr>
            <th>{tipoReporte === 'vueltas' ? 'Descripción Plan' : 'Rango de Personas'}</th>
            {months.map(month => (
              <th key={month}>{month}</th>
            ))}
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          {data.map((item, index) => (
            <tr 
              key={index} 
              className={item.esTotalGeneral ? 'total-row' : ''}
            >
              <td>{tipoReporte === 'vueltas' ? item.descripcionPlan : item.rangoPersonas}</td>
              {months.map(month => (
                <td key={month}>
                  {item.ingresosPorMes[month] !== undefined 
                    ? `$${item.ingresosPorMes[month].toLocaleString('es-CL')}` 
                    : '$0'}
                </td>
              ))}
              <td>${item.total.toLocaleString('es-CL')}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ReportesTable;