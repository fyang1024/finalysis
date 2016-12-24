package com.finalysis.research.reality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementRoleRepository extends JpaRepository<ManagementRole, Integer> {

    ManagementRole findByName(String name);

}
