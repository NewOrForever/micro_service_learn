package com.example.es;

import com.example.es.model.EsProduct;
import com.example.es.service.SpringDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName:SpringDataController
 * Package:com.example.es
 * Description:
 *
 * @Date:2024/1/18 16:47
 * @Author:qs@1.com
 */
@Controller
@RequestMapping("/esProduct")
public class SpringDataController {

    @Autowired
    private SpringDataService springDataService;


    @RequestMapping(value = "/create/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String create(@PathVariable Long id) {
        EsProduct esProduct = springDataService.create(id);

        return "";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String delete(@PathVariable Long id) {
        springDataService.delete(id);
        return "";
    }

    @RequestMapping(value = "/delete/batch", method = RequestMethod.GET)
    @ResponseBody
    public String delete(@RequestParam("ids") List<Long> ids) {
        springDataService.delete(ids);
        return "";
    }


    @RequestMapping(value = "/search/simple", method = RequestMethod.GET)
    @ResponseBody
    public String search(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                         @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Page<EsProduct> esProductPage = springDataService.search(keyword, pageNum, pageSize);
        System.out.println("esProductPage.toString():" + esProductPage.toString() + " esProductPage.getContent():" + esProductPage.getContent().size());
        return esProductPage.getContent().toString();
    }
}
