package com.jmehta.shopme.admin.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jmehta.shopme.common.entity.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {

}
