package by.innowise.order_service.controller;

import by.innowise.order_service.dto.item.CreateItemDto;
import by.innowise.order_service.dto.item.ItemDto;
import by.innowise.order_service.dto.item.ItemFilterDto;
import by.innowise.order_service.dto.item.UpdateItemDto;
import by.innowise.order_service.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody @Valid CreateItemDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.createItem(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id, @RequestBody @Valid UpdateItemDto dto) {
        return ResponseEntity.ok(itemService.updateItem(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(itemService.getItemDtoById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ItemDto>> getItemsWithFilters(@ModelAttribute ItemFilterDto dto,
                                                             @PageableDefault(sort = "id") Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(itemService.getItemsWithFilters(dto, pageable));
    }
}
