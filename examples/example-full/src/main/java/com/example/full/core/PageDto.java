package com.example.full.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T> {
    @Builder.Default
    private List<T> content = Collections.emptyList();

    private int  pageNumber;
    private int  pageSize;
    private int  pageElements;
    private int  totalPages;
    private long totalElements;

    private boolean empty;
    private boolean first;
    private boolean last;

    public static <T> PageDto<T> of(Page<T> page) {
        return PageDto.<T>builder()
                      .content(page.getContent())
                      .pageNumber(page.getNumber())
                      .pageSize(page.getSize())
                      .pageElements(page.getNumberOfElements())
                      .totalPages(page.getTotalPages())
                      .totalElements(page.getTotalElements())
                      .empty(page.isEmpty())
                      .first(page.isFirst())
                      .last(page.isLast())
                      .build();
    }
}
