package ru.practicum.explorewithme.statistics.service;

import ru.practicum.explorewithme.statistics.model.Statistics;

import java.util.Comparator;

public interface StatisticsServiceSort {
    Comparator<Statistics> comparator = (b1, b2) -> {
        if (b1.getId().equals(b2.getId())) return 0;
        return b1.getId() < b2.getId() ? 1 : -1;
    };
}
