package com.example.storage.controller;

import com.example.common.BusinessException;
import com.example.common.Result;
import com.example.storage.dto.StorageDTO;
import com.example.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName:OrderController
 * Package:com.example.order.controller
 * Description:
 *
 * @Date:2025/3/27 17:29
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/reduce-stock")
    public Result<?> reduceStock(@RequestBody StorageDTO storageDTO) {
        try {
            storageService.reduceStock(storageDTO.getCommodityCode(),
                    storageDTO.getCount());
        }
        catch (BusinessException e) {
            return Result.failed(e.getMessage());
        }
        return Result.success("");
    }

    @GetMapping("/")
    public Result<?> getRemainCount(String commodityCode) {
        return storageService.getRemainCount(commodityCode);
    }

}