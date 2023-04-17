package ru.practicum.explorewithme.compilation.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.compilation.dto.CompilationDTO;
import ru.practicum.explorewithme.compilation.model.Compilation;
import ru.practicum.explorewithme.compilation.repository.CompilationRepository;
import ru.practicum.explorewithme.event.dto.EventDTO;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.service.EventService;
import ru.practicum.explorewithme.exceptions.ApiErrorException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, EventService eventService) {
        this.compilationRepository = compilationRepository;
        this.eventService = eventService;
    }

    @Override
    public List<CompilationDTO.Controller.CompilationDto> findAllUsePinnedFlag(Boolean pinned, int from, int size) {
        Pageable pg = PageRequest.of(from, size);
        List<Compilation> compilations = compilationRepository.findAllUsePinnedFlag(pinned, pg);
        List<EventDTO.Controller.EventShortDto> events = eventService.findEventsByIdInDTO(compilationRepository.findIdsEventsByIdsCompils(compilations.stream().map(a -> a.getId()).collect(Collectors.toList())));
        Map<Long, EventDTO.Controller.EventShortDto> mapEventDto = events.stream().collect(Collectors.toMap(a -> a.getId(), a -> a, (a, b) -> a));
        return compilations.stream().map(
                a -> CompilationDTO.Controller.CompilationDto.builder()
                        .id(a.getId())
                        .pinned(a.getPinned())
                        .title(a.getTitle())
                        .events(a.getEvents().stream().map(b -> mapEventDto.get(b.getId())).collect(Collectors.toList()))
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public Compilation findCompilationById(Long id) {
        return compilationRepository.findById(id).orElseThrow(() ->
                new ApiErrorException(HttpStatus.NOT_FOUND,
                        "Compilation with id=" + id + " was not found",
                        "The required object was not found."));
    }

    @Override
    public CompilationDTO.Controller.CompilationDto findCompilationByIdDTO(Long id) {
        Compilation comp = findCompilationById(id);
        List<EventDTO.Controller.EventShortDto> events = eventService.findEventsByIdInDTO(compilationRepository.findIdsEventsByIdsCompils(List.of(comp.getId())));
        return CompilationDTO.Controller.CompilationDto.builder()
                .title(comp.getTitle())
                .pinned(comp.getPinned())
                .id(comp.getId())
                .events(events)
                .build();
    }

    @Transactional
    @Override
    public CompilationDTO.Controller.CompilationDto save(CompilationDTO.Controller.NewCompilationDto compDto) {
        Compilation comp = Compilation.builder()
                .pinned(compDto.getPinned())
                .title(compDto.getTitle())
                .id(null)
                .build();
        comp = compilationRepository.save(comp);

        List<EventDTO.Controller.EventShortDto> eventShortDtos = eventService.findEventsByIdInDTO(compDto.getEvents());
        return CompilationDTO.Controller.CompilationDto.builder()
                .id(comp.getId())
                .title(comp.getTitle())
                .pinned(comp.getPinned())
                .events(eventShortDtos)
                .build();
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Compilation comp = findCompilationById(id);
        compilationRepository.delete(comp);
    }

    @Transactional
    @Override
    public CompilationDTO.Controller.CompilationDto patchCompilationById(CompilationDTO.Controller.UpdateCompilationRequest compDto, Long id) {
        Compilation comp = findCompilationById(id);
        List<Event> events = eventService.findEventsByIdIn(compDto.getEvents());
        if (compDto.getPinned() != null) comp.setPinned(compDto.getPinned());
        if (compDto.getTitle() != null) comp.setTitle(compDto.getTitle());
        comp.removeEvents();
        events.stream().forEach(a -> comp.addEvent(a));
        List<EventDTO.Controller.EventShortDto> eventShortDtos = eventService.findEventsByIdInDTO(compDto.getEvents());
        return CompilationDTO.Controller.CompilationDto.builder()
                .id(comp.getId())
                .title(comp.getTitle())
                .pinned(comp.getPinned())
                .events(eventShortDtos)
                .build();
    }
}
