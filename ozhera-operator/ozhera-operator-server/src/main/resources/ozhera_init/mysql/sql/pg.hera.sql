

CREATE TABLE hera_trace_etl_config
(
    id                        SERIAL PRIMARY KEY,
    base_info_id              INTEGER DEFAULT NULL,
    exclude_method            VARCHAR(255) DEFAULT NULL,
    exclude_httpserver_method VARCHAR(255) DEFAULT NULL,
    exclude_thread            VARCHAR(255) DEFAULT NULL,
    exclude_sql               VARCHAR(255) DEFAULT NULL,
    exclude_http_url          VARCHAR(255) DEFAULT NULL,
    exclude_ua                VARCHAR(255) DEFAULT NULL,
    http_slow_threshold       INTEGER DEFAULT NULL,
    dubbo_slow_threshold      INTEGER DEFAULT NULL,
    mysql_slow_threshold      INTEGER DEFAULT NULL,
    trace_filter              INTEGER DEFAULT NULL,
    trace_duration_threshold  INTEGER DEFAULT NULL,
    trace_debug_flag          VARCHAR(255) DEFAULT NULL,
    http_status_error         VARCHAR(255) DEFAULT NULL,
    exception_error           VARCHAR(512) DEFAULT NULL,
    grpc_code_error           VARCHAR(255) DEFAULT NULL,
    status                    VARCHAR(2) DEFAULT '1',
    create_time               TIMESTAMP DEFAULT NULL,
    update_time               TIMESTAMP DEFAULT NULL,
    create_user               VARCHAR(32) DEFAULT NULL,
    update_user               VARCHAR(32) DEFAULT NULL
);


-- hera_meta_data

CREATE TABLE hera_meta_data (
  id bigserial PRIMARY KEY,
  type varchar(10) DEFAULT NULL,
  meta_id int DEFAULT NULL,
  meta_name varchar(255) DEFAULT NULL,
  env_id int DEFAULT NULL,
  env_name varchar(255) DEFAULT NULL,
  host varchar(255) DEFAULT NULL,
  port jsonb DEFAULT NULL,
  dubbo_service_meta text DEFAULT NULL,
  create_time timestamptz DEFAULT NULL,
  update_time timestamptz DEFAULT NULL,
  create_by varchar(125) DEFAULT NULL,
  update_by varchar(125) DEFAULT NULL
);

COMMENT ON TABLE hera_meta_data IS 'Data types include APP, MYSQL, REDIS, ES, MQ, etc.';
COMMENT ON COLUMN hera_meta_data.type IS 'Data types include APP, MYSQL, REDIS, ES, MQ, etc.';
COMMENT ON COLUMN hera_meta_data.meta_id IS 'Metadata id, such as appId.';
COMMENT ON COLUMN hera_meta_data.meta_name IS 'The name of the metadata, such as appName.';
COMMENT ON COLUMN hera_meta_data.env_id IS 'Environment ID';
COMMENT ON COLUMN hera_meta_data.env_name IS 'Environment name';
COMMENT ON COLUMN hera_meta_data.host IS 'The instance corresponding to the metadata could be an IP, a domain name, or a host name.';
COMMENT ON COLUMN hera_meta_data.port IS 'Port exposed by metadata';
COMMENT ON COLUMN hera_meta_data.dubbo_service_meta IS 'Dubbo Service information includes ServiceName, Group, and version.';
COMMENT ON COLUMN hera_meta_data.create_time IS 'Creation time';
COMMENT ON COLUMN hera_meta_data.update_time IS 'Update time';
COMMENT ON COLUMN hera_meta_data.create_by IS 'Creator';
COMMENT ON COLUMN hera_meta_data.update_by IS 'Updater';

CREATE INDEX index_meta_id ON hera_meta_data (meta_id);

-- mimonitor

CREATE TABLE alert_group (
  id bigserial PRIMARY KEY,
  name varchar(64) NOT NULL,
  "desc" varchar(256) DEFAULT NULL,
  chat_id varchar(125) DEFAULT NULL,
  creater varchar(64) DEFAULT NULL,
  create_time timestamptz DEFAULT NULL,
  update_time timestamptz DEFAULT NULL,
  rel_id bigint DEFAULT '0',
  type varchar(32) DEFAULT 'alert',
  deleted int DEFAULT '0'
);

COMMENT ON TABLE alert_group IS 'Table for storing alert groups.';
COMMENT ON COLUMN alert_group.name IS 'Name of the alert group.';
COMMENT ON COLUMN alert_group.desc IS 'Description of the alert group.';
COMMENT ON COLUMN alert_group.chat_id IS 'Feishu ID for communication.';
COMMENT ON COLUMN alert_group.creater IS 'Creator of the alert group.';
COMMENT ON COLUMN alert_group.create_time IS 'Creation time of the alert group.';
COMMENT ON COLUMN alert_group.update_time IS 'Update time of the alert group.';
COMMENT ON COLUMN alert_group.rel_id IS 'Relation ID of the alert group.';
COMMENT ON COLUMN alert_group.type IS 'Type of the alert group (e.g., alert).';
COMMENT ON COLUMN alert_group.deleted IS 'Flag indicating whether the alert group is deleted (0 normal, 1 delete).';


-- alert_group_member

CREATE TABLE alert_group_member (
    id bigserial PRIMARY KEY,
    member_id bigint DEFAULT '0',
    alert_group_id bigint DEFAULT '0',
    creater varchar(64) DEFAULT NULL,
    create_time timestamptz DEFAULT NULL,
    update_time timestamptz DEFAULT NULL,
    member varchar(64) DEFAULT '',
    deleted int DEFAULT '0'
);

COMMENT ON TABLE alert_group_member IS 'Table for storing alert group members.';
COMMENT ON COLUMN alert_group_member.member_id IS 'Member ID';
COMMENT ON COLUMN alert_group_member.alert_group_id IS 'Alarm group ID';
COMMENT ON COLUMN alert_group_member.creater IS 'Creator of the alert group member.';
COMMENT ON COLUMN alert_group_member.create_time IS 'Creation time of the alert group member.';
COMMENT ON COLUMN alert_group_member.update_time IS 'Update time of the alert group member.';
COMMENT ON COLUMN alert_group_member.member IS 'User associated with the alert group member.';
COMMENT ON COLUMN alert_group_member.deleted IS 'Flag indicating whether the alert group member is deleted (0 normal, 1 delete).';

CREATE INDEX idx_member_id ON alert_group_member (member_id);
CREATE INDEX idx_alert_group_id ON alert_group_member (alert_group_id);

-- app_alarm_rule

CREATE TABLE app_alarm_rule (
    id bigserial PRIMARY KEY,
    alarm_id int DEFAULT NULL,
    alert varchar(255) NOT NULL,
    cname varchar(255) DEFAULT NULL,
    metric_type int DEFAULT NULL,
    expr text DEFAULT NULL,
    for_time varchar(50) NOT NULL,
    labels text DEFAULT NULL,
    annotations varchar(255) DEFAULT NULL,
    rule_group varchar(50) DEFAULT NULL,
    priority varchar(20) DEFAULT NULL,
    alert_team jsonb DEFAULT NULL,
    env varchar(100) DEFAULT NULL,
    op varchar(5) DEFAULT NULL,
    value numeric(255, 2) DEFAULT NULL,
    data_count int DEFAULT NULL,
    send_interval varchar(20) DEFAULT NULL,
    project_id int DEFAULT NULL,
    strategy_id integer DEFAULT 0,
    iam_id int DEFAULT NULL,
    template_id int DEFAULT NULL,
    rule_type int DEFAULT NULL,
    rule_status int DEFAULT NULL,
    remark varchar(255) DEFAULT NULL,
    creater varchar(64) DEFAULT NULL,
    status int DEFAULT NULL,
    create_time timestamptz DEFAULT NULL,
    update_time timestamptz DEFAULT NULL
);

COMMENT ON TABLE app_alarm_rule IS 'Table for storing application alarm rules.';
COMMENT ON COLUMN app_alarm_rule.alarm_id IS 'Alarm ID, corresponding to the alarm ID of the Prometheus alarm interface.';
COMMENT ON COLUMN app_alarm_rule.alert IS 'Police name.';
COMMENT ON COLUMN app_alarm_rule.cname IS 'Alias for reporting a crime.';
COMMENT ON COLUMN app_alarm_rule.metric_type IS 'Indicator type 0 preset indicator 1 user-defined indicator.';
COMMENT ON COLUMN app_alarm_rule.expr IS 'Expression.';
COMMENT ON COLUMN app_alarm_rule.for_time IS 'Duration.';
COMMENT ON COLUMN app_alarm_rule.labels IS 'Label.';
COMMENT ON COLUMN app_alarm_rule.annotations IS 'Alarm description information.';
COMMENT ON COLUMN app_alarm_rule.rule_group IS 'Rule group.';
COMMENT ON COLUMN app_alarm_rule.priority IS 'Alarm level.';
COMMENT ON COLUMN app_alarm_rule.alert_team IS 'Alarm group JSON.';
COMMENT ON COLUMN app_alarm_rule.env IS 'Environment.';
COMMENT ON COLUMN app_alarm_rule.op IS 'Operator.';
COMMENT ON COLUMN app_alarm_rule.value IS 'Threshold.';
COMMENT ON COLUMN app_alarm_rule.data_count IS 'Number of data points recently.';
COMMENT ON COLUMN app_alarm_rule.send_interval IS 'Alarm sending interval.';
COMMENT ON COLUMN app_alarm_rule.project_id IS 'Project ID.';
COMMENT ON COLUMN app_alarm_rule.strategy_id IS 'Strategy ID.';
COMMENT ON COLUMN app_alarm_rule.iam_id IS 'iamId.';
COMMENT ON COLUMN app_alarm_rule.template_id IS 'Template ID.';
COMMENT ON COLUMN app_alarm_rule.rule_type IS 'Rule type: 0 template rule, 1 application configuration rule.';
COMMENT ON COLUMN app_alarm_rule.rule_status IS '0 Active 1 Pause.';
COMMENT ON COLUMN app_alarm_rule.remark IS 'Note.';
COMMENT ON COLUMN app_alarm_rule.creater IS 'Creator.';
COMMENT ON COLUMN app_alarm_rule.status IS 'Status 0 valid 1 deleted.';
COMMENT ON COLUMN app_alarm_rule.create_time IS 'Creation time.';
COMMENT ON COLUMN app_alarm_rule.update_time IS 'Update time.';



-- 创建表 app_alarm_rule_template
CREATE TABLE app_alarm_rule_template
(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    type int NOT NULL,
    remark varchar(255),
    creater varchar(64),
    status int,
    create_time timestamp,
    update_time timestamp,
    strategy_type int DEFAULT '0'
);

COMMENT ON TABLE app_alarm_rule_template IS 'name';

COMMENT ON COLUMN app_alarm_rule_template.name IS 'name';
COMMENT ON COLUMN app_alarm_rule_template.type IS 'type 0 system 1 user';
COMMENT ON COLUMN app_alarm_rule_template.remark IS 'remark';
COMMENT ON COLUMN app_alarm_rule_template.creater IS 'creator';
COMMENT ON COLUMN app_alarm_rule_template.status IS 'status：0 Effective 1 Deleted';
COMMENT ON COLUMN app_alarm_rule_template.create_time IS 'create_time';
COMMENT ON COLUMN app_alarm_rule_template.update_time IS 'update_time';
COMMENT ON COLUMN app_alarm_rule_template.strategy_type IS 'strategy_type';

-- 创建表 app_alarm_strategy
CREATE TABLE app_alarm_strategy
(
    id serial PRIMARY KEY,
    iamId int DEFAULT '0',
    appId int NOT NULL,
    appName varchar(100),
    strategy_type int,
    strategy_name varchar(100),
    "desc" varchar(255),
    creater varchar(64),
    create_time timestamp,
    update_time timestamp,
    status smallint NOT NULL DEFAULT '0',
    alert_team text,
    group3 varchar(32) DEFAULT '',
    group4 varchar(32) DEFAULT '',
    group5 varchar(32) DEFAULT '',
    envs text,
    alert_members text,
    at_members text,
    services text
);

COMMENT ON TABLE app_alarm_strategy IS 'app_alarm_strategy';

COMMENT ON COLUMN app_alarm_strategy.iamId IS 'iamId';
COMMENT ON COLUMN app_alarm_strategy.appId IS 'appId';
COMMENT ON COLUMN app_alarm_strategy.appName IS 'appName';
COMMENT ON COLUMN app_alarm_strategy.strategy_type IS 'strategy_type';
COMMENT ON COLUMN app_alarm_strategy.strategy_name IS 'strategy_name';
COMMENT ON COLUMN app_alarm_strategy."desc" IS 'desc';
COMMENT ON COLUMN app_alarm_strategy.creater IS 'creator';
COMMENT ON COLUMN app_alarm_strategy.create_time IS 'create_time';
COMMENT ON COLUMN app_alarm_strategy.update_time IS 'update_time';
COMMENT ON COLUMN app_alarm_strategy.status IS 'status';
COMMENT ON COLUMN app_alarm_strategy.alert_team IS 'alert_team';
COMMENT ON COLUMN app_alarm_strategy.group3 IS 'group3';
COMMENT ON COLUMN app_alarm_strategy.group4 IS 'group4';
COMMENT ON COLUMN app_alarm_strategy.group5 IS 'group5';
COMMENT ON COLUMN app_alarm_strategy.envs IS 'envs';
COMMENT ON COLUMN app_alarm_strategy.alert_members IS 'alert_members';
COMMENT ON COLUMN app_alarm_strategy.at_members IS 'at_members';
COMMENT ON COLUMN app_alarm_strategy.services IS 'services';

-- 创建表 app_capacity_auto_adjust
CREATE TABLE app_capacity_auto_adjust
(
    id serial PRIMARY KEY,
    app_id int NOT NULL,
    pipeline_id int NOT NULL,
    container varchar(255),
    status int,
    min_instance int,
    max_instance int,
    auto_capacity int,
    depend_on int,
    create_time timestamp,
    update_time timestamp,
    UNIQUE (app_id, pipeline_id)
);

COMMENT ON TABLE app_capacity_auto_adjust IS 'app_capacity_auto_adjust';
COMMENT ON COLUMN app_capacity_auto_adjust.app_id IS 'app_id';
COMMENT ON COLUMN app_capacity_auto_adjust.pipeline_id IS 'pipeline_id';
COMMENT ON COLUMN app_capacity_auto_adjust.container IS 'container name';
COMMENT ON COLUMN app_capacity_auto_adjust.status IS '0 Available，1 Not available.';
COMMENT ON COLUMN app_capacity_auto_adjust.min_instance IS 'min instance';
COMMENT ON COLUMN app_capacity_auto_adjust.max_instance IS 'max instance';
COMMENT ON COLUMN app_capacity_auto_adjust.auto_capacity IS 'auto capacity 1 yes 0 no';
COMMENT ON COLUMN app_capacity_auto_adjust.depend_on IS 'depend_on 0 cpu 1 memory 2 both depend_on';
COMMENT ON COLUMN app_capacity_auto_adjust.create_time IS 'create time';
COMMENT ON COLUMN app_capacity_auto_adjust.update_time IS 'update time';


-- 创建表 app_capacity_auto_adjust_record
CREATE TABLE app_capacity_auto_adjust_record
(
    id serial PRIMARY KEY,
    container varchar(255),
    name_space varchar(255),
    replicas int,
    set_replicas int,
    env_id int,
    status int,
    time bigint,
    create_time timestamp,
    update_time timestamp
);

-- 创建表 app_grafana_mapping
CREATE TABLE app_grafana_mapping
(
    id serial PRIMARY KEY,
    app_name varchar(100) NOT NULL,
    mione_env varchar(20),
    grafana_url varchar(200) NOT NULL,
    create_time timestamp,
    update_time timestamp
);

-- 创建表 app_monitor
CREATE TABLE app_monitor
(
    id serial PRIMARY KEY,
    project_id int,
    iam_tree_id int,
    iam_tree_type int,
    project_name varchar(255),
    app_source int DEFAULT '0',
    owner varchar(128),
    care_user varchar(30),
    alarm_level int,
    total_alarm int,
    exception_num int,
    slow_query_num int,
    status int,
    base_info_id int,
    create_time timestamp,
    update_time timestamp
);


-- 创建表 app_quality_market
CREATE TABLE app_quality_market
(
    id serial PRIMARY KEY,
    market_name varchar(255) NOT NULL DEFAULT '',
    creator varchar(100) DEFAULT '',
    service_list TEXT,
    last_updater varchar(100) DEFAULT '',
    remark varchar(255) DEFAULT '',
    create_time timestamp,
    update_time timestamp
);

-- 创建表 app_scrape_job
CREATE TABLE app_scrape_job
(
    id serial PRIMARY KEY,
    iam_id int NOT NULL,
    "user" varchar(64) NOT NULL DEFAULT '',
    job_json text,
    message varchar(255) NOT NULL DEFAULT '',
    data varchar(255) DEFAULT '',
    job_name varchar(64),
    status smallint NOT NULL DEFAULT '0',
    job_desc varchar(255) DEFAULT '',
    create_time timestamp NOT NULL,
    update_time timestamp
);

-- 创建表 app_service_market
CREATE TABLE app_service_market
(
    id serial PRIMARY KEY,
    market_name varchar(150) NOT NULL DEFAULT '',
    belong_team varchar(150) NOT NULL DEFAULT '',
    creator varchar(50) DEFAULT '',
    service_list TEXT,
    last_updater varchar(50) DEFAULT '',
    remark varchar(255) DEFAULT '',
    service_type int NOT NULL DEFAULT '0',
    create_time timestamp,
    update_time timestamp
);

-- 创建表 app_tesla_alarm_rule
CREATE TABLE app_tesla_alarm_rule
(
    id serial PRIMARY KEY,
    name varchar(100) DEFAULT NULL,
    tesla_group varchar(100) NOT NULL,
    alert_type varchar(50) DEFAULT NULL,
    exper text DEFAULT NULL,
    op varchar(2) DEFAULT NULL,
    value numeric(11, 2),
    duration varchar(20) DEFAULT NULL,
    remark varchar(255) DEFAULT NULL,
    type int DEFAULT NULL,
    status int DEFAULT NULL,
    creater varchar(64) DEFAULT NULL,
    create_time timestamp,
    update_time timestamp
);


-- 创建表 app_tesla_feishu_mapping
CREATE TABLE app_tesla_feishu_mapping
(
    id serial PRIMARY KEY,
    tesla_group varchar(50) NOT NULL,
    feishu_group_id varchar(50) NOT NULL,
    remark varchar(255) DEFAULT NULL,
    creater varchar(64),
    status int,
    create_time timestamp,
    update_time timestamp
);

-- 创建表 hera_app_base_info
CREATE TABLE hera_app_base_info
(
    id serial PRIMARY KEY,
    bind_id varchar(50) NOT NULL,
    bind_type int NOT NULL,
    app_name varchar(255) NOT NULL,
    app_cname varchar(255),
    app_type int NOT NULL,
    app_language varchar(30),
    platform_type int NOT NULL,
    app_sign_id varchar(60),
    iam_tree_id int,
    iam_tree_type int NOT NULL,
    envs_map json,
    auto_capacity int,
    status int,
    create_time timestamp,
    update_time timestamp
);

-- 创建表 hera_app_excess_info
CREATE TABLE hera_app_excess_info
(
    id serial PRIMARY KEY,
    app_base_id bigint,
    tree_ids json,
    node_ips json,
    managers json,
    create_time timestamp,
    update_time timestamp,
    UNIQUE (app_base_id)
);

<!--hera_app_env-->
CREATE TABLE hera_app_env (
    id bigserial PRIMARY KEY,
    hera_app_id bigint NOT NULL,
    app_id bigint NOT NULL,
    app_name varchar(100) NULL DEFAULT NULL,
    env_id bigint NULL DEFAULT NULL,
    env_name varchar(100) NULL DEFAULT NULL,
    ip_list json NULL,
    ctime bigint NOT NULL,
    creator varchar(50) NULL DEFAULT NULL,
    utime bigint NULL DEFAULT NULL,
    updater varchar(50) NULL DEFAULT NULL
);

COMMENT ON TABLE hera_app_env IS 'Your table comment here';
COMMENT ON COLUMN hera_app_env.hera_app_id IS 'hera_app_base_info table id';
COMMENT ON COLUMN hera_app_env.app_id IS 'app_id';
COMMENT ON COLUMN hera_app_env.app_name IS 'app_name';
COMMENT ON COLUMN hera_app_env.env_id IS 'env_id(Comes from synchronous information)';
COMMENT ON COLUMN hera_app_env.env_name IS 'env_name';
COMMENT ON COLUMN hera_app_env.ip_list IS 'ip_list (The information stored here is all final.)';
COMMENT ON COLUMN hera_app_env.ctime IS 'create time (Millisecond timestamp)';
COMMENT ON COLUMN hera_app_env.creator IS 'creator';
COMMENT ON COLUMN hera_app_env.utime IS 'update time (Millisecond timestamp)';
COMMENT ON COLUMN hera_app_env.updater IS 'updater';



<!--hera_app_role-->
CREATE TABLE hera_app_role (
    id serial PRIMARY KEY,
    app_id varchar(50) NOT NULL,
    app_platform int NOT NULL,
    "user" varchar(80) NOT NULL,
    role int NOT NULL,
    status int NOT NULL,
    create_time timestamp NULL DEFAULT NULL,
    update_time timestamp NULL DEFAULT NULL
);

-- 在数据库级别设置 COLLATE
ALTER DATABASE hera SET default_collation = 'utf8mb4_bin';

-- 创建索引
CREATE INDEX idx_app_role ON hera_app_role (app_id, app_platform);
CREATE INDEX idx_app_role_user ON hera_app_role ("user");


<!--hera_oper_log-->
CREATE TABLE hera_oper_log (
    id bigserial PRIMARY KEY,
    oper_name varchar(64) NOT NULL,
    log_type int DEFAULT '0',
    before_parent_id bigint DEFAULT '0',
    module_name varchar(64) DEFAULT '',
    interface_name varchar(64) DEFAULT '',
    interface_url varchar(128) DEFAULT '',
    action varchar(32) DEFAULT '',
    before_data text DEFAULT NULL,
    after_data text DEFAULT NULL,
    create_time timestamp NULL DEFAULT NULL,
    update_time timestamp NULL DEFAULT NULL,
    data_type int DEFAULT '0',
    after_parent_id bigint DEFAULT '0',
    result_desc varchar(128) DEFAULT ''
);

CREATE INDEX idx_before_parent_id ON hera_oper_log (before_parent_id);
CREATE INDEX idx_oper_name ON hera_oper_log (oper_name);
CREATE INDEX idx_after_parent_id ON hera_oper_log (after_parent_id);


<!--rules-->
CREATE TABLE rules (
    rule_id serial PRIMARY KEY,
    rule_name varchar(255) DEFAULT '',
    rule_fn varchar(255) DEFAULT '',
    rule_interval int DEFAULT NULL,
    rule_alert varchar(255) DEFAULT '',
    rule_expr text DEFAULT NULL,
    rule_for varchar(255) DEFAULT '',
    rule_labels varchar(255) DEFAULT '',
    rule_annotations text DEFAULT NULL,
    principal varchar(255) DEFAULT NULL,
    create_time date DEFAULT NULL,
    update_time date DEFAULT NULL
);

-- 为列添加注释
COMMENT ON COLUMN rules.rule_id IS 'rule_id';
COMMENT ON COLUMN rules.rule_name IS 'rule_name';
COMMENT ON COLUMN rules.rule_fn IS 'type';
COMMENT ON COLUMN rules.rule_interval IS 'rule_interval';
COMMENT ON COLUMN rules.rule_alert IS 'rule_alert name';
COMMENT ON COLUMN rules.rule_expr IS 'rule_expr';
COMMENT ON COLUMN rules.rule_for IS 'duration';
COMMENT ON COLUMN rules.rule_labels IS 'Rule dimension information';
COMMENT ON COLUMN rules.rule_annotations IS 'Description of rules';
COMMENT ON COLUMN rules.principal IS 'Comma separated prefixes of the person in charge email.';
COMMENT ON COLUMN rules.create_time IS 'create_time';
COMMENT ON COLUMN rules.update_time IS 'update_time';

-- 添加唯一约束
ALTER TABLE rules ADD CONSTRAINT unique_key UNIQUE (rule_alert);


<!--rule_promql_template-->
CREATE TABLE rule_promql_template (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    promql varchar(512) DEFAULT NULL,
    type int NOT NULL,
    remark varchar(255) DEFAULT NULL,
    creater varchar(64) DEFAULT '',
    status int DEFAULT NULL,
    create_time timestamp NULL DEFAULT NULL,
    update_time timestamp NULL DEFAULT NULL
);

COMMENT ON TABLE rule_promql_template IS 'Table to store PromQL templates';
COMMENT ON COLUMN rule_promql_template.name IS 'Template Name';
COMMENT ON COLUMN rule_promql_template.type IS 'type 0 system 1 user';
COMMENT ON COLUMN rule_promql_template.promql IS 'promql';
COMMENT ON COLUMN rule_promql_template.remark IS 'remark';
COMMENT ON COLUMN rule_promql_template.creater IS 'creator';
COMMENT ON COLUMN rule_promql_template.status IS 'status: 0 Effective';
COMMENT ON COLUMN rule_promql_template.create_time IS 'create_time';
COMMENT ON COLUMN rule_promql_template.update_time IS 'update_time';

CREATE INDEX idx_creater ON rule_promql_template (creater);


<!--app_monitor_config-->
CREATE TABLE app_monitor_config (
    id serial PRIMARY KEY,
    project_id int NOT NULL,
    config_type int NOT NULL,
    config_name varchar(50) NOT NULL,
    value varchar(255) NOT NULL,
    status int NOT NULL,
    create_time timestamp NULL DEFAULT NULL,
    update_time timestamp NULL DEFAULT NULL
);

COMMENT ON TABLE app_monitor_config IS 'Table for application monitoring configuration';
COMMENT ON COLUMN app_monitor_config.project_id IS 'id';
COMMENT ON COLUMN app_monitor_config.config_type IS 'config_type 0 Slow query time';
COMMENT ON COLUMN app_monitor_config.config_name IS 'config_name';
COMMENT ON COLUMN app_monitor_config.value IS 'value';
COMMENT ON COLUMN app_monitor_config.status IS 'status';
COMMENT ON COLUMN app_monitor_config.create_time IS 'create_time';
COMMENT ON COLUMN app_monitor_config.update_time IS 'update_time';


<!--mione_grafana_template-->
CREATE TABLE mione_grafana_template (
    id bigserial PRIMARY KEY,
    name varchar(64) NOT NULL UNIQUE,
    template text DEFAULT NULL,
    platform int DEFAULT NULL,
    language int DEFAULT NULL,
    app_type int DEFAULT NULL,
    panel_id_list text DEFAULT NULL,
    url_param text DEFAULT NULL,
    create_time timestamp NULL DEFAULT NULL,
    update_time timestamp NULL DEFAULT NULL,
    deleted boolean DEFAULT 'false'
);

COMMENT ON TABLE mione_grafana_template IS 'Table for Grafana templates';
COMMENT ON COLUMN mione_grafana_template.id IS 'id';
COMMENT ON COLUMN mione_grafana_template.name IS 'Template name';
COMMENT ON COLUMN mione_grafana_template.template IS 'template json';
COMMENT ON COLUMN mione_grafana_template.platform IS 'platform';
COMMENT ON COLUMN mione_grafana_template.language IS 'language';
COMMENT ON COLUMN mione_grafana_template.app_type IS 'app_type';
COMMENT ON COLUMN mione_grafana_template.panel_id_list IS 'panel_id_list';
COMMENT ON COLUMN mione_grafana_template.url_param IS 'url_param';
COMMENT ON COLUMN mione_grafana_template.create_time IS 'create_time';
COMMENT ON COLUMN mione_grafana_template.update_time IS 'update_time';
COMMENT ON COLUMN mione_grafana_template.deleted IS '0 Not deleted 1 deleted';

-- 添加更新时间戳的触发器
CREATE OR REPLACE FUNCTION set_update_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_update_time_trigger
BEFORE UPDATE ON mione_grafana_template
FOR EACH ROW EXECUTE FUNCTION set_update_time();


<!--milog_analyse_dashboard-->
CREATE TABLE milog_analyse_dashboard (
    id bigserial PRIMARY KEY,
    name varchar(255) NULL,
    store_id bigint NULL,
    space_id bigint NULL,
    creator varchar(255) NULL,
    updater varchar(255) NULL,
    create_time bigint NULL,
    update_time bigint NULL
);

-- 按照需要添加索引
-- CREATE INDEX idx_store_id ON milog_analyse_dashboard (store_id);
-- CREATE INDEX idx_space_id ON milog_analyse_dashboard (space_id);


<!--milog_analyse_dashboard_graph_ref-->
CREATE TABLE milog_analyse_dashboard_graph_ref (
    id bigserial PRIMARY KEY,
    dashboard_id bigint NULL,
    graph_id bigint NULL,
    point json NULL,
    private_name varchar(255) NULL
)


<!--milog_analyse_graph-->
CREATE TABLE milog_analyse_graph (
    id bigserial PRIMARY KEY,
    name varchar(255) NULL,
    field_name varchar(255) NULL,
    space_id bigint NULL,
    store_id bigint NULL,
    graph_type int NULL,
    graph_param varchar(1000) NULL,
    updater varchar(255) NULL,
    creator varchar(255) NULL,
    create_time bigint NULL,
    update_time bigint NULL
);


<!--milog_analyse_graph_type-->
CREATE TABLE milog_analyse_graph_type (
    id bigserial PRIMARY KEY,
    name varchar(255) NULL,
    type int NULL,
    calculate varchar(255) NULL,
    classify varchar(50) NULL
);


<!--milog_app_middleware_rel-->
CREATE TABLE milog_app_middleware_rel (
    id bigserial PRIMARY KEY,
    milog_app_id bigint NOT NULL,
    middleware_id bigint NOT NULL,
    tail_id bigint NOT NULL,
    config json NULL,
    ctime bigint NOT NULL,
    utime bigint NOT NULL,
    creator varchar(50) NOT NULL,
    updater varchar(50) NOT NULL
);


<!--milog_es_cluster-->
CREATE TABLE milog_es_cluster (
    id bigserial PRIMARY KEY,
    log_storage_type1 varchar(50) DEFAULT 'elasticsearch',
    tag varchar(255) NULL,
    name varchar(255) NULL,
    region varchar(255) NULL,
    cluster_name varchar(255) NULL,
    addr varchar(255) NULL,
    "user" varchar(255) NULL,
    pwd varchar(255) NULL,
    token varchar(255) NULL,
    dt_catalog varchar(255) NULL,
    dt_database varchar(255) NULL,
    area varchar(255) NULL,
    ctime bigint NULL,
    utime bigint NULL,
    creator varchar(50) NULL,
    updater varchar(50) NULL,
    labels json NULL,
    con_way varchar(50) NULL,
    is_default smallint DEFAULT '0'
);

COMMENT ON COLUMN milog_es_cluster.log_storage_type1 IS 'Log storage type';
COMMENT ON COLUMN milog_es_cluster.area IS 'area';
COMMENT ON COLUMN milog_es_cluster.labels IS 'labels';
COMMENT ON COLUMN milog_es_cluster.con_way IS 'connect way: pwd,token';
COMMENT ON COLUMN milog_es_cluster.is_default IS 'is_default';


<!--milog_es_index-->
CREATE TABLE milog_es_index (
    id bigserial PRIMARY KEY,
    cluster_id bigint NULL,
    log_type int NULL,
    index_name varchar(255) NULL
);


<!--milog_log_count-->
CREATE TABLE milog_log_count (
    id bigserial PRIMARY KEY,
    tail_id bigint NULL,
    es_index varchar(255) NULL,
    day date NULL,
    number bigint NULL
);


<!--milog_log_num_alert-->
CREATE TABLE milog_log_num_alert (
    id bigserial PRIMARY KEY,
    day date NULL,
    number bigint NULL,
    app_id bigint NULL,
    app_name varchar(255) NULL,
    alert_user varchar(5000) NULL,
    ctime bigint NULL
);


<!--milog_log_search_save-->
CREATE TABLE milog_log_search_save (
    id bigserial PRIMARY KEY,
    name varchar(255) NULL,
    space_id int NULL,
    store_id bigint NULL,
    tail_id varchar(250) NULL,
    query_text varchar(2000) NULL,
    is_fix_time int NULL,
    start_time bigint NULL,
    end_time bigint NULL,
    common varchar(255) NULL,
    sort bigint NULL,
    order_num bigint NULL,
    creator varchar(255) NULL,
    updater varchar(255) NULL,
    create_time bigint NULL,
    update_time bigint NULL
);

COMMENT ON COLUMN milog_log_search_save.is_fix_time IS '1-Saved the time parameter 0-Not saved';
COMMENT ON COLUMN milog_log_search_save.start_time IS 'search start time';
COMMENT ON COLUMN milog_log_search_save.end_time IS 'search end time';
COMMENT ON COLUMN milog_log_search_save.sort IS 'type 1-search,2-tail,3-store';
COMMENT ON COLUMN milog_log_search_save.order_num IS 'sort';


<!--milog_log_template-->
CREATE TABLE milog_log_template (
    id bigserial NOT NULL PRIMARY KEY,
    ctime bigint DEFAULT NULL,
    utime bigint DEFAULT NULL,
    template_name varchar(255) NOT NULL,
    type int DEFAULT NULL,
    support_area varchar(255) DEFAULT NULL,
    order_col int DEFAULT NULL,
    supported_consume smallint NOT NULL DEFAULT '1'
);

COMMENT ON COLUMN milog_log_template.id IS 'id';
COMMENT ON COLUMN milog_log_template.ctime IS 'ctime';
COMMENT ON COLUMN milog_log_template.utime IS 'utime';
COMMENT ON COLUMN milog_log_template.template_name IS 'template_name';
COMMENT ON COLUMN milog_log_template.type IS 'template type 0-Custom log,1-app,2-nginx';
COMMENT ON COLUMN milog_log_template.support_area IS 'support_area';
COMMENT ON COLUMN milog_log_template.order_col IS 'sort';
COMMENT ON COLUMN milog_log_template.supported_consume IS 'Whether to support consumption, default support is 1.';



<!--milog_log_template_detail-->
CREATE TABLE milog_log_template_detail (
    id bigserial NOT NULL PRIMARY KEY,
    ctime bigint NULL DEFAULT NULL,
    utime bigint NULL DEFAULT NULL,
    template_id varchar(20) NULL,
    properties_key varchar(255) NULL,
    properties_type varchar(255) NULL
);

COMMENT ON COLUMN milog_log_template_detail.id IS 'id';
COMMENT ON COLUMN milog_log_template_detail.ctime IS 'ctime';
COMMENT ON COLUMN milog_log_template_detail.utime IS 'utime';
COMMENT ON COLUMN milog_log_template_detail.template_id IS 'template_id';
COMMENT ON COLUMN milog_log_template_detail.properties_key IS 'properties_key；1-Required,2-Suggestion,3-Hidden';
COMMENT ON COLUMN milog_log_template_detail.properties_type IS 'properties_type';



<!--milog_logstail-->
CREATE TABLE milog_logstail (
    id bigserial NOT NULL PRIMARY KEY,
    ctime bigint NULL DEFAULT NULL,
    utime bigint NULL DEFAULT NULL,
    creator varchar(80) NULL DEFAULT NULL,
    updater varchar(80) NULL DEFAULT NULL,
    space_id bigint NULL DEFAULT NULL,
    store_id bigint NULL DEFAULT NULL,
    tail varchar(255) NULL DEFAULT NULL,
    milog_app_id bigint NULL DEFAULT NULL,
    app_id bigint NULL DEFAULT NULL,
    app_name varchar(128) NULL DEFAULT NULL,
    app_type smallint NULL DEFAULT NULL,
    machine_type smallint NULL DEFAULT NULL,
    env_id int NULL DEFAULT NULL,
    env_name varchar(128) NULL DEFAULT NULL,
    parse_type int NULL DEFAULT NULL,
    parse_script text NULL,
    log_path varchar(1024) NULL DEFAULT NULL,
    log_split_express varchar(255) NULL DEFAULT NULL,
    value_list varchar(1024) NULL DEFAULT NULL,
    ips json NULL,
    motor_rooms json NULL,
    filter json NULL,
    en_es_index json NULL,
    deploy_way int NULL DEFAULT NULL,
    deploy_space varchar(255) NULL DEFAULT NULL,
    first_line_reg varchar(255) NULL DEFAULT NULL
);

COMMENT ON COLUMN milog_logstail.id IS 'id';
COMMENT ON COLUMN milog_logstail.ctime IS 'ctime';
COMMENT ON COLUMN milog_logstail.utime IS 'utime';
COMMENT ON COLUMN milog_logstail.creator IS 'creator';
COMMENT ON COLUMN milog_logstail.updater IS 'updater';
COMMENT ON COLUMN milog_logstail.space_id IS 'spaceId';
COMMENT ON COLUMN milog_logstail.store_id IS 'storeId';
COMMENT ON COLUMN milog_logstail.tail IS 'app alias name';
COMMENT ON COLUMN milog_logstail.milog_app_id IS 'milog table id';
COMMENT ON COLUMN milog_logstail.app_id IS 'app id';
COMMENT ON COLUMN milog_logstail.app_name IS 'app_name';
COMMENT ON COLUMN milog_logstail.app_type IS '0.mione 1.mis';
COMMENT ON COLUMN milog_logstail.machine_type IS 'mis app machine type 0.container 1.physical machine';
COMMENT ON COLUMN milog_logstail.env_id IS 'env_id';
COMMENT ON COLUMN milog_logstail.env_name IS 'env_name';
COMMENT ON COLUMN milog_logstail.parse_type IS 'parse_type: 1:Service application log, 2.Separator, 3: One line, 4: Multiple lines, 5: customize';
COMMENT ON COLUMN milog_logstail.parse_script IS 'For delimiters, this field specifies the delimiter, for custom, this field specifies the log reading script.';
COMMENT ON COLUMN milog_logstail.log_path IS 'Comma-separated, multiple log file paths, e.g. /home/work/log/xxx/server.log';
COMMENT ON COLUMN milog_logstail.log_split_express IS 'Log split expression';
COMMENT ON COLUMN milog_logstail.value_list IS 'Value list, separated by commas.';
COMMENT ON COLUMN milog_logstail.ips IS 'ip list';
COMMENT ON COLUMN milog_logstail.motor_rooms IS 'mis Application server room information';
COMMENT ON COLUMN milog_logstail.filter IS 'filter config';
COMMENT ON COLUMN milog_logstail.en_es_index IS 'mis Application index configuration';
COMMENT ON COLUMN milog_logstail.deploy_way IS 'deploy way: 1-mione, 2-miline, 3-k8s';
COMMENT ON COLUMN milog_logstail.deploy_space IS 'matrix service deployment space';
COMMENT ON COLUMN milog_logstail.first_line_reg IS 'Regular expression at the beginning of a line';



<!--milog_logstore-->
CREATE TABLE milog_logstore (
    id bigserial NOT NULL PRIMARY KEY,
    ctime bigint NULL DEFAULT NULL,
    utime bigint NULL DEFAULT NULL,
    space_id bigint NOT NULL,
    logstoreName varchar(255) NOT NULL,
    store_period int NULL DEFAULT NULL,
    shard_cnt int NULL DEFAULT NULL,
    key_list varchar(1024) NULL DEFAULT NULL,
    column_type_list varchar(1024) NULL DEFAULT NULL,
    log_type varchar(11) NULL DEFAULT NULL,
    es_index varchar(255) NULL DEFAULT NULL,
    es_cluster_id bigint NULL DEFAULT NULL,
    machine_room varchar(50) NULL DEFAULT NULL,
    creator varchar(50) NULL DEFAULT NULL,
    updater varchar(50) NULL DEFAULT NULL,
    mq_resource_id bigint NULL DEFAULT NULL,
    is_matrix_app int NULL DEFAULT 0
);

COMMENT ON COLUMN milog_logstore.id IS 'id';
COMMENT ON COLUMN milog_logstore.ctime IS 'ctime';
COMMENT ON COLUMN milog_logstore.utime IS 'utime';
COMMENT ON COLUMN milog_logstore.space_id IS 'spaceId';
COMMENT ON COLUMN milog_logstore.logstoreName IS 'log store Name';
COMMENT ON COLUMN milog_logstore.store_period IS 'store_period:1-3-5-7';
COMMENT ON COLUMN milog_logstore.shard_cnt IS 'Number of storage shards';
COMMENT ON COLUMN milog_logstore.key_list IS 'key list, Multiple separated by commas';
COMMENT ON COLUMN milog_logstore.column_type_list IS 'column type, Multiple separated by commas';
COMMENT ON COLUMN milog_logstore.log_type IS '1:app,2:ngx..';
COMMENT ON COLUMN milog_logstore.es_index IS 'es index:milog_logstoreName';
COMMENT ON COLUMN milog_logstore.es_cluster_id IS 'es_cluster_id';
COMMENT ON COLUMN milog_logstore.machine_room IS 'machine info';
COMMENT ON COLUMN milog_logstore.creator IS 'creator';
COMMENT ON COLUMN milog_logstore.updater IS 'updater';
COMMENT ON COLUMN milog_logstore.mq_resource_id IS 'resource mq Id';
COMMENT ON COLUMN milog_logstore.is_matrix_app IS 'is matrix app: 0=false，1=true';



<!--milog_middleware_config-->
CREATE TABLE milog_middleware_config (
    id bigserial NOT NULL PRIMARY KEY,
    type smallint NOT NULL,
    region_en varchar(20) DEFAULT NULL,
    alias varchar(255) DEFAULT NULL,
    name_server varchar(255) DEFAULT NULL,
    service_url varchar(255) DEFAULT NULL,
    ak varchar(255) DEFAULT NULL,
    sk varchar(255) DEFAULT NULL,
    broker_name varchar(255) DEFAULT NULL,
    token varchar(255) DEFAULT NULL,
    dt_catalog varchar(255) DEFAULT NULL,
    dt_database varchar(255) DEFAULT NULL,
    "authorization" text DEFAULT NULL,
    org_id varchar(50) DEFAULT NULL,
    team_id varchar(50) DEFAULT NULL,
    is_default smallint DEFAULT '0',
    ctime bigint NOT NULL,
    utime bigint NOT NULL,
    creator varchar(50) NOT NULL,
    updater varchar(50) NOT NULL,
    labels json DEFAULT NULL
);

COMMENT ON COLUMN milog_middleware_config.id IS 'id';
COMMENT ON COLUMN milog_middleware_config.type IS 'config type 1. rocketmq 2.talos';
COMMENT ON COLUMN milog_middleware_config.region_en IS 'region';
COMMENT ON COLUMN milog_middleware_config.alias IS 'alias';
COMMENT ON COLUMN milog_middleware_config.name_server IS 'nameServer addr';
COMMENT ON COLUMN milog_middleware_config.service_url IS 'domain';
COMMENT ON COLUMN milog_middleware_config.ak IS 'ak';
COMMENT ON COLUMN milog_middleware_config.sk IS 'sk';
COMMENT ON COLUMN milog_middleware_config.broker_name IS 'rocketmq addr';
COMMENT ON COLUMN milog_middleware_config.token IS 'token';
COMMENT ON COLUMN milog_middleware_config.dt_catalog IS 'dt_catalog';
COMMENT ON COLUMN milog_middleware_config.dt_database IS 'dt_database';
COMMENT ON COLUMN milog_middleware_config."authorization" IS 'Authorization information (required for HTTP interface request headers)';
COMMENT ON COLUMN milog_middleware_config.org_id IS 'Organization Id';
COMMENT ON COLUMN milog_middleware_config.team_id IS 'team Id';
COMMENT ON COLUMN milog_middleware_config.is_default IS 'Does this configuration apply by default when mq is not selected?(1.yes 0.no)';
COMMENT ON COLUMN milog_middleware_config.ctime IS 'ctime';
COMMENT ON COLUMN milog_middleware_config.utime IS 'utime';
COMMENT ON COLUMN milog_middleware_config.creator IS 'creator';
COMMENT ON COLUMN milog_middleware_config.updater IS 'updater';
COMMENT ON COLUMN milog_middleware_config.labels IS 'labels';


<!--milog_region_zone-->
CREATE TABLE milog_region_zone (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    region_name_en VARCHAR(255),
    region_name_cn VARCHAR(255),
    zone_name_en VARCHAR(50),
    zone_name_cn VARCHAR(255),
    ctime BIGINT,
    utime BIGINT,
    creator VARCHAR(50),
    updater VARCHAR(50)
);


<!--milog_space-->
CREATE TABLE milog_space (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    ctime BIGINT,
    utime BIGINT,
    tenant_id INT,
    space_name VARCHAR(255),
    source VARCHAR(20),
    creator VARCHAR(255),
    dept_id VARCHAR(255),
    updater VARCHAR(50),
    description VARCHAR(255),
    create_dept_id VARCHAR(50),
    perm_dept_id VARCHAR(2000)
);


<!--milog_store_space_auth-->
CREATE TABLE milog_store_space_auth (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    store_id BIGINT NOT NULL,
    space_id BIGINT NOT NULL,
    ctime BIGINT NOT NULL,
    utime BIGINT,
    creator VARCHAR(100),
    updater VARCHAR(100)
);


<!--prometheus_alert-->
CREATE TABLE prometheus_alert (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cname VARCHAR(255) NOT NULL,
    expr VARCHAR(4096) NOT NULL,
    labels VARCHAR(4096) NOT NULL,
    alert_for VARCHAR(20) NOT NULL,
    env VARCHAR(100) DEFAULT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    priority SMALLINT NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_by VARCHAR(255) NOT NULL,
    deleted_time TIMESTAMPTZ DEFAULT NULL,
    prom_cluster VARCHAR(100) DEFAULT 'public',
    status VARCHAR(32) NOT NULL DEFAULT 'pending',
    instances VARCHAR(255) DEFAULT '',
    thresholds_op VARCHAR(8) DEFAULT NULL,
    thresholds TEXT,
    type INT DEFAULT NULL,
    alert_member VARCHAR(1024) NOT NULL DEFAULT '',
    alert_at_people VARCHAR(1024) NOT NULL,
    annotations VARCHAR(4096) NOT NULL DEFAULT '',
    alert_group VARCHAR(255) DEFAULT ''
);

COMMENT ON COLUMN prometheus_alert.id IS 'Alert ID';
COMMENT ON COLUMN prometheus_alert.name IS 'Alert name';
COMMENT ON COLUMN prometheus_alert.cname IS 'Alert cname';
COMMENT ON COLUMN prometheus_alert.expr IS 'expr';
COMMENT ON COLUMN prometheus_alert.labels IS 'labels';
COMMENT ON COLUMN prometheus_alert.alert_for IS 'for';
COMMENT ON COLUMN prometheus_alert.env IS 'config environment';
COMMENT ON COLUMN prometheus_alert.enabled IS 'enabled';
COMMENT ON COLUMN prometheus_alert.priority IS 'priority';
COMMENT ON COLUMN prometheus_alert.created_by IS 'creator';
COMMENT ON COLUMN prometheus_alert.created_time IS 'created time';
COMMENT ON COLUMN prometheus_alert.updated_time IS 'updated time';
COMMENT ON COLUMN prometheus_alert.deleted_by IS 'delete user';
COMMENT ON COLUMN prometheus_alert.deleted_time IS 'deleted time';
COMMENT ON COLUMN prometheus_alert.prom_cluster IS 'prometheus cluster name';
COMMENT ON COLUMN prometheus_alert.status IS 'Was the configuration successfully sent: pending、success';
COMMENT ON COLUMN prometheus_alert.instances IS 'Instances where the configuration takes effect, separated by commas.';
COMMENT ON COLUMN prometheus_alert.thresholds_op IS 'Multiple threshold operators, supporting "or" or "and".';
COMMENT ON COLUMN prometheus_alert.thresholds IS 'Alarm threshold array (use this field in simple mode)';
COMMENT ON COLUMN prometheus_alert.type IS 'Mode, simple mode is 0, complex mode is 1.';
COMMENT ON COLUMN prometheus_alert.alert_member IS 'alert_member';
COMMENT ON COLUMN prometheus_alert.alert_at_people IS 'alert at people';
COMMENT ON COLUMN prometheus_alert.annotations IS 'annotations';
COMMENT ON COLUMN prometheus_alert.alert_group IS 'group';

CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_timestamp_trigger
BEFORE UPDATE ON prometheus_alert
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();


<!--scrape_config--
CREATE TABLE scrape_config
(
    id serial PRIMARY KEY,
    prom_cluster varchar(100) NOT NULL DEFAULT 'public',
    region varchar(128) NOT NULL DEFAULT '',
    zone varchar(128) NOT NULL DEFAULT '',
    env varchar(100) NOT NULL DEFAULT '',
    status varchar(32) NOT NULL DEFAULT 'pending',
    instances varchar(255) DEFAULT '',
    job_name varchar(255) NOT NULL DEFAULT '',
    body jsonb NOT NULL,
    created_by varchar(100) NOT NULL DEFAULT '',
    created_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_by varchar(100) DEFAULT NULL,
    deleted_time timestamp NULL DEFAULT NULL
);

-- 添加注释
COMMENT ON TABLE scrape_config IS 'scrape_config表';
COMMENT ON COLUMN scrape_config.prom_cluster IS 'prometheus集群名称';
COMMENT ON COLUMN scrape_config.region IS '区域';
COMMENT ON COLUMN scrape_config.zone IS '区域';
COMMENT ON COLUMN scrape_config.env IS '配置环境: staging, preview, production';
COMMENT ON COLUMN scrape_config.status IS '任务的当前状态（是否已成功分配）: pending、success';
COMMENT ON COLUMN scrape_config.instances IS '采集任务的示例：逗号分隔';
COMMENT ON COLUMN scrape_config.job_name IS '采集任务的名称';
COMMENT ON COLUMN scrape_config.body IS 'scrape_config结构的JSON字符串';
COMMENT ON COLUMN scrape_config.created_by IS '创建者';
COMMENT ON COLUMN scrape_config.created_time IS '创建时间';
COMMENT ON COLUMN scrape_config.updated_time IS '更新时间';
COMMENT ON COLUMN scrape_config.deleted_by IS '删除者';
COMMENT ON COLUMN scrape_config.deleted_time IS '删除时间';

-- 添加索引
CREATE INDEX idx_prom_cluster ON scrape_config (prom_cluster);
CREATE INDEX idx_region ON scrape_config (region);
CREATE INDEX idx_zone ON scrape_config (zone);




<!--silence-->
CREATE TABLE "silence"
(
"id"           SERIAL PRIMARY KEY,
"uuid"         varchar(100) COLLATE "en_US.utf8" NOT NULL,
"comment"      varchar(255) COLLATE "en_US.utf8" NOT NULL,
"start_time"   timestamp NOT NULL,
"end_time"     timestamp NOT NULL,
"created_by"   varchar(255) COLLATE "en_US.utf8" NOT NULL,
"created_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
"updated_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
"prom_cluster" varchar(100) COLLATE "en_US.utf8" DEFAULT 'public',
"status"       varchar(32) COLLATE "en_US.utf8" NOT NULL DEFAULT 'pending',
"alert_id"      varchar(255) COLLATE "en_US.utf8" NOT NULL DEFAULT '0'
);

COMMENT ON TABLE "silence" IS 'silence table';
COMMENT ON COLUMN "silence"."uuid" IS 'silence uuid';
COMMENT ON COLUMN "silence"."comment" IS 'creator';
COMMENT ON COLUMN "silence"."start_time" IS 'silence start time';
COMMENT ON COLUMN "silence"."end_time" IS 'silence end time';
COMMENT ON COLUMN "silence"."created_by" IS 'creator';
COMMENT ON COLUMN "silence"."created_time" IS 'created time';
COMMENT ON COLUMN "silence"."updated_time" IS 'updated time';
COMMENT ON COLUMN "silence"."prom_cluster" IS 'prometheus cluster name';
COMMENT ON COLUMN "silence"."status" IS 'Was the configuration successfully deployed : pending、success';
COMMENT ON COLUMN "silence"."alert_id" IS 'alert id';



<!--silence_matcher-->
CREATE TABLE silence_matcher
(
silence_id int NOT NULL,
name       varchar(255) COLLATE "en_US.utf8" NOT NULL,
value      varchar(255) COLLATE "en_US.utf8" NOT NULL,
is_regex   smallint NOT NULL,
is_equal   smallint NOT NULL
);

COMMENT ON COLUMN silence_matcher.silence_id IS 'silence id';
COMMENT ON COLUMN silence_matcher.name IS 'name';
COMMENT ON COLUMN silence_matcher.value IS 'value';
COMMENT ON COLUMN silence_matcher.is_regex IS 'if is regex matcher';
COMMENT ON COLUMN silence_matcher.is_equal IS 'if is equal matcher';

