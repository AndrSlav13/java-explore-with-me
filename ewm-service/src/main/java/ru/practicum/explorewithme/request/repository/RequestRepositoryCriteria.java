package ru.practicum.explorewithme.request.repository;

import ru.practicum.explorewithme.request.model.StatusEventParticipation;

import java.util.List;
import java.util.Map;

public interface RequestRepositoryCriteria {
    Map<Long, Long> countAllByEventIdInAndStatusIn(List<Long> idsEvent, List<StatusEventParticipation> status);
}
