package ru.practicum.compilationservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilationservice.service.CompilationService;
import ru.practicum.interactionapi.dto.compilationservice.CompilationDto;
import ru.practicum.interactionapi.dto.compilationservice.CreateCompilationDto;
import ru.practicum.interactionapi.dto.compilationservice.UpdateCompilationDto;
import ru.practicum.interactionapi.exception.compilationservice.CompilationNotFoundException;

@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@RestController
@Slf4j
public class AdminCompilationController {
    /**
     * Сервис для сущности "Подборка событий".
     */
    private final CompilationService compilationService;

    /**
     * Добавить новую подборку событий.
     *
     * @param createCompilationDto трансферный объект, содержащий данные для создания подборки событий.
     * @return трансферный объект, содержащий данные о подборке событий.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid CreateCompilationDto createCompilationDto) {
        log.info("Create compilation - {}", createCompilationDto);
        return compilationService.createCompilation(createCompilationDto);
    }

    /**
     * Обновить подборку событий.
     *
     * @param compilationId        идентификатор подборки событий.
     * @param updateCompilationDto трансферный объект, содержащий данные для обновления подборки событий.
     * @return трансферный объект, содержащий данные о подборке событий.
     * @throws CompilationNotFoundException подборка событий с идентификатором {@code compilationId} не найдена.
     */
    @PatchMapping("/{compilationId}")
    public CompilationDto updateCompilation(@PathVariable @Positive Long compilationId,
                                            @RequestBody @Valid UpdateCompilationDto updateCompilationDto) throws CompilationNotFoundException {
        log.info("Update compilation {} with id = {}", updateCompilationDto, compilationId);
        return compilationService.updateCompilation(compilationId, updateCompilationDto);
    }

    /**
     * Удалить подборку событий.
     *
     * @param compilationId идентификатор подборки событий.
     * @throws CompilationNotFoundException подборка событий с идентификатором {@code compilationId} не найдена.
     */
    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive Long compilationId) throws CompilationNotFoundException {
        log.info("Delete compilation with id = {}", compilationId);
        compilationService.deleteCompilation(compilationId);
    }
}
