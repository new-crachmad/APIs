package br.com.servicos_auto.models;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ImageDTO {

    private Long id;
    private String url;
    private String type;
    private LocalDateTime uploadedAt;

    public ImageDTO(Image image) {
        this.id = image.getId();
        this.url = image.getUrl();
        this.type = image.getType();
        this.uploadedAt = image.getUploadedAt();
    }

}
