package com.passdeveloperblog.estore.ProductsService.core.data;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "product_lookup")
public class ProductLookupEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String productId;
    
    @Column(unique = true)
    private String title;
}
