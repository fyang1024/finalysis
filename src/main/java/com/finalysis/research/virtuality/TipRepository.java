package com.finalysis.research.virtuality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TipRepository extends JpaRepository<Tip, Integer> {

    @Query("select min(tipDay) from Tip" )
    Date findEarliestTipDay();
}
