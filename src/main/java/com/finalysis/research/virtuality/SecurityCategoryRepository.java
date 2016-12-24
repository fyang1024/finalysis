package com.finalysis.research.virtuality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityCategoryRepository extends JpaRepository<SecurityCategory, Integer> {

    SecurityCategory findByName(String name);

}
