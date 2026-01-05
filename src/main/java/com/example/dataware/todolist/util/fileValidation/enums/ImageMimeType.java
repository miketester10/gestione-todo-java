package com.example.dataware.todolist.util.fileValidation.enums;

import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageMimeType {

    JPEG("image/jpeg", ".jpg"),
    PNG("image/png", ".png"),
    WEBP("image/webp", ".webp"),
    GIF("image/gif", ".gif"),
    HEIC("image/heic", ".heic"),
    HEIF("image/heif", ".heif");

    private final String mimeType;
    private final String extension;

    /**
     * Restituisce il {@link ImageMimeType} associato alla stringa MIME type
     * fornita.
     * <p>
     * Il confronto viene effettuato in modo case-insensitive.
     *
     * @param mimeType stringa MIME type rilevata (es. "image/jpeg")
     * @return {@link Optional} contenente il tipo immagine corrispondente,
     *         oppure {@link Optional#empty()} se il MIME type non è supportato
     */
    public static Optional<ImageMimeType> fromMimeType(String mimeType) {
        return Arrays.stream(ImageMimeType.values())
                .filter(value -> value.mimeType.equalsIgnoreCase(mimeType))
                .findFirst();
    }
}
