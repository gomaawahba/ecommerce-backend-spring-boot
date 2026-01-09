package com.gomaa.repository;

import com.gomaa.dto.DiscountDTO;
import com.gomaa.model.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query("""
        SELECT d FROM Discount d
        WHERE LOWER(d.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Discount> searchByCodeOrName(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
