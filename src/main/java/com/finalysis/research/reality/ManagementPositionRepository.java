package com.finalysis.research.reality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagementPositionRepository extends JpaRepository<ManagementPosition, Integer> {

    ManagementPosition findByPersonAndCompanyAndManagementRole(Person person, Company company, ManagementRole role);

    List<ManagementPosition> findByCompany(Company company);

}
