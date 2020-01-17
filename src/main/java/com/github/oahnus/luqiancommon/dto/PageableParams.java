package com.github.oahnus.luqiancommon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Created by oahnus on 2019/10/17
 * 18:10.
 */
@Data
public class PageableParams {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    @JsonIgnore
    private String orderProp = "id"; // 排序属性值
    @JsonIgnore
    private String orderDir = "ASC"; // 排序方向

    @JsonIgnore
    public Integer getPageNum() {
        if (pageNum == null || pageNum <= 0) {
            return 1;
        }
        return pageNum;
    }

    @JsonIgnore
    public Integer getPageSize() {
        if (pageSize == null || pageSize <= 0) {
            return 10;
        }
        return pageSize;
    }
}
