package com.godEShop.Entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The persistent class for the ProductDetails database table.
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ProductDetails")
public class ProductDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "DetailKey")
    private String detailKey;

    @Column(name = "DetailValue")
    private String detailValue;

    // bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "ProductId")
    private Product product;

}