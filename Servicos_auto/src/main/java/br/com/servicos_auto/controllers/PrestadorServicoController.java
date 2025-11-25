package br.com.servicos_auto.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.com.servicos_auto.models.Image;
import br.com.servicos_auto.models.ImageDTO;
import br.com.servicos_auto.models.PrestadorServico;
import br.com.servicos_auto.models.PrestadorServicoDTO;

import br.com.servicos_auto.services.ImageService;
import br.com.servicos_auto.services.PrestadorServicoService;
import br.com.servicos_auto.services.CloudinaryService;      // 🔥 IMPORT ADICIONADO

import br.com.servicos_auto.repositories.ImageRepository;   // 🔥 IMPORT ADICIONADO

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/prestadores")
@SecurityRequirement(name = "bearerAuth")
public class PrestadorServicoController {

    @Autowired
    private PrestadorServicoService prestadorServicoService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;  // 🔥 ADICIONADO — VOCÊ USA ESTE SERVICE

    @GetMapping
    public ResponseEntity<List<PrestadorServicoDTO>> findAll() {
        List<PrestadorServicoDTO> prestadores = prestadorServicoService.findall();
        return ResponseEntity.ok(prestadores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestadorServicoDTO> findById(@PathVariable Long id) {
        PrestadorServicoDTO prestador = prestadorServicoService.findById(id);
        return ResponseEntity.ok(prestador);
    }

    @GetMapping("/by-email")
    public ResponseEntity<PrestadorServicoDTO> findById(@RequestParam String email) {
        PrestadorServicoDTO prestador = prestadorServicoService.findByEmail(email);
        return ResponseEntity.ok(prestador);
    }

    @Operation(summary = "Cria um novo prestador de serviço", 
               description = "Endpoint para cadastrar um novo prestador")
    @PostMapping
    public ResponseEntity<PrestadorServicoDTO> create(@Valid @RequestBody PrestadorServico prestadorServico) {
        PrestadorServicoDTO savedPrestadorServico = prestadorServicoService.create(prestadorServico);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPrestadorServico);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PrestadorServicoDTO> update(@PathVariable Long id,
            @RequestBody PrestadorServico prestadorServico) {
        PrestadorServicoDTO updatedPrestador = prestadorServicoService.update(id, prestadorServico);
        return ResponseEntity.ok(updatedPrestador);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prestadorServicoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{prestadorId}/upload-image")
    public ResponseEntity<ImageDTO> uploadImage(@PathVariable Long prestadorId,
            @RequestParam("file") MultipartFile file) {

        try {
            if (file == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Image image = imageService.uploadPrestadorImage(file, prestadorId);

            return ResponseEntity.ok(new ImageDTO(image));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
