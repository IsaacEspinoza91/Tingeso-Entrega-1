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
import java.util.NoSuchElementException;

@Service
public class ComprobanteService {

    private static final double IVA = 0.19;         // Valor porcentual constante del IVA

    @Autowired
    private ComprobanteRepository comprobanteRepository;
    // Al ser DetalleComprobante una entidad debil que depende de Comprobante, toda su operacion se realiza en esta capa
    @Autowired
    private DetalleComprobanteRepository detalleComprobanteRepository;
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private ClienteService clienteService;





    //Permite crear un comprobante completo con todos sus detalles de comprobante
    public ComprobanteEntity crearComprobanteDesdeReserva(Long reservaId, boolean esFeriado, double descuentoExtra) {
        // Condicion base si el descuento extra es negativo. No crea el comprobante ni detalles
        if (descuentoExtra<0) return null;

        // Obtener reserva segun la id
        ReservaEntity reserva = reservaService.getReservaById(reservaId);

        // Obtener precio de tarifa de la reserva segun el tipo de dia
        int tarifaBase = calcularTarifaBase(reserva,esFeriado);
        // Obtener la cantidad de personas del grupo
        int totalPersonas = reserva.getTotalPersonas();
        // Obtener tarifa para cada integrante
        double tarifaIntegrante = (double) tarifaBase / totalPersonas;  // Casteo de enteros
        // Obtner descuento extra para cada integrante
        double descuenteExtraIntegrante = descuentoExtra / totalPersonas;
        // Inicializacion de var para contar cumpleañeros. Regla de negocio, cantidad max de descuento por grupo
        int cantidadCumpleanieros = 0;
        List<ClienteEntity> integrantes = reservaService.getIntegrantesById(reservaId);

        // Caso en que la lista en que la reserva no tiene integrantes asociados. No se pueden crear detalles ni comprobante
        if (integrantes.isEmpty()) throw new IllegalStateException("No hay clientes asociados a la reserva");

        // Caso en que no estan todos los integrantes asignados a la reserva. No se pueden crear detalles ni comprobante
        if (integrantes.size() != totalPersonas) throw new IllegalStateException("No estan todos los clientes asociados a la reserva");


        // Crear objeto comprobante y asignar reserva y estado de no pagado
        ComprobanteEntity comprobante = new ComprobanteEntity();
        comprobante.setReserva(reserva);
        comprobante.setPagado(true);
        comprobante = comprobanteRepository.save(comprobante);


        // Crear detalles para cada persona en la reserva. Iteramos sobre la lista de integrantes
        for (ClienteEntity clienteActual : integrantes) {

            DetalleComprobanteEntity detalle = crearDetalleComprobante(
                    comprobante, clienteActual, tarifaIntegrante,
                    descuenteExtraIntegrante, totalPersonas, reserva.getFecha(), cantidadCumpleanieros);
            detalleComprobanteRepository.save(detalle);

            // Si es cumpleaniero, se suma a la cantidad en la variable
            if (clienteService.cumpleAnios(clienteActual,reserva.getFecha())) cantidadCumpleanieros += 1;

            comprobante.getDetalles().add(detalle);     // Agregamos el detalle a la lista de detalles del comprobante
        }

        actualizarTotalComprobante(comprobante.getIdComprobante());
        return comprobante;
    }


    // Funcion que crea un objeto DetalleComprobante desde la entidad Comprobante
    public DetalleComprobanteEntity crearDetalleComprobante(ComprobanteEntity comprobante,
                                                             ClienteEntity cliente,
                                                             double tarifa,
                                                             double descuentoExtra,
                                                             int totalPersonas,
                                                             LocalDate fechaReserva,
                                                             int cantidadCompleanieros) {
        // Creo nuevo detalle
        DetalleComprobanteEntity detalle = new DetalleComprobanteEntity();
        detalle.setComprobante(comprobante);
        detalle.setCliente(cliente);

        // Calculo valores para el detalle
        double porcentajeDescuentoEspecial = 0;
        double porcentajeDescuentoGrupo = calcularDescuentoGrupo(totalPersonas);


        // Obtener que tipo de % descuento especial aplica (cliente frecuente o cumpleanios)
        //  Reglas de negocio sobre cantidad maxima de descuentos cumpleaños (50%)
        //   Grupo de 3 a 5: 1 persona max de cumpleanios tiene descuento
        //   Grupo de 6 a 15: 2 personas max de cumpleanios tienen descuento       (Deberia ser hasta 10 personas?)
        //  Cliente frecuente:
        //   No frecuente (0-1): 0%  , Regular (2-4): 10%  , Frecuente (5-6): 20%  , Muy Frecuente (7 o mas): 30%
        if (clienteService.cumpleAnios(cliente, fechaReserva)) {     // Cliente cumpleañero
            // Caso grupo 3 a 5, Cliente cumple años y hay cupo de descuento
            if (totalPersonas>=3 && totalPersonas<=5 && cantidadCompleanieros<1) {
                porcentajeDescuentoEspecial = calcularDescuentoCumpleanios(cliente,fechaReserva);
                detalle.setTieneDescuentoCumpleanios(true);

            } else if (totalPersonas>=6 && totalPersonas<=15 && cantidadCompleanieros <2) {   // Caso grupo de 6 a 15 y hay cupo)
                porcentajeDescuentoEspecial = calcularDescuentoCumpleanios(cliente, fechaReserva);
                detalle.setTieneDescuentoCumpleanios(true);

            }
        } else {    // Cliento no cumple años. Se verifica si es cliente frecuente
            porcentajeDescuentoEspecial = calcularDescuentoFrecuente(cliente, fechaReserva);
            if (porcentajeDescuentoEspecial != 0.0) detalle.setTieneDescuentoClienteFrecuente(true);
        }

        // Calculamos el monto con descuentos. Utilizamos descuentos en cascada, es decir, el descuento siguiente
        // se realiza sobre el valor anterior con descuento, no sobre el valor original
        double descuentoGrupo = tarifa * porcentajeDescuentoGrupo;
        double descuentoEspecial = (tarifa - descuentoGrupo) * porcentajeDescuentoEspecial;
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

        // Guardaos los porcentajes de descuento en el objeto detalle
        detalle.setPorcentajeDescuentoGrupo(porcentajeDescuentoGrupo*100);
        detalle.setPorcentajeDescuentoEspecial(porcentajeDescuentoEspecial*100);
        return detalle;
    }

    // Obtiene el precio de tarifa del arriendo segun el dia (semana, fin de semana o feriado)
    public int calcularTarifaBase(ReservaEntity reserva, boolean esFeriado) {
        // Para dias feriados se ingresa valor boleano

        LocalDate fecha = reserva.getFecha();

        boolean esFinDeSemana = fecha.getDayOfWeek().getValue() >= 6;// Analisis si es fin de semana o no

        if (esFeriado) return reserva.getPlan().getPrecioFeriado();
        else if (esFinDeSemana) return reserva.getPlan().getPrecioFinSemana();
        else return reserva.getPlan().getPrecioRegular();
    }

    // Obtiene el valor porcentual del descuento segun el numero de personas del grupo
    public double calcularDescuentoGrupo(int totalPersonas) {
        if (totalPersonas >= 11) return 0.30;
        if (totalPersonas >= 6) return 0.20;
        if (totalPersonas >= 3) return 0.10;
        // Caso menos de 3 personas
        return 0.0;
    }

    // Determina si el cliente obtiene el 50% de descuento por reservar el karting el dia de su compleanios
    public double calcularDescuentoCumpleanios(ClienteEntity cliente, LocalDate fecha) {
        if (clienteService.cumpleAnios(cliente, fecha)) {
            return 0.5;
        }
        return 0.0;
    }

    // Obtiene el valor porcentual del descuento para clientes frecuentes
    public double calcularDescuentoFrecuente(ClienteEntity cliente, LocalDate fecha) {
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

    // Craer unico objeto Comprobante, no considera detalles
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






    // Operaciones de DetalleComprobante

    // Obtener todos los detalles
    public List<DetalleComprobanteEntity> getDetalleComprobantes() {
        return detalleComprobanteRepository.findAll();
    }

    // Obtener detalles por comprobante segun id
    public List<DetalleComprobanteEntity> getDetallesByComprobante(Long idComprobante) {
        return detalleComprobanteRepository.findByComprobanteIdComprobante(idComprobante);
    }

    // Obtener detalle especifico segun id
    public DetalleComprobanteEntity getDetalleComprobanteById(Long id) {
        return detalleComprobanteRepository.findById(id).get();
    }

    // Obtener todos los detallesComprobantes de un cliente
    public List<DetalleComprobanteEntity> getDetalleComprobantesByClienteId(Long clienteId) {
        return detalleComprobanteRepository.findByClienteId(clienteId);
    }

    // Obtener el detalleComprobante de un cliente y comprobante especifo
    public DetalleComprobanteEntity getDetalleComprobanteByClienteIdAndComprobanteId(Long clienteId, Long comprobanteId) {
        return detalleComprobanteRepository.findByClienteIdAndComprobanteIdComprobante(clienteId, comprobanteId);
    }

    // Crear unico objeto Detalle, Considera todos los valores como parametros y automatiza el calculo de los montos
    @Transactional
    public DetalleComprobanteEntity createDetalleComprobante(DetalleComprobanteEntity detalle, Long idComprobante, Long idCliente) {
        // Obtengo el comprobante segun la id
        ComprobanteEntity comprobante = getComprobanteById(idComprobante);
        // Obtengo el cliente segun la id
        ClienteEntity cliente = clienteService.getClienteById(idCliente);

        // Configurar relación bidireccional Comprobante
        detalle.setComprobante(comprobante);
        // Configurar Relación unidireccional Cliente
        detalle.setCliente(cliente);

        DetalleComprobanteEntity detalleGuardado = detalleComprobanteRepository.save(detalle);

        // Actualizar total del comprobante al crear un nuevo detalle
        //actualizarTotalComprobante(idComprobante); // Notar que la lista de detalles es vacia, por lo que no actualiza el total

        return detalleGuardado;
    }

    // Actualizar detalle existente
    @Transactional
    public DetalleComprobanteEntity updateDetalle(Long id, DetalleComprobanteEntity detalle) {
        DetalleComprobanteEntity detalleOriginal = detalleComprobanteRepository.findById(id).get();

        // Actualizar campos de detalle
        detalle.setIdDetalle(id);
        detalle.setCliente(detalleOriginal.getCliente());
        detalle.setComprobante(detalleOriginal.getComprobante());

        // Actualizar total del detalle
        double descuentoGrupo = detalle.getTarifa() * (detalle.getPorcentajeDescuentoGrupo()/100);
        double descuentoEspecial = (detalle.getTarifa() - descuentoGrupo) * (detalle.getPorcentajeDescuentoEspecial()/100);
        double montoConDescuento = detalle.getTarifa() - descuentoGrupo - descuentoEspecial - detalle.getDescuentoExtra();

        double iva = montoConDescuento * IVA;// calculo el valor del iva
        double total = montoConDescuento + iva;// Calculo el total sumando iva

        detalle.setDescuentoGrupo(descuentoGrupo);
        detalle.setDescuentoEspecial(descuentoEspecial);
        detalle.setMontoFinal(montoConDescuento);
        detalle.setMontoIva(iva);
        detalle.setMontoTotal(total);

        // Actualizar total del comprobante
        actualizarTotalComprobante(detalle.getComprobante().getIdComprobante());

        return detalleComprobanteRepository.save(detalle);
    }

    // Actualizar detalle existente
    @Transactional
    public DetalleComprobanteEntity updateClienteDeDetalle(Long id, Long idCliente) {
        DetalleComprobanteEntity detalleOriginal = detalleComprobanteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el detalle con ID: " + id));

        ClienteEntity cliente = clienteService.getClienteById(idCliente);
        if (cliente == null) {
            throw new NoSuchElementException("No se encontró el cliente con ID: " + idCliente);
        }
        detalleOriginal.setCliente(cliente);
        return detalleComprobanteRepository.save(detalleOriginal);
    }

    // Eliminar detalle
    @Transactional
    public boolean deleteDetalleComprobante(Long id) throws Exception{
        try {
            // Obtener el detalle segun la id
            DetalleComprobanteEntity detalle = getDetalleComprobanteById(id);

            // Obtener comprobante de un DetalleComprobante
            ComprobanteEntity comprobante = detalle.getComprobante();

            // Obtener intregrante del Detalle
            ClienteEntity cliente = detalle.getCliente();

            // Obtener la reserva del Detalle
            ReservaEntity reserva = comprobante.getReserva();

            // Elimina el detalle de la lista de detalles de Comprobante
            comprobante.getDetalles().remove(detalle);
            detalle.setComprobante(null);

            // Elimina el integrante del detalle de la lista de integrantes de la reserva
            reserva.getIntegrantes().remove(cliente);

            // Eliminar la reserva de la lista reservas como integrante del Cliente
            cliente.getReservasComoIntegrante().remove(reserva);

            // Actualizar el total del comprobante
            actualizarTotalComprobante(comprobante.getIdComprobante());

            // Elimina el detalle de la base de datos
            detalleComprobanteRepository.delete(detalle);
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar DetalleComprobante: " + e.getMessage(), e);
        }
    }
}
