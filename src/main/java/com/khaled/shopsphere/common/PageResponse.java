package com.khaled.shopsphere.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private long totalPages;
    private boolean first;
    private boolean last;
}
