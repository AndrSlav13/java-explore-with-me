package ru.practicum.explorewithme.request.model;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class RequestId implements Serializable {
    private Long id;
}