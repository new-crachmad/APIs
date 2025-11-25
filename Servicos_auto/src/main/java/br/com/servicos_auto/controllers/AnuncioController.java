package br.com.servicos_auto.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.com.servicos_auto.models.Anuncio;
import br.com.servicos_auto.models.AnuncioDTO;
import br.com.servicos_auto.models.Image;
import br.com.servicos_auto.models.ImageDTO;
import br.com.servicos_auto.repositories.AnuncioRepository;
import br.com.servicos_auto.repositories.ImageRepository;
import br.com.servicos_auto.services.AnuncioService;
import br.com.servicos_auto.services.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/anuncios")
@SecurityRequirement(name = "bearerAuth")
public class AnuncioController {

    @Autowired
    private AnuncioService anuncioService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private AnuncioRepository anuncioRepository;

    @Autowired
    private ImageRepository imageRepository;


    @GetMapping
    public ResponseEntity<List<AnuncioDTO>> findAll() {
        List<AnuncioDTO> anuncios = anuncioService.findAll();
        return ResponseEntity.ok(anuncios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnuncioDTO> findById(@PathVariable Long id) {
        AnuncioDTO anuncio = anuncioService.findById(id);
        return ResponseEntity.ok(anuncio);
    }

    @Operation(summary = "Cria um novo anúncio", description = "Endpoint para cadastrar um novo anúncio associado a um prestador de serviço.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{prestadorId}")
    public ResponseEntity<AnuncioDTO> create(
            @Parameter(description = "ID do prestador de serviço", required = true) @PathVariable Long prestadorId,
            @Valid @RequestBody Anuncio anuncio) {
        AnuncioDTO savedAnuncio = anuncioService.create(anuncio, prestadorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAnuncio);
    }

    @PatchMapping("/{AnuncioId}")
    public ResponseEntity<AnuncioDTO> update(@PathVariable Long AnuncioId, @RequestBody Anuncio anuncio) {
        AnuncioDTO UpdatedAnuncio = anuncioService.update(AnuncioId, anuncio);
        return ResponseEntity.ok(UpdatedAnuncio);
    }

    @DeleteMapping("/{AnuncioId}")
    public ResponseEntity<Void> delete(@PathVariable Long AnuncioId) {
        anuncioService.delete(AnuncioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{anuncioId}/upload-image")
    public ResponseEntity<ImageDTO> uploadImage(@PathVariable Long anuncioId,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }

            Anuncio anuncio = anuncioRepository.findById(anuncioId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio não encontrado"));

            String imageUrl = cloudinaryService.uploadImage(file);

            Image image = new Image();
            image.setUrl(imageUrl);
            image.setAnuncio(anuncio);
            image.setType(file.getContentType());
            
            Image savedImage = imageRepository.save(image);

            return ResponseEntity.ok(new ImageDTO(savedImage));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
