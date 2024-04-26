package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.forDto.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                            @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Поступил POST-запрос в /requests");
        ItemRequestDto createdRequest = itemRequestService.create(requestorId, itemRequestDto);
        log.info("POST-запрос /requests был обработан: {}", createdRequest);
        return createdRequest;
    }

    @GetMapping
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.info("Поступил GET-запрос в /requests");
        List<ItemRequestDto> requests = itemRequestService.getAll(requestorId);
        log.info("GET-запрос /requests был обработан: {}", requests);
        return requests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") long requestorId, @PathVariable long requestId) {
        log.info("Поступил GET-запрос в /requests/{}", requestId);
        ItemRequestDto request = itemRequestService.get(requestorId, requestId);
        log.info("GET-запрос /requests/{} был обработан: {}", requestId, request);
        return request;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                                   @RequestParam(defaultValue = "0") @Min(value = 0) int from,
                                                   @RequestParam(defaultValue = "10") @Min(value = 1) int size) {
        log.info("Поступил GET-запрос в /requests/all");
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequestDto> requests = itemRequestService.getAll(requestorId, pageable);
        log.info("GET-запрос /requests/all был обработан: {}", requests);
        return requests;
    }
}
