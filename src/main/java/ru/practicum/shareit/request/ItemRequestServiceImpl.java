package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestRepository.save(requestMapper.toModel(itemRequestDto, user));
        return requestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAll(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(userId, Sort.by("created").descending());

        return getItemRequestDtos(requests);
    }

    @Override
    public ItemRequestDto get(long userId, long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на вещь с идентификатором id: " + requestId + " не найден"));
        List<ItemDto> items = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        return requestMapper.toDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNot(userId, pageable);

        return getItemRequestDtos(requests);
    }

    private List<ItemRequestDto> getItemRequestDtos(List<ItemRequest> requests) {
        List<Long> requestIds = requests
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());


        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsReq = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(), Collectors.toList()));

        return requests.stream()
                .map(request -> {
                    List<ItemDto> itemsDto = itemsReq.getOrDefault(request.getId(), Collections.emptyList())
                            .stream()
                            .map(itemMapper::toDto)
                            .collect(Collectors.toList());
                    return requestMapper.toDto(request, itemsDto);
                })
                .collect(Collectors.toList());
    }
}
