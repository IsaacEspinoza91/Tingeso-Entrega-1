import { Page, Text, View, Document, StyleSheet} from '@react-pdf/renderer';
import './ComprobantePDF.css';

// Estilos para el PDF
const styles = StyleSheet.create({
  page: {
    padding: 30,
    fontSize: 10,
    fontFamily: 'Helvetica',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 20,
    borderBottom: '1px solid #ccc',
    paddingBottom: 10,
  },
  logo: {
    width: 100,
    height: 50,
  },
  title: {
    fontSize: 16,
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 10,
  },
  subtitle: {
    fontSize: 12,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  section: {
    marginBottom: 15,
  },
  row: {
    flexDirection: 'row',
    marginBottom: 5,
  },
  label: {
    width: '30%',
    fontWeight: 'bold',
  },
  value: {
    width: '70%',
  },
  table: {
    width: '100%',
    marginTop: 15,
  },
  tableHeader: {
    backgroundColor: '#f0f0f0',
    flexDirection: 'row',
    padding: 5,
    fontWeight: 'bold',
    borderBottom: '1px solid #ddd',
  },
  tableRow: {
    flexDirection: 'row',
    padding: 5,
    borderBottom: '1px solid #eee',
  },
  col1: { width: '5%' },
  col2: { width: '20%' },
  col3: { width: '10%' },
  col4: { width: '16%' },
  col5: { width: '16%' },
  col6: { width: '10%' },
  col7: { width: '10%' },
  col8: { width: '10%' },
  totals: {
    marginTop: 15,
    paddingTop: 10,
    borderTop: '1px solid #333',
  },
  footer: {
    marginTop: 30,
    paddingTop: 10,
    borderTop: '1px solid #ccc',
    fontSize: 8,
    textAlign: 'center',
  },
});

const ComprobantePDF = ({ comprobante }) => (
  <Document>
    <Page size="A4" style={styles.page}>

      <Text style={styles.title}>COMPROBANTE DE PAGO</Text>

      <View style={styles.section}>
        <View style={styles.row}>
          <Text style={styles.label}>N° Comprobante:</Text>
          <Text style={styles.value}>{comprobante.idComprobante}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Estado:</Text>
          <Text style={styles.value}>{comprobante.pagado ? 'PAGADO' : 'PENDIENTE'}</Text>
        </View>
      </View>


      <View style={styles.section}>
        <Text style={styles.subtitle}>INFORMACIÓN DEL PLAN</Text>
        <View style={styles.row}>
          <Text style={styles.label}>N° Plan:</Text>
          <Text style={styles.value}>{comprobante.reserva.plan.idPlan}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Descripción:</Text>
          <Text style={styles.value}>{comprobante.reserva.plan.descripcion}</Text>
        </View>
      </View>


      <View style={styles.section}>
        <Text style={styles.subtitle}>INFORMACIÓN DE LA RESERVA</Text>
        <View style={styles.row}>
          <Text style={styles.label}>N° Reserva:</Text>
          <Text style={styles.value}>{comprobante.reserva.idReserva}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Fecha:</Text>
          <Text style={styles.value}>{new Date(comprobante.reserva.fecha+"T00:00:00").toLocaleDateString()}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Horario de Inicio:</Text>
          <Text style={styles.value}>{comprobante.reserva.horaInicio.substring(0, 5)}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Total Personas:</Text>
          <Text style={styles.value}>{comprobante.reserva.totalPersonas}</Text>
        </View>
      </View>


      <View style={styles.section}>
        <Text style={styles.subtitle}>INFORMACIÓN DEL CLIENTE</Text>
        <View style={styles.row}>
          <Text style={styles.label}>N° Cliente:</Text>
          <Text style={styles.value}>{comprobante.reserva.reservante.id}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Cliente Reservante:</Text>
          <Text style={styles.value}>
            {comprobante.reserva.reservante.nombre} {comprobante.reserva.reservante.apellido}
          </Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>RUT:</Text>
          <Text style={styles.value}>{comprobante.reserva.reservante.rut}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Correo Electrónico:</Text>
          <Text style={styles.value}>{comprobante.reserva.reservante.correo}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.label}>Número Telefónico:</Text>
          <Text style={styles.value}>{comprobante.reserva.reservante.telefono}</Text>
        </View>
      </View>

      
      <Text style={styles.subtitle}>DETALLES DE PAGO</Text>
      <View style={styles.table}>
        <View style={styles.tableHeader}>
          <Text style={styles.col1}></Text>
          <Text style={styles.col2}>Cliente</Text>
          <Text style={styles.col3}>Tarifa</Text>

          <Text style={styles.col4}>Des. Grup</Text>
          <Text style={styles.col5}>Des. Esp</Text>
          <Text style={styles.col6}>M. Final</Text>
          <Text style={styles.col7}>IVA</Text>
          <Text style={styles.col8}>Total</Text>
        </View>
        
        {comprobante.detalles.map((detalle, index) => (
          <View key={detalle.idDetalle} style={styles.tableRow}>
            <Text style={styles.col1}>{index + 1}</Text>
            <Text style={styles.col2}>
              {detalle.cliente.nombre} {detalle.cliente.apellido}
            </Text>
            <Text style={styles.col3}>${detalle.tarifa.toLocaleString()}</Text>
            <Text style={styles.col4}>
                ({detalle.porcentajeDescuentoGrupo.toLocaleString()}%) ${detalle.descuentoGrupo.toLocaleString()}
            </Text>
            <Text style={styles.col5}>
                ({detalle.porcentajeDescuentoEspecial.toLocaleString()}%) ${detalle.descuentoEspecial.toLocaleString()}
            </Text>

            <Text style={styles.col6}>${detalle.montoFinal.toLocaleString()}</Text>
            <Text style={styles.col7}>${detalle.montoIva.toLocaleString()}</Text>
            <Text style={styles.col8}>${detalle.montoTotal.toLocaleString()}</Text>
          </View>
        ))}
      </View>


      <View style={styles.totals}>
        <View style={styles.row}>
          <Text style={[styles.label, { width: '70%' }]}>SUBTOTAL:</Text>
          <Text style={[styles.value, { width: '30%' }]}>
            ${comprobante.detalles.reduce((sum, item) => sum + item.montoFinal, 0).toLocaleString()}
          </Text>
        </View>
        <View style={styles.row}>
          <Text style={[styles.label, { width: '70%' }]}>IVA (19%):</Text>
          <Text style={[styles.value, { width: '30%' }]}>
            ${comprobante.detalles.reduce((sum, item) => sum + item.montoIva, 0).toLocaleString()}
          </Text>
        </View>
        <View style={styles.row}>
          <Text style={[styles.label, { width: '70%', fontWeight: 'bold' }]}>TOTAL:</Text>
          <Text style={[styles.value, { width: '30%', fontWeight: 'bold' }]}>
            ${comprobante.total.toLocaleString()}
          </Text>
        </View>
      </View>

      <View style={styles.footer}>
        <Text>¡Gracias por su preferencia! - Karting RM</Text>
      </View>
    </Page>
  </Document>
);

export default ComprobantePDF;