package com.fanaka.protekt.dto;

import java.util.List;

public class PaginationDto<T> {
    private List<T> data;
    private int page;
    private int size;
    private long total;

    public PaginationDto() {}

    public PaginationDto(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.data = content;
        this.page = pageNumber;
        this.size = pageSize;
        this.total = totalElements;
    }

    // Getters and setters
    public List<T> getContent() {
        return data;
    }

    public void setContent(List<T> content) {
        this.data = content;
    }

    public int getPageNumber() {
        return page;
    }

    public void setPageNumber(int pageNumber) {
        this.page = pageNumber;
    }

    public int getPageSize() {
        return size;
    }

    public void setPageSize(int pageSize) {
        this.size = pageSize;
    }

    public long getTotalElements() {
        return total;
    }

    public void setTotalElements(long totalElements) {
        this.total = totalElements;
    }
}