package com.godEShop.Service;

import java.util.List;

import com.godEShop.Dto.ProductAccessoryInfoDto;
import com.godEShop.Entity.Accessory;
import com.godEShop.Entity.Watch;

public interface AccessoryService {
	 List<ProductAccessoryInfoDto> lstFullInfoAccessory();

	Accessory create(Accessory ac);

	Accessory getById(Integer accessoryId);

	Accessory update(Accessory ac);

	
}
