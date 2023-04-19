package ru.practicum.explorewithme.compilation.controller;
/**
 * Функции с суффиксом DTO возвращают самый часто используемый DTO, либо самый универсальный
 * Функции без индекса возвращают сущности JPA
 */

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.compilation.dto.CompilationDTO;
import ru.practicum.explorewithme.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Validated
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping(path = "/compilations")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public List<CompilationDTO.Controller.CompilationDto> getCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        return compilationService.findAllUsePinnedFlag(pinned, from, size);
    }

    @GetMapping(path = "/compilations/{compId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public CompilationDTO.Controller.CompilationDto getCompilationById(@PathVariable Long compId) {
        return compilationService.findCompilationByIdDTO(compId);
    }

    @PostMapping(path = "/admin/compilations")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDTO.Controller.CompilationDto addCompilation(@Valid @NotNull @RequestBody(required = false) CompilationDTO.Controller.NewCompilationDto compilation) {
        return compilationService.save(compilation);
    }

    @DeleteMapping(path = "/admin/compilations/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.delete(compId);
    }

    @PatchMapping(path = "/admin/compilations/{compId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public CompilationDTO.Controller.CompilationDto patchCompilationById(@RequestBody @Valid CompilationDTO.Controller.UpdateCompilationRequest compDto,
                                                                         @PathVariable Long compId) {
        return compilationService.patchCompilationById(compDto, compId);
    }
}
