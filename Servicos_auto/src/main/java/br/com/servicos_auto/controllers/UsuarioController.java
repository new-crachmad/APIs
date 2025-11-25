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

import br.com.servicos_auto.models.Image;
import br.com.servicos_auto.models.ImageDTO;
import br.com.servicos_auto.models.Usuario;
import br.com.servicos_auto.models.UsuarioDTO;
import br.com.servicos_auto.repositories.ImageRepository;
import br.com.servicos_auto.services.CloudinaryService;
import br.com.servicos_auto.services.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ImageRepository imageRepository;

    // Endpoint para listar todos os usuários
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> findAll() {
        List<UsuarioDTO> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    // Endpoint para buscar um usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.findById(id);
        return ResponseEntity.ok(usuario);
    }

    // Endpoint para buscar um usuário por email
    @GetMapping("/by-email")
    public ResponseEntity<UsuarioDTO> findByEmail(@RequestParam String email) {
        UsuarioDTO usuario = usuarioService.findByEmail(email);
        return ResponseEntity.ok(usuario);
    }

    // Endpoint para criar um novo usuário
    @PostMapping
    public ResponseEntity<UsuarioDTO> create(@Valid @RequestBody Usuario usuario) {
        Usuario savedUsuario = usuarioService.create(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioDTO(savedUsuario));
    }

    // Endpoint para atualizar um usuário existente
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        Usuario updatedUsuario = usuarioService.update(id, usuarioDetails);
        return ResponseEntity.ok(new UsuarioDTO(updatedUsuario));
    }

    // Endpoint para deletar um usuário (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para upload de imagem para um usuário específico
    @PostMapping("/{usuarioId}/upload-image")
    public ResponseEntity<ImageDTO> uploadImage(@PathVariable Long usuarioId,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }

            Usuario usuario = usuarioService.findEntityById(usuarioId);
            String url = cloudinaryService.uploadImage(file);

            Image image = new Image();
            image.setUsuario(usuario);
            image.setUrl(url);
            image.setType(file.getContentType());
            image.setUploadedAt(LocalDateTime.now());

            Image savedImage = imageRepository.save(image);

            return ResponseEntity.ok(new ImageDTO(savedImage));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
