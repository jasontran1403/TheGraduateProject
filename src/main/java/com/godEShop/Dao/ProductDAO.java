package com.godEShop.Dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.godEShop.Dto.AccessoryDto;
import com.godEShop.Dto.ProductDiscountDto;
import com.godEShop.Dto.ProductShopDto;
import com.godEShop.Dto.ProductWatchInfoDto;
import com.godEShop.Dto.WatchDto;
import com.godEShop.Entity.Product;

@Repository
public interface ProductDAO extends JpaRepository<Product, Long> {

    @Query("SELECT new com.godEShop.Dto.ProductShopDto"
	    + "(p.id, c.id, p.name, p.price, p.createDate, c.name, MIN(pp.id), CAST(AVG(pe.evaluation) AS int), pd.discount, p.detail, MAX(pd.endDate), p.quantity) "
	    + "FROM Product p " 
	    + "FULL JOIN p.productPhotos pp " 
	    + "FULL JOIN p.productEvaluations pe "
	    + "FULL JOIN p.brand pb " 
	    + "FULL JOIN p.productDiscounts pd " 
	    + "FULL JOIN p.category c "
	    + "GROUP BY p.id, c.id, p.name, p.price, p.createDate, c.name, pd.discount, p.detail, p.quantity")
    List<ProductShopDto> findAllProduct();

    // ------------------------------------------------------------------------
    // Product for shop page
    @Query("SELECT new com.godEShop.Dto.ProductShopDto"
	    + "(p.id, c.id, p.name, p.price, p.createDate, c.name, MIN(pp.id), CAST(AVG(pe.evaluation) AS int), pd.discount, p.detail, MAX(pd.endDate), p.quantity) "
	    + "FROM Product p " 
	    + "FULL JOIN p.productPhotos pp " 
	    + "FULL JOIN p.productEvaluations pe "
	    + "FULL JOIN p.brand pb " 
	    + "FULL JOIN p.productDiscounts pd " 
	    + "FULL JOIN p.category c "
	    + "WHERE p.isDeleted = 0 AND c.available = 0 AND (p.name LIKE ?1 OR c.name LIKE ?1) AND c.name LIKE ?2 AND pb.name LIKE ?3 "
	    + "GROUP BY p.id, c.id, p.name, p.price, p.createDate, c.name, pd.discount, p.detail, p.quantity "
	    + "HAVING MAX(pd.endDate) >= GETDATE()")
    Page<ProductShopDto> productShop(String kws, String categoryName, String brandName, Pageable pageable);

    // -------------------------------------------------------------------------
    // Product for deal of the day
    @Query("SELECT new com.godEShop.Dto.ProductDiscountDto"
	    + "(p.id, p.name, p.price, pd.discount, pe.evaluation, MIN(pp.id), pd.endDate, p.detail) "
	    + "FROM Product p " 
	    + "FULL JOIN p.productPhotos pp " 
	    + "FULL JOIN p.productEvaluations pe "
	    + "FULL JOIN p.productDiscounts pd "
	    + "WHERE p.isDeleted = 0 AND pd.discount > 0 "
	    + "GROUP BY p.id, p.name, p.price, pd.discount, pe.evaluation, pd.endDate, p.detail "
	    + "HAVING MAX(pd.endDate) >= GETDATE() "
	    + "ORDER BY pd.endDate ASC")
    List<ProductDiscountDto> productDealOfTheDay();

    // -------------------------------------------------------------------------
    // Product for best seller
    @Query("SELECT new com.godEShop.Dto.ProductDiscountDto"
	    + "(p.id, p.name, p.price, pd.discount, pe.evaluation, MIN(pp.id), pd.endDate, p.detail) "
	    + "FROM Product p " 
	    + "FULL JOIN p.productPhotos pp " 
	    + "FULL JOIN p.productEvaluations pe "
	    + "FULL JOIN p.productDiscounts pd " 
	    + "FULL JOIN p.orderDetails pod " 
	    + "WHERE p.isDeleted = 0 "
	    + "GROUP BY p.id, p.name, p.price, pd.discount, pe.evaluation, pd.endDate, p.detail "
	    + "ORDER BY COUNT(pod.product.id) DESC")
    List<ProductDiscountDto> productBestSeller();

    // -------------------------------------------------------------------------
    // Product for new arrivals
    @Query("SELECT new com.godEShop.Dto.ProductDiscountDto"
	    + "(p.id, p.name, p.price, pd.discount, pe.evaluation, MIN(pp.id), pd.endDate, p.detail) "
	    + "FROM Product p " 
	    + "FULL JOIN p.productPhotos pp " 
	    + "FULL JOIN p.productEvaluations pe "
	    + "FULL JOIN p.productDiscounts pd " 
	    + "FULL JOIN p.orderDetails pod " 
	    + "WHERE p.isDeleted = 0 "
	    + "GROUP BY p.id, p.name, p.price, pd.discount, pe.evaluation, pd.endDate, p.detail, p.createDate "
	    + "ORDER BY p.createDate DESC")
    List<ProductDiscountDto> productNewArrivals();

    // -------------------------------------------------------------------------
    @Query("SELECT new com.godEShop.Dto.ProductDiscountDto"
	    + "(p.id, p.name, p.price, pd.discount, pe.evaluation, MIN(pp.id), MAX(pd.endDate), p.detail) "
	    + "FROM Product p " 
	    + "FULL JOIN p.productPhotos pp " 
	    + "FULL JOIN p.productEvaluations pe "
	    + "FULL JOIN p.productDiscounts pd " 
	    + "FULL JOIN p.orderDetails pod "
	    + "WHERE p.isDeleted = 0 AND p.brand.id=?1 "
	    + "GROUP BY p.id, p.name, p.price, pd.discount, pe.evaluation, p.detail, p.createDate "
	    + "ORDER BY p.price DESC")
    List<ProductDiscountDto> productByIdBrands(Integer id);

    // -------------------------------------------------------------------------
    @Query("SELECT new com.godEShop.Dto.ProductShopDto"
	    + "(p.id, c.id, p.name, p.price, p.createDate, c.name, MIN(pp.id), CAST(AVG(pe.evaluation) AS int), pd.discount, p.detail, MAX(pd.endDate), p.quantity) "
	    + "FROM Product p " 
	    + "FULL JOIN p.productPhotos pp " 
	    + "FULL JOIN p.productEvaluations pe "
	    + "FULL JOIN p.brand pb " 
	    + "FULL JOIN p.productDiscounts pd " 
	    + "FULL JOIN p.category c "
	    + "WHERE p.id = ?1 AND p.isDeleted = 0 "
	    + "GROUP BY p.id, c.id, p.name, p.price, p.createDate, c.name, pd.discount, p.detail, p.quantity")
    ProductShopDto productShopById(Long id);

    // -------------------------------------------------------------------------
    @Query("SELECT new com.godEShop.Dto.WatchDto"
	    + "(p.id, p.name, c.name, pb.name, p.madeIn, p.warranty, w.glassSizes, w.atm, w.glassColors, w.caseColors, gm.name, bm.name, mi.name) "
	    + "FROM Product p " 
	    + "FULL JOIN p.watches w " 
	    + "FULL JOIN p.brand pb " 
	    + "FULL JOIN w.glassMaterial gm "
	    + "FULL JOIN w.braceletMaterial bm " 
	    + "FULL JOIN w.machineInside mi " 
	    + "FULL JOIN p.category c "
	    + "WHERE p.id = ?1 AND p.isDeleted = 0 "
	    + "GROUP BY p.id, p.name, c.name, pb.name, p.madeIn, p.warranty, w.glassSizes, w.atm, w.glassColors, w.caseColors, gm.name, bm.name, mi.name")
    WatchDto getWatchById(Long id);

    // -------------------------------------------------------------------------

    @Query("SELECT new com.godEShop.Dto.AccessoryDto"
	    + "(p.id, p.name, c.name, pb.name, p.madeIn, p.warranty, bm.name, a.colors) " 
	    + "FROM Product p "
	    + "FULL JOIN p.accessories a " 
	    + "FULL JOIN p.brand pb " 
	    + "FULL JOIN a.braceletMaterial bm "
	    + "FULL JOIN p.category c " 
	    + "WHERE p.id = ?1 AND p.isDeleted = 0 "
	    + "GROUP BY p.id, p.name, c.name, pb.name, p.madeIn, p.warranty, bm.name, a.colors")
    AccessoryDto getAccessoryDtoById(Long id);

    // ---------------------------------------------------------------------------

    @Query("SELECT new com.godEShop.Dto.ProductWatchInfoDto"
	    + "(p.id, p.name, p.isDeleted, p.quantity, p.price, p.createDate, p.warranty, p.madeIn, p.detail, pb.id, c.id, MIN(pp.id), w.id, w.gender, w.glassSizes, w.atm, w.glassColors, w.caseColors, gm.id, bm.id, mi.id, pb.name, c.name, gm.name, bm.name, mi.name) "
	    + "FROM Product p " 
	    + "INNER JOIN p.watches w " 
	    + "INNER JOIN p.productPhotos pp "
	    + "INNER JOIN p.brand pb " 
	    + "INNER JOIN w.glassMaterial gm " 
	    + "INNER JOIN w.braceletMaterial bm "
	    + "INNER JOIN w.machineInside mi " 
	    + "INNER JOIN p.category c "
	    + "GROUP BY p.id, p.name, p.isDeleted, p.quantity, p.price, p.createDate, p.warranty, p.madeIn, p.detail, pb.id, c.id, w.id, w.gender, w.glassSizes, w.atm, w.glassColors, w.caseColors, gm.id, bm.id, mi.id, pb.name, c.name, gm.name, bm.name, mi.name")
    List<ProductWatchInfoDto> lstFullInfoWatch();

    // ---------------------------------------------------------------------------
    
    
    @Query("SELECT new com.godEShop.Dto.ProductWatchInfoDto"
	    + "(p.id, p.name, p.isDeleted, p.quantity, p.price, p.createDate, p.warranty, p.madeIn, p.detail, pb.id, c.id, MIN(pp.id), w.id, w.gender, w.glassSizes, w.atm, w.glassColors, w.caseColors, gm.id, bm.id, mi.id, pb.name, c.name, gm.name, bm.name, mi.name) "
	    + "FROM Product p " 
	    + "INNER JOIN p.watches w " 
	    + "INNER JOIN p.productPhotos pp "
	    + "INNER JOIN p.brand pb " 
	    + "INNER JOIN w.glassMaterial gm " 
	    + "INNER JOIN w.braceletMaterial bm "
	    + "INNER JOIN w.machineInside mi " 
	    + "INNER JOIN p.category c "
	    + "WHERE p.name LIKE ?1 OR c.name LIKE ?1 "
	    + "GROUP BY p.id, p.name, p.isDeleted, p.quantity, p.price, p.createDate, p.warranty, p.madeIn, p.detail, pb.id, c.id, w.id, w.gender, w.glassSizes, w.atm, w.glassColors, w.caseColors, gm.id, bm.id, mi.id, pb.name, c.name, gm.name, bm.name, mi.name")
    List<ProductWatchInfoDto> lstSearchFullInfoWatch(String name);
    
    // ---------------------------------------------------------------------------
    @Query("SELECT p FROM Product p "   		
    		+ "WHERE p.name = ?1")
    List<Product> findByNameOrderDetail(String productName);
}
