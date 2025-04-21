package com.kartingRM.AppKartingRM.services;

import com.kartingRM.AppKartingRM.entities.ClienteEntity;
import com.kartingRM.AppKartingRM.entities.ComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.DetalleComprobanteEntity;
import com.kartingRM.AppKartingRM.entities.ReservaEntity;
import com.kartingRM.AppKartingRM.repositories.ComprobanteRepository;
import com.kartingRM.AppKartingRM.repositories.DetalleComprobanteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ComprobanteService {

    private static final double IVA = 0.19;         // Valor porcentual constante del IVA

    @Autowired
    private ComprobanteRepository comprobanteRepository;
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private ClienteService clienteService;




    @Autowired
    private DetalleComprobanteRepository detalleComprobanteRepository;
    //private DetalleComprobanteService detalleComprobanteService;


    public ComprobanteEntity crearComprobanteDesdeReserva(Long reservaId, boolean esFeriado, double descuentoExtra) {
        // Condicion base si el descuento extra es negativo. No crea el comprobante ni detalles
        if (descuentoExtra<0) return null;

        // Obtener reserva segun la id
        ReservaEntity reserva = reservaService.getReservaById(reservaId);

        // Crear objeto comprobante y asignar reserva y estado de no pagado
        ComprobanteEntity comprobante = new ComprobanteEntity();
        comprobante.setReserva(reserva);
        comprobante.setPagado(false);
        comprobante = comprobanteRepository.save(comprobante);

        // Obtener precio de tarifa de la reserva segun el tipo de dia
        int tarifaBase = calcularTarifaBase(reserva,esFeriado);
        // Obtener la cantidad de personas del grupo
        int totalPersonas = reserva.getTotalPersonas();
        // Obtener tarifa para cada integrante
        double tarifaIntegrante = tarifaBase / totalPersonas;
        // Obtner descuento extra para cada integrante
        double descuenteExtraIntegrante = descuentoExtra / totalPersonas;

        // Crear detalles para cada persona en la reserva. Iteramos sobre la lista de integrantes
        for (ClienteEntity clienteActual : reservaService.getIntegrantesById(reservaId)) {
            DetalleComprobanteEntity detalle = crearDetalleComprobante(
                    comprobante, clienteActual, tarifaIntegrante,
                    descuenteExtraIntegrante, totalPersonas, reserva.getFecha());
            detalleComprobanteRepository.save(detalle);
            //detalleComprobanteService.guardarDetalle(detalle);
            comprobante.getDetalles().add(detalle);     // Agregamos el detalle a la lista de detalles del comprobante
        }

        actualizarTotalComprobante(comprobante.getIdComprobante());
        return comprobante;
    }


    // Funcion que crea un objeto DetalleComprobante desde la entidad Comprobante
    private DetalleComprobanteEntity crearDetalleComprobante(ComprobanteEntity comprobante,
                                                             ClienteEntity cliente,
                                                             double tarifa,
                                                             double descuentoExtra,
                                                             int totalPersonas,
                                                             LocalDate fechaReserva) {
        // Creo nuevo detalle
        DetalleComprobanteEntity detalle = new DetalleComprobanteEntity();
        detalle.setComprobante(comprobante);
        detalle.setCliente(cliente);

        // Calculo valores para el detalle
        double porcentajeDescuentoEspecial;
        double porcentajeDescuentoGrupo = calcularDescuentoGrupo(totalPersonas);

        // Obtener que tipo de descuento especial aplica (cliente frecuente o cumpleanios)
        if (clienteService.cumpleAnios(cliente, fechaReserva)) {    // Caso cliente esta de cumpleanios el dia de la reserva
            porcentajeDescuentoEspecial = calcularDescuentoCumpleanios(cliente,fechaReserva);
        } else {    // Caso no esta de cumpleanios y se verifica si es cliente frecuente
            porcentajeDescuentoEspecial = calcularDescuentoFrecuente(cliente, fechaReserva);
        }

        // Calculamos el monto con descuentos. Utilizamos descuentos en cascada, es decir, el descuento siguiente
        // se realiza sobre el valor anterior con descuento, no sobre el valor original
        double descuentoGrupo = tarifa * porcentajeDescuentoGrupo;
        double descuentoEspecial = descuentoGrupo * porcentajeDescuentoEspecial;
        // Calcula el valor total de para un detalle (sin iva)
        double montoConDescuento = tarifa - descuentoGrupo - descuentoEspecial - descuentoExtra;


        // calculo el valor del iva
        double iva = montoConDescuento * IVA;
        // Calculo el total sumando iva
        double total = montoConDescuento + iva;

        // Seteamos valores dentro del detalle, considerando descuento de cumpleanios y especiales
        detalle.setTarifa(tarifa);
        detalle.setDescuentoGrupo(descuentoGrupo);
        detalle.setDescuentoEspecial(descuentoEspecial);
        detalle.setDescuentoExtra(descuentoExtra);
        detalle.setMontoFinal(montoConDescuento);
        detalle.setMontoIva(iva);
        detalle.setMontoTotal(total);

        return detalle;
    }

    // Obtiene el precio de tarifa del arriendo segun el dia (semana, fin de semana o feriado)
    private int calcularTarifaBase(ReservaEntity reserva, boolean esFeriado) {
        // Para dias feriados se ingresa valor boleano

        LocalDate fecha = reserva.getFecha();

        boolean esFinDeSemana = fecha.getDayOfWeek().getValue() >= 6;// Analisis si es fin de semana o no

        if (esFeriado) return reserva.getPlan().getPrecioFeriado();
        else if (esFinDeSemana) return reserva.getPlan().getPrecioFinSemana();
        else return reserva.getPlan().getPrecioRegular();
    }

    // Obtiene el valor porcentual del descuento segun el numero de personas del grupo
    private double calcularDescuentoGrupo(int totalPersonas) {
        if (totalPersonas >= 11) return 0.30;
        if (totalPersonas >= 6) return 0.20;
        if (totalPersonas >= 3) return 0.10;
        // Caso menos de 3 personas
        return 0.0;
    }

    // Determina si el cliente obtiene el 50% de descuento por reservar el karting el dia de su compleanios
    private double calcularDescuentoCumpleanios(ClienteEntity cliente, LocalDate fecha) {
        if (clienteService.cumpleAnios(cliente, fecha)) {
            return 0.5;
        }
        return 0.0;
    }

    // Obtiene el valor porcentual del descuento para clientes frecuentes
    private double calcularDescuentoFrecuente(ClienteEntity cliente, LocalDate fecha) {
        // Obtener cantidad de veces que el cliente utiliza el karting en el mes actual
        int visitas = clienteService.obtenerVecesUtilizadoKarting(cliente.getId(), fecha.getYear(), fecha.getMonthValue());
        if (visitas >= 7) return 0.30;
        if (visitas >= 5) return 0.20;
        if (visitas >= 2) return 0.10;
        return 0.0;
    }









    public List<ComprobanteEntity> getComprobantes() {
        return comprobanteRepository.findAll();
    }

    public ComprobanteEntity getComprobanteById(Long id) {
        return comprobanteRepository.findById(id).get();
    }

    public ComprobanteEntity createComprobante(ComprobanteEntity comprobante, Long idReserva) {
        ReservaEntity reserva = reservaService.getReservaById(idReserva);
        if (reserva != null) {
            comprobante.setReserva(reserva);
            return comprobanteRepository.save(comprobante);
        } else {
            throw new RuntimeException("Reserva no encontrada");
        }
    }

    public ComprobanteEntity updateComprobante(Long id, ComprobanteEntity comprobante) {
        ComprobanteEntity comprobanteOriginal = comprobanteRepository.findById(id).get();
        comprobante.setIdComprobante(id);
        comprobante.setReserva(comprobanteOriginal.getReserva());
        return comprobanteRepository.save(comprobante);
    }

    public ComprobanteEntity updateReservaDeComprobante(Long id, Long idReserva) {
        ComprobanteEntity comprobanteOriginal = comprobanteRepository.findById(id).get();
        ReservaEntity reserva = reservaService.getReservaById(idReserva);

        comprobanteOriginal.setReserva(reserva);
        return comprobanteRepository.save(comprobanteOriginal);
    }

    public boolean deleteComprobante(Long id) throws Exception{
        try{
            comprobanteRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    // Calcular el atributo total de Comprobante segun el los detalles de un comprobante especifico
    @Transactional
    public void actualizarTotalComprobante(Long idComprobante) {
        ComprobanteEntity comprobante = getComprobanteById(idComprobante);
        double total = 0.0;
        // Itero en los detalles y voy sumando cada monto total para obtener el total de ganancias del comprobante
        for (DetalleComprobanteEntity detalle : comprobante.getDetalles()) {
            total = total + detalle.getMontoTotal();
        }
        comprobante.setTotal(total);
        comprobanteRepository.save(comprobante);
    }
}
