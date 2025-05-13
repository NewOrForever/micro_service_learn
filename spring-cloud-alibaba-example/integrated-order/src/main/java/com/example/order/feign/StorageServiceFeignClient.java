package com.example.order.feign;

import com.example.common.Result;
import com.example.order.feign.dto.StorageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * ClassName:StorageServiceFeignClient
 * Package:com.example.order.feign
 * Description:
 *
 * @Date:2025/3/27 17:16
 * @Author:qs@1.com
 */
@FeignClient(name = "integrated-storage")
public interface StorageServiceFeignClient {
    /**
     * 扣减库存
     * @param productReduceStockDTO
     * @return
     */
    @PostMapping("/storage/reduce-stock")
    Result<?> reduceStock(@RequestBody StorageDTO productReduceStockDTO);
}
