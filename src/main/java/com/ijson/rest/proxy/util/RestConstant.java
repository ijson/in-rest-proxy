package com.ijson.rest.proxy.util;

/**
 * Created by cuiyongxu on 20/05/2017.
 */
public interface RestConstant {
    enum HeaderKey {
        Enterprise("FSR-EA"), EnterpriseEI("FSR-TenantId"), UserId("FSR-UserId"), Business("FSR-Business"), Status("FSR-Status"), Error_Msg("Error-Msg");
        private String value;

        HeaderKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    enum HeaderStatus {
        Success("FSR-Success"), Error_System("FSR-Error-System"), Error_Business("FSR-Error-Business"), Error_Arg("FSR-Error-Arg");
        private String value;

        HeaderStatus(String name) {
            this.value = name;
        }

        public String getValue() {
            return value;
        }
    }

}
