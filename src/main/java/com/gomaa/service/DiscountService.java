package com.gomaa.service;

import com.gomaa.dto.DiscountDTO;
import com.gomaa.exception.ResourceNotFoundException;
import com.gomaa.model.Discount;
import com.gomaa.enums.DiscountStatus;
import com.gomaa.enums.DiscountType;
import com.gomaa.repository.DiscountRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;

    // ------------------- DTO Conversion -------------------

    private Discount toEntity(DiscountDTO dto) {
        Discount d = new Discount();
        d.setId(dto.getId());
        d.setCode(dto.getCode());
        d.setName(dto.getName());
        d.setType(DiscountType.valueOf(dto.getType()));
        d.setValue(dto.getValue());
        d.setUsageLimit(dto.getUsageLimit());
        d.setUsedCount(dto.getUsedCount() != null ? dto.getUsedCount() : 0);
        d.setStartDate(dto.getStartDate());
        d.setEndDate(dto.getEndDate());
        d.setActive(dto.getActive() != null ? dto.getActive() : true);
        return d;
    }

    private DiscountDTO toDto(Discount d) {
        return DiscountDTO.builder()
                .id(d.getId())
                .code(d.getCode())
                .name(d.getName())
                .type(d.getType().name())
                .value(d.getValue())
                .usageLimit(d.getUsageLimit())
                .usedCount(d.getUsedCount())
                .startDate(d.getStartDate())
                .endDate(d.getEndDate())
                .active(d.getActive())
                .status(calculateStatus(d).name())
                .build();
    }

    // ------------------- Status Logic -------------------

    private DiscountStatus calculateStatus(Discount d) {
        LocalDate today = LocalDate.now();

        if (!Boolean.TRUE.equals(d.getActive()))
            return DiscountStatus.DISABLED;

        if (d.getEndDate() != null && today.isAfter(d.getEndDate()))
            return DiscountStatus.EXPIRED;

        if (d.getEndDate() != null &&
                ChronoUnit.DAYS.between(today, d.getEndDate()) <= 7)
            return DiscountStatus.EXPIRING_SOON;

        return DiscountStatus.ACTIVE;
    }

    // ------------------- CRUD -------------------

    @Transactional
    public DiscountDTO create(DiscountDTO dto) {
        Discount saved = discountRepository.save(toEntity(dto));
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DiscountDTO> getAll() {
        return discountRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public DiscountDTO getById(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Discount with id " + id + " not found"));
        return toDto(discount);
    }

    @Transactional
    public DiscountDTO update(Long id, DiscountDTO dto) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Discount with id " + id + " not found"));

        discount.setCode(dto.getCode());
        discount.setName(dto.getName());
        discount.setType(DiscountType.valueOf(dto.getType()));
        discount.setValue(dto.getValue());
        discount.setUsageLimit(dto.getUsageLimit());
        discount.setStartDate(dto.getStartDate());
        discount.setEndDate(dto.getEndDate());
        discount.setActive(dto.getActive());

        return toDto(discountRepository.save(discount));
    }

    public void delete(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Discount with id " + id + " not found");
        }
        discountRepository.deleteById(id);
    }

    // ------------------- Export CSV -------------------

    @Transactional(readOnly = true)
    public void exportCsv(HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=discounts.csv");

        try (PrintWriter writer = response.getWriter()) {

            writer.println("Code,Name,Type,Value,Used,Limit,Start Date,End Date,Status");

            List<Discount> discounts = discountRepository.findAll();
            for (Discount d : discounts) {
                writer.println(
                        d.getCode() + "," +
                                d.getName() + "," +
                                d.getType() + "," +
                                (d.getValue() != null ? d.getValue() : "-") + "," +
                                d.getUsedCount() + "," +
                                (d.getUsageLimit() != null ? d.getUsageLimit() : "∞") + "," +
                                d.getStartDate() + "," +
                                d.getEndDate() + "," +
                                calculateStatus(d)
                );
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<DiscountDTO> search(String keyword, int page) {

        // default page size = 3
        PageRequest pageable = PageRequest.of(page, 3);

        return discountRepository
                .searchByCodeOrName(keyword, pageable)
                .map(this::toDto);
    }
}
