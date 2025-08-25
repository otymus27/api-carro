package br.com.carro.controllers;

import br.com.carro.entities.Marca;
import br.com.carro.services.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    // ✅ Novo Endpoint para Relatórios de Marcas
    @GetMapping("/marcas")
    public ResponseEntity<byte[]> gerarRelatorioMarcas(@RequestParam String formato) {

        List<Marca> marcas = relatorioService.getAllMarcas();

        byte[] relatorioBytes;
        String filename;
        String contentType;

        switch (formato.toLowerCase()) {
            case "csv":
                relatorioBytes = relatorioService.gerarMarcaCsv(marcas);
                filename = "relatorio_marcas.csv";
                contentType = "text/csv";
                break;
            case "xls":
                relatorioBytes = relatorioService.gerarMarcaXls(marcas);
                filename = "relatorio_marcas.xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                break;
            case "pdf":
                relatorioBytes = relatorioService.gerarMarcaPdf(marcas);
                filename = "relatorio_marcas.pdf";
                contentType = "application/pdf";
                break;
            default:
                return ResponseEntity.badRequest().body("Formato de relatório inválido.".getBytes());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(relatorioBytes);
    }
}
