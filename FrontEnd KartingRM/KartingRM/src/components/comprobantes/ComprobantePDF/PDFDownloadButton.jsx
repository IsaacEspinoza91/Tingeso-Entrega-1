import React from 'react';
import { PDFDownloadLink } from '@react-pdf/renderer';
import ComprobantePDF from './ComprobantePDF';

const PDFDownloadButton = ({ comprobante }) => {
  return (
    <PDFDownloadLink
      document={<ComprobantePDF comprobante={comprobante} />}
      fileName={`comprobante-${comprobante.idComprobante}.pdf`}
    >
      {({ loading }) => (
        <button className="pdf-button" disabled={loading}>
          {loading ? 'Generando PDF...' : 'Descargar PDF'}
        </button>
      )}
    </PDFDownloadLink>
  );
};

export default PDFDownloadButton;