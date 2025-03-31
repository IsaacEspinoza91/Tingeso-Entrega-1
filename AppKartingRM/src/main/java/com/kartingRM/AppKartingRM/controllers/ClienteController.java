package com.kartingRM.AppKartingRM.controllers;

import com.kartingRM.AppKartingRM.entities.ClienteEntity;
import com.kartingRM.AppKartingRM.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/")
    public ResponseEntity<List<ClienteEntity>> getClientes() {
        List<ClienteEntity> clientes = clienteService.getClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id_cliente}")
    public ResponseEntity<ClienteEntity> getCliente(@PathVariable Long id_cliente) {
        ClienteEntity cliente = clienteService.getClienteById(id_cliente);
        return ResponseEntity.ok(cliente);
    }

    @PostMapping("/")
    public ResponseEntity<ClienteEntity> createCliente(@RequestBody ClienteEntity cliente) {
        ClienteEntity clienteNuevo = clienteService.createCliente(cliente);
        return ResponseEntity.ok(clienteNuevo);
    }

    @PutMapping("/{id_cliente}")
    public ResponseEntity<ClienteEntity> updateCliente(@PathVariable Long id_cliente, @RequestBody ClienteEntity cliente) {
        ClienteEntity clienteActualizado = clienteService.updateCliente(id_cliente, cliente);
        return ResponseEntity.ok(clienteActualizado);
    }

    @DeleteMapping("/{id_cliente}")
    public ResponseEntity<Boolean> deleteCliente(@PathVariable Long id_cliente) throws Exception {
        clienteService.deleteCliente(id_cliente);
        return ResponseEntity.noContent().build();
    }
}
