package com.sms.service;

import com.sms.model.Role;
import com.sms.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, Role roleDetails) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setName(roleDetails.getName());
        role.setDescription(roleDetails.getDescription());
        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
