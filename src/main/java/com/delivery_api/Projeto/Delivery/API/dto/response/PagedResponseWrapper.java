package com.delivery_api.Projeto.Delivery.API.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Getter
@Schema(description = "Wrapper para encapsular respostas de paginação da API")
public class PagedResponseWrapper<T> {

    @Schema(description = "Lista de itens da página")
    private final List<T> content;

    @Schema(description = "Informações da paginação")
    private final PageInfo page;

    @Schema(description = "Links de navegação da página")
    private final PageLinks links;

    public PagedResponseWrapper(Page<T> page) {
        this.content = page.getContent();
        this.page = new PageInfo(page);
        this.links = new PageLinks(page);
    }

    @Getter
    @Schema(description = "Informações de paginação")
    public static class PageInfo {
        @Schema(description = "Número da página atual (base 0)", example = "0")
        private final int number;
        @Schema(description = "Tamanho da página", example = "10")
        private final int size;
        @Schema(description = "Total de elementos", example = "50")
        private final long totalElements;
        @Schema(description = "Total de páginas", example = "5")
        private final int totalPages;
        @Schema(description = "É a primeira página", example = "true")
        private final boolean first;
        @Schema(description = "É a última página", example = "false")
        private final boolean last;

        public PageInfo(Page<?> page) {
            this.number = page.getNumber();
            this.size = page.getSize();
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.first = page.isFirst();
            this.last = page.isLast();
        }
    }

    @Getter
    @Schema(description = "Links de navegação")
    public static class PageLinks {
        @Schema(description = "Link para primeira página")
        private String first;
        @Schema(description = "Link para última página")
        private String last;
        @Schema(description = "Link para próxima página")
        private String next;
        @Schema(description = "Link para página anterior")
        private String prev;

        public PageLinks(Page<?> page) {
            var uriBuilder = ServletUriComponentsBuilder.fromCurrentRequestUri();
            
            this.first = uriBuilder.replaceQueryParam("page", 0).build().toUriString();
            this.last = uriBuilder.replaceQueryParam("page", page.getTotalPages() > 0 ? page.getTotalPages() - 1 : 0).build().toUriString();

            if (page.hasNext()) {
                this.next = uriBuilder.replaceQueryParam("page", page.getNumber() + 1).build().toUriString();
            }

            if (page.hasPrevious()) {
                this.prev = uriBuilder.replaceQueryParam("page", page.getNumber() - 1).build().toUriString();
            }
        }
    }
}
