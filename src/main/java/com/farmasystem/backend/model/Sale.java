package com.farmasystem.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "sales")
public class Sale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_doc")
    private String clientDoc;

    @Column(nullable = false)
    private BigDecimal total;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleDetail> details;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientDoc() {
        return clientDoc;
    }

    public void setClientDoc(String clientDoc) {
        this.clientDoc = clientDoc;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<SaleDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SaleDetail> details) {
        this.details = details;
    }
}