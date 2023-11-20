package com.mcmp.controller.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
*
* @author : Jihyeong Lee
* @Project : mcmp-conf/controller
* @version : 1.0.0
* @date : 11/7/23
* @class-description : definition class of playbook path
*
**/
public class PlaybookPath {

    public static final String DEPLOY_APPLICATION = "/service/deploy-application.yml";
    public static final String ACTIVATION_SERVICE = "/service/activation.yml";
    public static final String DEACTIVATION_SERVICE = "/service/deactivation.yml";
    public static final String HEALTH_CHECKING_SERVICE = "/service/app-health-checking.yml";
    public static final String WEB_DEPLOY = "/across-service/deploy-web.yml";
    public static final String DB_DEPLOY = "/across-service/deploy-db.yml";
    public static final String ACTIVATION_ACROSS_SERVICE_VPN = "/across-service/across-activation-vpn.sh";
    public static final String ACTIVATION_ACROSS_SERVICE_GSLB = "/across-service/across-activation-gslb.sh";
    public static final String DEACTIVATION_ACROSS_SERVICE_VPN = "/across-service/across-deactivation-vpn.sh";
    public static final String DEACTIVATION_ACROSS_SERVICE_GSLB = "/across-service/across-deactivation-gslb.sh";
    public static final String HEALTH_CHECKING_WEB = "/across-service/db-health-check.yml";
    public static final String HEALTH_CHECKING_DB = "/across-service/web-health-check.yml";
    public static final String INSTALLATION_TELEGRAF = "/monitoring/telegraf-install.yml";
    public static final String ACTIVATION_TELEGRAF_ACROSS_SERVICE_VPN = "/monitoring/across-service/across-activation-vpn.sh";
    public static final String ACTIVATION_TELEGRAF_ACROSS_SERVICE_GSLB = "/monitoring/across-service/across-activation-gslb.sh";
    public static final String DEACTIVATION_TELEGRAF_ACROSS_SERVICE = "/monitoring/across-service/across-deactivation.sh";
    public static final String ACTIVATION_TELEGRAF_SERVICE = "/monitoring/service/activation-telegraf.yml";
    public static final String DEACTIVATION_TELEGRAF_SERVICE = "/monitoring/service/deactivation-telegraf.yml";
    public static final String MIGRATION_DUMP = "/migration/data-dump.yml";
    public static final String MIGRATION_RESTORE = "/migration/migration.yml";


}
