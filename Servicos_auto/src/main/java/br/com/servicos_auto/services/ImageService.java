package br.com.servicos_auto.services;

import br.com.servicos_auto.models.Anuncio;
import br.com.servicos_auto.models.Image;
import br.com.servicos_auto.models.PrestadorServico;
import br.com.servicos_auto.models.Usuario;
import br.com.servicos_auto.repositories.AnuncioRepository;
import br.com.servicos_auto.repositories.ImageRepository;
import br.com.servicos_auto.repositories.PrestadorServicoRepository;
import br.com.servicos_auto.repositories.UsuarioRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PrestadorServicoRepository prestadorServicoRepository;

    @Autowired
    private AnuncioRepository anuncioRepository;

    private Image uploadImage(MultipartFile file) {
        try {
            logger.info("Iniciando upload para o Cloudinary...");
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            logger.info("Upload para o Cloudinary concluído com sucesso.");

            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            String format = (String) uploadResult.get("format");

            if (url == null || publicId == null) {
                logger.error("A resposta do Cloudinary não contém 'secure_url' ou 'public_id'. Resposta: {}", uploadResult);
                throw new RuntimeException("Erro ao processar a resposta do Cloudinary.");
            }

            Image image = new Image();
            image.setUrl(url);
            image.setCloudinaryPublicId(publicId);
            image.setType("image/" + format);
            image.setUploadedAt(LocalDateTime.now());

            return image;

        } catch (IOException e) {
            logger.error("Erro ao fazer upload da imagem para o Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Falha ao fazer upload da imagem.", e);
        }
    }

    public Image uploadUsuarioImage(MultipartFile file, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Image image = uploadImage(file);
        image.setUsuario(usuario);

        return imageRepository.save(image);
    }

    public Image uploadPrestadorImage(MultipartFile file, Long prestadorId) {
        PrestadorServico prestadorServico = prestadorServicoRepository.findById(prestadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prestador não encontrado"));

        Image image = uploadImage(file);
        image.setPrestadorServico(prestadorServico);

        return imageRepository.save(image);
    }

    public Image uploadAnuncioImage(MultipartFile file, Long anuncioId) {
        Anuncio anuncio = anuncioRepository.findById(anuncioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anúncio não encontrado"));

        Image image = uploadImage(file);
        image.setAnuncio(anuncio);

        return imageRepository.save(image);
    }

    public void deleteImage(String publicId) {
        try {
            logger.info("Tentando deletar imagem com public_id: {}", publicId);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("Imagem {} deletada do Cloudinary com sucesso.", publicId);
        } catch (IOException e) {
            logger.error("Erro ao deletar imagem do Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Falha ao deletar imagem.", e);
        }
    }
}
