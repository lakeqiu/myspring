package com.lakeqiu.service.combine.impl;



import com.lakeqiu.entity.bo.HeadLine;
import com.lakeqiu.entity.bo.ShopCategory;
import com.lakeqiu.entity.dto.MainPageInfoDTO;
import com.lakeqiu.entity.dto.Result;
import com.lakeqiu.service.combine.HeadLineShopCategoryCombineService;
import com.lakeqiu.service.solo.HeadLineService;
import com.lakeqiu.service.solo.ShopCategoryService;
import org.myspringframework.core.annotation.Service;
import org.myspringframework.inject.annotation.Autowired;

import java.util.List;

@Service
public class HeadLineShopCategoryCombineServiceImpl implements HeadLineShopCategoryCombineService {
    @Autowired
    private HeadLineService headLineService;
    @Autowired
    private ShopCategoryService shopCategoryService;
    @Override
    public Result<MainPageInfoDTO> getMainPageInfo() {
        //1.获取头条列表
        HeadLine headLineCondition = new HeadLine();
        headLineCondition.setEnableStatus(1);
        Result<List<HeadLine>> HeadLineResult = headLineService.queryHeadLine(headLineCondition, 1, 4);
        //2.获取店铺类别列表
        ShopCategory shopCategoryCondition = new ShopCategory();
        Result<List<ShopCategory>> shopCategoryResult =shopCategoryService.queryShopCategory(shopCategoryCondition, 1, 100);
        //3.合并两者并返回
        Result<MainPageInfoDTO> result = mergeMainPageInfoResult(HeadLineResult, shopCategoryResult);
        return result;
    }

    private Result<MainPageInfoDTO> mergeMainPageInfoResult(Result<List<HeadLine>> headLineResult, Result<List<ShopCategory>> shopCategoryResult) {
        return  null;
    }
}
