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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "RefAccounts")
public class RefAccount implements Serializable{
    
    private static final long serialVersionUID = 1L;
        
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "Refer")
    private String refer; // người nhập mã giới thiệu
    
    @Column(name = " IsReward")
    private Boolean isReward = false;
    
    @ManyToOne
    @JoinColumn(name = "Receiver")
    Account account; // người giới thiệu
    
    
    
}
