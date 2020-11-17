
package com.ijson.rest.proxy.example.model;

import lombok.Data;


/**
 * Created by cuiyongxu on 17/7/15.
 */
public interface PostIP {
    @Data
    class Result extends BaseResult {
        private IPInfoEntity data;
        private String msg;
    }

    @Data
    class IPInfoEntity {
        private String ip;
        private String country;
        private String area;
        private String region;
        private String city;
        private String county;
        private String isp;
        private String country_id;
        private String area_id;
        private String region_id;
        private String city_id;
        private String county_id;
        private String isp_id;

    }
}
