package ru.practicum.explorewithme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ReturnStatDTO {
    String app;
    String uri;
    Long hits;   //number of appeals
}