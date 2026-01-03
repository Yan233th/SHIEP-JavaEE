package com.sms.controller;

import com.sms.model.Menu;
import com.sms.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    public ResponseEntity<List<Menu>> getAllMenus() {
        return ResponseEntity.ok(menuService.getAllMenus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Menu> getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Menu> createMenu(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.createMenu(menu));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Menu> updateMenu(@PathVariable Long id, @RequestBody Menu menu) {
        try {
            return ResponseEntity.ok(menuService.updateMenu(id, menu));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.ok().build();
    }
}
