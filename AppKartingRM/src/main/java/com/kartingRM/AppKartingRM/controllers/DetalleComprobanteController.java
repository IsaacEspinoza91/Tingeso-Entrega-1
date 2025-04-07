package com.kartingRM.AppKartingRM.controllers;

import com.kartingRM.AppKartingRM.services.ComprobanteService;
import com.kartingRM.AppKartingRM.services.DetalleComprobanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/detalleComprobantes")
public class DetalleComprobanteController {

    @Autowired
    private DetalleComprobanteService detalleComprobanteService;
    @Autowired
    private ComprobanteService comprobanteService;


}
