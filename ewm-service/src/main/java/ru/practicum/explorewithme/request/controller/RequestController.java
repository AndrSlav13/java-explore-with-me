package ru.practicum.explorewithme.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.request.dto.RequestDTO;
import ru.practicum.explorewithme.request.service.RequestService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Validated
public class RequestController {
    private final RequestService requestService;

    @GetMapping(path = "/users/{userId}/requests")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public List<RequestDTO.Controller.ParticipationRequestDto> getRequestsByUserId(@PathVariable Long userId,
                                                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        return requestService.findRequestsByUserId(userId, from, size);
    }

    @PostMapping(path = "/users/{userId}/requests")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.CREATED)
    public RequestDTO.Controller.ParticipationRequestDto addRequest(@PathVariable Long userId, @NotNull @RequestParam Long eventId) {
        return requestService.save(userId, eventId);
    }

    @PatchMapping(path = "/users/{userId}/requests/{requestId}/cancel")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public RequestDTO.Controller.ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancel(userId, requestId);
    }

    @GetMapping(path = "/users/{userId}/events/{eventId}/requests")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public List<RequestDTO.Controller.ParticipationRequestDto> getRequestsForEventOfUser(@PathVariable Long userId,
                                                                                         @PathVariable Long eventId) {
        return requestService.findRequestsForEventOfUser(userId, eventId);
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}/requests")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public RequestDTO.Controller.EventRequestStatusUpdateResult patchRequestsForEventsOfUser(@RequestBody RequestDTO.Controller.EventRequestStatusUpdateRequest updateRequest,
                                                                                             @PathVariable Long userId,
                                                                                             @PathVariable Long eventId) {
        return requestService.patchRequestsForEventsOfUser(updateRequest, userId, eventId);
    }
}
