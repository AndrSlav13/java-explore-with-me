package ru.practicum.explorewithme.compilation.service;

import ru.practicum.explorewithme.compilation.dto.CompilationDTO;
import ru.practicum.explorewithme.compilation.model.Compilation;

import java.util.List;

public interface CompilationService {
    List<CompilationDTO.Controller.CompilationDto> findAllUsePinnedFlag(Boolean pinned, int from, int size);

    CompilationDTO.Controller.CompilationDto save(CompilationDTO.Controller.NewCompilationDto compDto);

    void delete(Long id);

    CompilationDTO.Controller.CompilationDto patchCompilationById(CompilationDTO.Controller.UpdateCompilationRequest compilation, Long compId);

    Compilation findCompilationById(Long id);

    CompilationDTO.Controller.CompilationDto findCompilationByIdDTO(Long id);
}
