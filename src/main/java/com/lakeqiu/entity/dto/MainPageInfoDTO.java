package com.lakeqiu.entity.dto;

import com.lakeqiu.entity.bo.HeadLine;
import com.lakeqiu.entity.bo.ShopCategory;
import lombok.Data;

import java.util.List;

@Data
public class MainPageInfoDTO {
    private List<HeadLine> headLineList;
    private List<ShopCategory> shopCategoryList;
}
