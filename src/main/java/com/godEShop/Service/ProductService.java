package com.godEShop.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.godEShop.Dto.AccessoryDto;
import com.godEShop.Dto.ProductDiscountDto;
import com.godEShop.Dto.ProductShopDto;
import com.godEShop.Dto.WatchDto;

public interface ProductService {
    //-------------------------
    Page<ProductShopDto> productShop(String kws, String categoryName, String brandName,Pageable pageable);

    //-------------------------
    List<ProductDiscountDto> productDealOfTheDay();
    
    //-------------------------
    List<ProductDiscountDto> productBestSeller();
    
    //-------------------------
    List<ProductDiscountDto> productNewArrivals();
    
    //-------------------------
    List<ProductDiscountDto> productByIdBrands(Integer id);
    
    //-------------------------
    ProductShopDto productShopById(Long id);
    
    //-------------------------
    WatchDto getWatchById(Long id);
    
    //-------------------------
    AccessoryDto getAccessoryDtoById(Long id);
}