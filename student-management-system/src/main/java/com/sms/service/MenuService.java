package com.sms.service;

import com.sms.model.Menu;
import com.sms.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public Optional<Menu> getMenuById(Long id) {
        return menuRepository.findById(id);
    }

    public Menu createMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu updateMenu(Long id, Menu menuDetails) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found"));
        menu.setName(menuDetails.getName());
        menu.setPath(menuDetails.getPath());
        menu.setIcon(menuDetails.getIcon());
        menu.setParentId(menuDetails.getParentId());
        menu.setSortOrder(menuDetails.getSortOrder());
        return menuRepository.save(menu);
    }

    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }
}
