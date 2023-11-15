package com.mcmp.controller.util;

public class PlaybookPath {
    public static final String DEPLOY_APPLICATION = "/root/mcmp/CnC/service/deploy-application.yml";
    public static final String ACTIVATION_SERVICE = "/root/mcmp/CnC/service/activation.yml";
    public static final String DEACTIVATION_SERVICE = "/root/mcmp/CnC/service/deactivation.yml";
    public static final String HEALTH_CHECKING_SERVICE = "/root/mcmp/CnC/service/app-health-checking.yml";
    public static final String WEB_DEPLOY = "/root/mcmp/CnC/across-service/deploy-web.yml";
    public static final String DB_DEPLOY = "/root/mcmp/CnC/across-service/deploy-db.yml";
    public static final String ACTIVATION_ACROSS_SERVICE_VPN = "/root/mcmp/CnC/across-service/across-activation-vpn.sh";
    public static final String ACTIVATION_ACROSS_SERVICE_GSLB = "/root/mcmp/CnC/across-service/across-activation-gslb.sh";
    public static final String DEACTIVATION_ACROSS_SERVICE_VPN = "/root/mcmp/CnC/across-service/across-deactivation-vpn.sh";
    public static final String DEACTIVATION_ACROSS_SERVICE_GSLB = "/root/mcmp/CnC/across-service/across-deactivation-gslb.sh";
    public static final String HEALTH_CHECKING_WEB = "/root/mcmp/CnC/across-service/db-health-check";
    public static final String HEALTH_CHECKING_DB = "/root/mcmp/CnC/across-service/web-health-check";
    public static final String INSTALLATION_TELEGRAF = "/root/mcmp/CnC/monitoring/telegraf-install.yml";
    public static final String ACTIVATION_TELEGRAF_ACROSS_SERVICE_VPN = "/root/mcmp/CnC/monitoring/across-service/across-activation-vpn.sh";
    public static final String ACTIVATION_TELEGRAF_ACROSS_SERVICE_GSLB = "/root/mcmp/CnC/monitoring/across-service/across-activation-gslb.sh";
    public static final String DEACTIVATION_TELEGRAF_ACROSS_SERVICE = "/root/mcmp/CnC/monitoring/across-service/across-deactivation.sh";
    public static final String ACTIVATION_TELEGRAF_SERVICE = "/root/mcmp/CnC/monitoring/service/activation-telegraf.yml";
    public static final String DEACTIVATION_TELEGRAF_SERVICE = "/root/mcmp/CnC/monitoring/service/deactivation-telegraf.yml";
    public static final String MIGRATION_DUMP = "/root/mcmp/CnC/migration/data-dump.yml";
    public static final String MIGRATION_RESTORE = "/root/mcmp/CnC/migration/migration.yml";

}
