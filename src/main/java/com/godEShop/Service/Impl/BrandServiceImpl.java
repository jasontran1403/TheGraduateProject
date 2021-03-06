package com.godEShop.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.godEShop.Dao.BrandDAO;
import com.godEShop.Entity.Brand;
import com.godEShop.Service.BrandService;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    BrandDAO brandDAO;

    @Override
    public List<Brand> findAll() {
	// TODO Auto-generated method stub
	// lấy tất cả danh sách hãng
	return brandDAO.findAll();
    }

    @Override
    public Brand getById(int id) {
	// TODO Auto-generated method stub
	return brandDAO.getById(id);
    }

    @Override
    public List<Integer> getTop4BrandByEvaluation() {
	// TODO Auto-generated method stub
	return brandDAO.getTop4BrandByEvaluation();
    }

    @Override
    public Brand create(Brand brand) {
	// TODO Auto-generated method stub
	return brandDAO.save(brand);
    }

    @Override
    public void delete(Integer id) {
	// TODO Auto-generated method stub
	Brand b = brandDAO.getById(id);
	b.setAvailable(true);
	brandDAO.save(b);
    }

    @Override
    public Brand update(Brand brand) {
	// TODO Auto-generated method stub
	return brandDAO.save(brand);
    }


    @Override
    public List<Brand> getAllBrandByName(String name) {
	// TODO Auto-generated method stub
	return brandDAO.getAllBrandByName(name);
    }

    @Override
    public List<Brand> findAllBrand() {
	// TODO Auto-generated method stub
	return brandDAO.findAllBrand();
    }

}
