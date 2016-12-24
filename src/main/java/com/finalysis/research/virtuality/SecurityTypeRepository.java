package com.finalysis.research.virtuality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityTypeRepository extends JpaRepository<SecurityType, Integer> {

    SecurityType findByName(String name);

}
