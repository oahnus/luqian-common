package com.github.oahnus.luqiancommon.config.props;

import lombok.Data;

/**
 * Created by oahnus on 2019/10/18
 * 13:47.
 */
@Data
public class ZkProperties {
    private String connectStr;
    private Integer retryTime = 1000;
}
