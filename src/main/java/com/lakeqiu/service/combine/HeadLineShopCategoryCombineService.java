package com.lakeqiu.service.combine;

import com.lakeqiu.entity.dto.MainPageInfoDTO;
import com.lakeqiu.entity.dto.Result;

public interface HeadLineShopCategoryCombineService {
    Result<MainPageInfoDTO> getMainPageInfo();
}
