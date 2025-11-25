package br.com.servicos_auto.services;

import br.com.servicos_auto.models.Image;
import br.com.servicos_auto.models.PrestadorServico;
import br.com.servicos_auto.repositories.ImageRepository;
import br.com.servicos_auto.repositories.PrestadorServicoRepository;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;

@Service
public class ImageService {

    private final Cloudinary cloudinary;
    private final ImageRepository imageRepository;
    private final PrestadorServicoRepository prestadorServicoRepository;

    public ImageService(
        Cloudinary cloudinary,
        ImageRepository imageRepository,
        PrestadorServicoRepository prestadorServicoRepository
    ) {
        this.cloudinary = cloudinary;
        this.imageRepository = imageRepository;
        this.prestadorServicoRepository = prestadorServicoRepository;
    }

    // 🔥 MÉTODO QUE SEU CONTROLLER ESPERA
    public Image uploadPrestadorImage(MultipartFile file, Long prestadorId) throws IOException {

        // Verifica se prestador existe
        PrestadorServico prestador = prestadorServicoRepository.findById(prestadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prestador não encontrado"));

        // Upload no Cloudinary
        Map uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.emptyMap());

        String imageUrl = uploadResult.get("secure_url").toString();

        // Salva a imagem no banco
        Image image = new Image();
        image.setUrl(imageUrl);
        image.setPrestadorServico(prestador);

        return imageRepository.save(image);
    }
}
