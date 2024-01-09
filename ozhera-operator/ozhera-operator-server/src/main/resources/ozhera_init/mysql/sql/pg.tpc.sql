CREATE DATABASE mi_tpc
  WITH OWNER = postgres
       ENCODING = 'UTF8';

-- 使用数据库
\c mi_tpc;

CREATE TABLE account_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(128),
    content varchar(64),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp NOT NULL,
    update_time timestamp NOT NULL,
    deleted int DEFAULT '0',
    account varchar(64) DEFAULT '' UNIQUE,
    pwd varchar(64) DEFAULT '',
    name varchar(64)
);

COMMENT ON TABLE account_entity IS 'Table comment for account_entity';
COMMENT ON COLUMN account_entity.id IS 'Unique Record';
COMMENT ON COLUMN account_entity.type IS 'Type';
COMMENT ON COLUMN account_entity.status IS 'Status';
COMMENT ON COLUMN account_entity."desc" IS 'Description';
COMMENT ON COLUMN account_entity.content IS 'Content';
COMMENT ON COLUMN account_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN account_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN account_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN account_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN account_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN account_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN account_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN account_entity.update_time IS 'Update Time';
COMMENT ON COLUMN account_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN account_entity.account IS 'Account';
COMMENT ON COLUMN account_entity.pwd IS 'Password';
COMMENT ON COLUMN account_entity.name IS 'Name';



CREATE TABLE user_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(128),
    content varchar(128),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    account varchar(64) DEFAULT '',
    CONSTRAINT idx_account UNIQUE (account)
);

COMMENT ON TABLE user_entity IS 'Table comment for user_entity';
COMMENT ON COLUMN user_entity.id IS 'Unique Record';
COMMENT ON COLUMN user_entity.type IS 'Type';
COMMENT ON COLUMN user_entity.status IS 'Status';
COMMENT ON COLUMN user_entity."desc" IS 'Description';
COMMENT ON COLUMN user_entity.content IS 'Content';
COMMENT ON COLUMN user_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN user_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN user_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN user_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN user_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN user_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN user_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN user_entity.update_time IS 'Update Time';
COMMENT ON COLUMN user_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN user_entity.account IS 'Account';


CREATE TABLE user_group_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(128),
    content varchar(128),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    group_name varchar(32) DEFAULT ''
);

COMMENT ON TABLE user_group_entity IS 'Table comment for user_group_entity';
COMMENT ON COLUMN user_group_entity.id IS 'Unique Record';
COMMENT ON COLUMN user_group_entity.type IS 'Type';
COMMENT ON COLUMN user_group_entity.status IS 'Status';
COMMENT ON COLUMN user_group_entity."desc" IS 'Description';
COMMENT ON COLUMN user_group_entity.content IS 'Content';
COMMENT ON COLUMN user_group_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN user_group_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN user_group_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN user_group_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN user_group_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN user_group_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN user_group_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN user_group_entity.update_time IS 'Update Time';
COMMENT ON COLUMN user_group_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN user_group_entity.group_name IS 'Group Name';



CREATE TABLE user_group_rel_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(64),
    content varchar(64),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    group_id int DEFAULT '0',
    user_id int DEFAULT '0',
    account varchar(64) DEFAULT '',
    user_type int DEFAULT '0',
    CONSTRAINT idx_group_id FOREIGN KEY (group_id) REFERENCES user_group_entity(id),
    CONSTRAINT idx_user_id FOREIGN KEY (user_id) REFERENCES user_entity(id),
    CONSTRAINT idx_account UNIQUE (account)
);



CREATE TABLE node_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(128),
    content varchar(256),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    parent_id int DEFAULT '0',
    parent_type int DEFAULT '0',
    top_id int DEFAULT '0',
    top_type int DEFAULT '0',
    node_name varchar(64) DEFAULT '',
    out_id int DEFAULT '0',
    out_id_type int DEFAULT '0',
    env_flag int DEFAULT '0',
    code varchar(64) DEFAULT '',
    env text,
    CONSTRAINT idx_parent_id FOREIGN KEY (parent_id) REFERENCES node_entity(id),
    CONSTRAINT idx_top_id FOREIGN KEY (top_id) REFERENCES node_entity(id),
    CONSTRAINT idx_out_id FOREIGN KEY (out_id) REFERENCES node_entity(id),
    CONSTRAINT uk_node_name UNIQUE (node_name)
);
COMMENT ON TABLE node_entity IS 'Table comment for node_entity';
COMMENT ON COLUMN node_entity.id IS 'Unique Record';
COMMENT ON COLUMN node_entity.type IS 'type';
COMMENT ON COLUMN node_entity.status IS 'status';
COMMENT ON COLUMN node_entity."desc" IS 'description';
COMMENT ON COLUMN node_entity.content IS 'content';
COMMENT ON COLUMN node_entity.creater_id IS 'creator ID';
COMMENT ON COLUMN node_entity.creater_acc IS 'creator account';
COMMENT ON COLUMN node_entity.creater_type IS 'creator type';
COMMENT ON COLUMN node_entity.updater_id IS 'updater ID';
COMMENT ON COLUMN node_entity.updater_acc IS 'updater account';
COMMENT ON COLUMN node_entity.updater_type IS 'updater type';
COMMENT ON COLUMN node_entity.create_time IS 'create time';
COMMENT ON COLUMN node_entity.update_time IS 'update time';
COMMENT ON COLUMN node_entity.deleted IS 'delete or not 0->no 1->deleted';
COMMENT ON COLUMN node_entity.parent_id IS 'parent node Id';
COMMENT ON COLUMN node_entity.parent_type IS 'parent node type';
COMMENT ON COLUMN node_entity.top_id IS 'root node ID';
COMMENT ON COLUMN node_entity.top_type IS 'root node type';
COMMENT ON COLUMN node_entity.node_name IS 'node name';
COMMENT ON COLUMN node_entity.out_id IS 'external ID type';
COMMENT ON COLUMN node_entity.out_id_type IS 'external ID type';
COMMENT ON COLUMN node_entity.env_flag IS 'env flag';
COMMENT ON COLUMN node_entity.code IS 'node encoding';
COMMENT ON COLUMN node_entity.env IS '环境变量';



CREATE TABLE node_user_rel_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(64),
    content varchar(64),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    user_id int DEFAULT '0',
    account varchar(64) DEFAULT '',
    user_type int DEFAULT '0',
    node_id int DEFAULT '0',
    node_type int DEFAULT '0',
    tester int DEFAULT '0',
    CONSTRAINT idx_user_id FOREIGN KEY (user_id) REFERENCES node_user_rel_entity(id),
    CONSTRAINT idx_node_id FOREIGN KEY (node_id) REFERENCES node_entity(id),
    CONSTRAINT uk_user_account UNIQUE (account)
);
COMMENT ON TABLE node_user_rel_entity IS 'Table comment for node_user_rel_entity';
COMMENT ON COLUMN node_user_rel_entity.id IS 'Unique Record';
COMMENT ON COLUMN node_user_rel_entity.type IS 'Type';
COMMENT ON COLUMN node_user_rel_entity.status IS 'Status';
COMMENT ON COLUMN node_user_rel_entity."desc" IS 'Description';
COMMENT ON COLUMN node_user_rel_entity.content IS 'Content';
COMMENT ON COLUMN node_user_rel_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN node_user_rel_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN node_user_rel_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN node_user_rel_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN node_user_rel_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN node_user_rel_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN node_user_rel_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN node_user_rel_entity.update_time IS 'Update Time';
COMMENT ON COLUMN node_user_rel_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN node_user_rel_entity.user_id IS 'User ID';
COMMENT ON COLUMN node_user_rel_entity.account IS 'Account';
COMMENT ON COLUMN node_user_rel_entity.user_type IS 'User Type';
COMMENT ON COLUMN node_user_rel_entity.node_id IS 'Node ID';
COMMENT ON COLUMN node_user_rel_entity.node_type IS 'Node Type';
COMMENT ON COLUMN node_user_rel_entity.tester IS '0 for No, 1 for Yes';



CREATE TABLE node_user_group_rel_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(64),
    content varchar(64),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    user_group_id int DEFAULT '0',
    user_group_name varchar(64) DEFAULT '',
    node_id int DEFAULT '0',
    node_type int DEFAULT '0',
    CONSTRAINT idx_user_group_id FOREIGN KEY (user_group_id) REFERENCES node_user_group_rel_entity(id),
    CONSTRAINT idx_node_id FOREIGN KEY (node_id) REFERENCES node_entity(id),
    CONSTRAINT uk_user_group_name UNIQUE (user_group_name)
);
COMMENT ON TABLE node_user_group_rel_entity IS 'Table comment for node_user_group_rel_entity';
COMMENT ON COLUMN node_user_group_rel_entity.id IS 'Unique Record';
COMMENT ON COLUMN node_user_group_rel_entity.type IS 'Type';
COMMENT ON COLUMN node_user_group_rel_entity.status IS 'Status';
COMMENT ON COLUMN node_user_group_rel_entity."desc" IS 'Description';
COMMENT ON COLUMN node_user_group_rel_entity.content IS 'Content';
COMMENT ON COLUMN node_user_group_rel_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN node_user_group_rel_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN node_user_group_rel_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN node_user_group_rel_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN node_user_group_rel_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN node_user_group_rel_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN node_user_group_rel_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN node_user_group_rel_entity.update_time IS 'Update Time';
COMMENT ON COLUMN node_user_group_rel_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN node_user_group_rel_entity.user_group_id IS 'User Group ID';
COMMENT ON COLUMN node_user_group_rel_entity.user_group_name IS 'User Group Name';
COMMENT ON COLUMN node_user_group_rel_entity.node_id IS 'Node ID';
COMMENT ON COLUMN node_user_group_rel_entity.node_type IS 'Node Type';



CREATE TABLE flag_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(128),
    content varchar(128),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    parent_id int DEFAULT '0',
    flag_name varchar(64) DEFAULT '',
    flag_key varchar(128),
    flag_val varchar(128),
    CONSTRAINT idx_parent_id FOREIGN KEY (parent_id) REFERENCES flag_entity(id),
    CONSTRAINT uk_flag_key UNIQUE (flag_key)
);
COMMENT ON TABLE flag_entity IS 'Table comment for flag_entity';
COMMENT ON COLUMN flag_entity.id IS 'Unique Record';
COMMENT ON COLUMN flag_entity.type IS 'Type';
COMMENT ON COLUMN flag_entity.status IS 'Status';
COMMENT ON COLUMN flag_entity."desc" IS 'Description';
COMMENT ON COLUMN flag_entity.content IS 'Content';
COMMENT ON COLUMN flag_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN flag_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN flag_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN flag_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN flag_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN flag_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN flag_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN flag_entity.update_time IS 'Update Time';
COMMENT ON COLUMN flag_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN flag_entity.parent_id IS 'Parent node ID';
COMMENT ON COLUMN flag_entity.flag_name IS 'Name';
COMMENT ON COLUMN flag_entity.flag_key IS 'Key';
COMMENT ON COLUMN flag_entity.flag_val IS 'Value';



-- 创建表 `node_resource_rel_entity`：
CREATE TABLE node_resource_rel_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(64),
    content varchar(64),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    resource_id int DEFAULT '0',
    resource_type int DEFAULT '0',
    node_id int DEFAULT '0',
    node_type int DEFAULT '0',
    CONSTRAINT uk_resource_node UNIQUE (resource_id, node_id)
);


-- 创建表 `resource_entity`：
CREATE TABLE resource_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(128),
    content text,
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    pool_node_id int DEFAULT '0',
    apply_id int DEFAULT '0',
    resource_name varchar(64) DEFAULT '',
    key1 varchar(64) DEFAULT '',
    key2 varchar(256) DEFAULT '',
    env_flag int DEFAULT '0',
    is_open_kc int DEFAULT '0',
    sid varchar(255) DEFAULT '',
    kc_user varchar(255) DEFAULT '',
    mfa varchar(255) DEFAULT '',
    region int DEFAULT '0',
    CONSTRAINT idx_pool_node_id FOREIGN KEY (pool_node_id) REFERENCES resource_entity(id),
    CONSTRAINT idx_apply_id FOREIGN KEY (apply_id) REFERENCES resource_entity(id),
    CONSTRAINT uk_resource_name UNIQUE (resource_name)
);
COMMENT ON TABLE resource_entity IS 'Table comment for resource_entity';
COMMENT ON COLUMN resource_entity.id IS 'Unique Record';
COMMENT ON COLUMN resource_entity.type IS 'Type';
COMMENT ON COLUMN resource_entity.status IS 'Status';
COMMENT ON COLUMN resource_entity."desc" IS 'Description';
COMMENT ON COLUMN resource_entity.content IS 'Content';
COMMENT ON COLUMN resource_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN resource_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN resource_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN resource_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN resource_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN resource_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN resource_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN resource_entity.update_time IS 'Update Time';
COMMENT ON COLUMN resource_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN resource_entity.pool_node_id IS 'Resource Pool ID';
COMMENT ON COLUMN resource_entity.apply_id IS 'Application Workorder ID';
COMMENT ON COLUMN resource_entity.resource_name IS 'Resource Name';
COMMENT ON COLUMN resource_entity.key1 IS 'Resource Tag 1';
COMMENT ON COLUMN resource_entity.key2 IS 'Resource Tag 2';
COMMENT ON COLUMN resource_entity.env_flag IS 'Environment Flag';
COMMENT ON COLUMN resource_entity.is_open_kc IS 'Whether to Use Keycenter (0: Not Used, 1: Used)';
COMMENT ON COLUMN resource_entity.sid IS 'SID Used by Keycenter';
COMMENT ON COLUMN resource_entity.kc_user IS 'Keycenter User';
COMMENT ON COLUMN resource_entity.mfa IS 'Keycenter MFA';
COMMENT ON COLUMN resource_entity.region IS 'Resource Region';



-- 创建表 `apply_entity`：
CREATE TABLE apply_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(128),
    content text,
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    cur_node_id int DEFAULT '0',
    cur_node_type int DEFAULT '0',
    apply_node_id int DEFAULT '0',
    apply_node_type int DEFAULT '0',
    apply_user_id int DEFAULT '0',
    apply_account varchar(64) DEFAULT '',
    apply_user_type int DEFAULT '0',
    apply_name varchar(64) DEFAULT '',
    CONSTRAINT uk_apply_account UNIQUE (apply_account)
);
COMMENT ON TABLE apply_entity IS 'Table comment for apply_entity';
COMMENT ON COLUMN apply_entity.id IS 'Unique Record';
COMMENT ON COLUMN apply_entity.type IS 'Type';
COMMENT ON COLUMN apply_entity.status IS 'Status';
COMMENT ON COLUMN apply_entity."desc" IS 'Description';
COMMENT ON COLUMN apply_entity.content IS 'Content';
COMMENT ON COLUMN apply_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN apply_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN apply_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN apply_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN apply_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN apply_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN apply_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN apply_entity.update_time IS 'Update Time';
COMMENT ON COLUMN apply_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN apply_entity.cur_node_id IS 'Current Node ID';
COMMENT ON COLUMN apply_entity.cur_node_type IS 'Current Node Type';
COMMENT ON COLUMN apply_entity.apply_node_id IS 'Application Node ID';
COMMENT ON COLUMN apply_entity.apply_node_type IS 'Application Node Type';
COMMENT ON COLUMN apply_entity.apply_user_id IS 'Applicant ID';
COMMENT ON COLUMN apply_entity.apply_account IS 'Applicant Account';
COMMENT ON COLUMN apply_entity.apply_user_type IS 'Applicant User Type';
COMMENT ON COLUMN apply_entity.apply_name IS 'Application Name';

-- 创建表 `apply_approval_entity`：
CREATE TABLE apply_approval_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(128),
    content text,
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    apply_id int DEFAULT '0',
    apply_type int DEFAULT '0',
    cur_node_id int DEFAULT '0',
    cur_node_type int DEFAULT '0',
    approval_name varchar(64) DEFAULT ''
);
COMMENT ON TABLE apply_approval_entity IS 'Table comment for apply_approval_entity';
COMMENT ON COLUMN apply_approval_entity.id IS 'Unique Record';
COMMENT ON COLUMN apply_approval_entity.type IS 'Type';
COMMENT ON COLUMN apply_approval_entity.status IS 'Status';
COMMENT ON COLUMN apply_approval_entity."desc" IS 'Description';
COMMENT ON COLUMN apply_approval_entity.content IS 'Content';
COMMENT ON COLUMN apply_approval_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN apply_approval_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN apply_approval_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN apply_approval_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN apply_approval_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN apply_approval_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN apply_approval_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN apply_approval_entity.update_time IS 'Update Time';
COMMENT ON COLUMN apply_approval_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN apply_approval_entity.apply_id IS 'Application ID';
COMMENT ON COLUMN apply_approval_entity.apply_type IS 'Application Type';
COMMENT ON COLUMN apply_approval_entity.cur_node_id IS 'Approval Node ID';
COMMENT ON COLUMN apply_approval_entity.cur_node_type IS 'Approval Node Type';
COMMENT ON COLUMN apply_approval_entity.approval_name IS 'Approval Name';



-- 创建表 `system_entity`：
CREATE TABLE system_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(128),
    content varchar(128),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    system_name varchar(64) DEFAULT '',
    system_token varchar(64) DEFAULT '',
    CONSTRAINT idx_system_name UNIQUE (system_name),
    CONSTRAINT idx_system_token UNIQUE (system_token)
);
COMMENT ON TABLE system_entity IS 'Table comment for system_entity';
COMMENT ON COLUMN system_entity.id IS 'Unique Record';
COMMENT ON COLUMN system_entity.type IS 'Type';
COMMENT ON COLUMN system_entity.status IS 'Status';
COMMENT ON COLUMN system_entity."desc" IS 'Description';
COMMENT ON COLUMN system_entity.content IS 'Content';
COMMENT ON COLUMN system_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN system_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN system_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN system_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN system_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN system_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN system_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN system_entity.update_time IS 'Update Time';
COMMENT ON COLUMN system_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN system_entity.system_name IS 'System Name';
COMMENT ON COLUMN system_entity.system_token IS 'System Token';

-- 创建表 `permission_entity`：
CREATE TABLE permission_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(128),
    content varchar(128),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    system_id int DEFAULT '0',
    permission_name varchar(64) DEFAULT '',
    path varchar(128) DEFAULT '',
    CONSTRAINT idx_system_id FOREIGN KEY (system_id) REFERENCES system_entity(id),
    CONSTRAINT idx_path UNIQUE (path)
);
COMMENT ON TABLE permission_entity IS 'Table comment for permission_entity';
COMMENT ON COLUMN permission_entity.id IS 'Unique Record';
COMMENT ON COLUMN permission_entity.type IS 'Type';
COMMENT ON COLUMN permission_entity.status IS 'Status';
COMMENT ON COLUMN permission_entity."desc" IS 'Description';
COMMENT ON COLUMN permission_entity.content IS 'Content';
COMMENT ON COLUMN permission_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN permission_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN permission_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN permission_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN permission_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN permission_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN permission_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN permission_entity.update_time IS 'Update Time';
COMMENT ON COLUMN permission_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN permission_entity.system_id IS 'System ID';
COMMENT ON COLUMN permission_entity.permission_name IS 'Permission Name';
COMMENT ON COLUMN permission_entity.path IS 'Permission URL';

-- 创建表 `role_entity`：
CREATE TABLE role_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(128),
    content varchar(128),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    system_id int DEFAULT '0',
    role_name varchar(64) DEFAULT '',
    node_id int DEFAULT '0',
    node_type int DEFAULT '0',
);
COMMENT ON TABLE role_entity IS 'Table comment for role_entity';
COMMENT ON COLUMN role_entity.id IS 'Unique Record';
COMMENT ON COLUMN role_entity.type IS 'Type';
COMMENT ON COLUMN role_entity.status IS 'Status';
COMMENT ON COLUMN role_entity."desc" IS 'Description';
COMMENT ON COLUMN role_entity.content IS 'Content';
COMMENT ON COLUMN role_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN role_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN role_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN role_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN role_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN role_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN role_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN role_entity.update_time IS 'Update Time';
COMMENT ON COLUMN role_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN role_entity.system_id IS 'System ID';
COMMENT ON COLUMN role_entity.role_name IS 'Role Name';
COMMENT ON COLUMN role_entity.node_id IS 'Node ID';
COMMENT ON COLUMN role_entity.node_type IS 'Node Type';



-- 创建表 `role_permission_rel_entity`：
CREATE TABLE role_permission_rel_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(64),
    content varchar(64),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    system_id int DEFAULT '0',
    permission_id int DEFAULT '0',
    role_id int DEFAULT '0',
    CONSTRAINT idx_permission_id FOREIGN KEY (permission_id) REFERENCES permission_entity(id),
    CONSTRAINT idx_role_id FOREIGN KEY (role_id) REFERENCES role_entity(id)
);
COMMENT ON TABLE role_permission_rel_entity IS 'Table comment for role_permission_rel_entity';
COMMENT ON COLUMN role_permission_rel_entity.id IS 'Unique Record';
COMMENT ON COLUMN role_permission_rel_entity.type IS 'Type';
COMMENT ON COLUMN role_permission_rel_entity.status IS 'Status';
COMMENT ON COLUMN role_permission_rel_entity."desc" IS 'Description';
COMMENT ON COLUMN role_permission_rel_entity.content IS 'Content';
COMMENT ON COLUMN role_permission_rel_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN role_permission_rel_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN role_permission_rel_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN role_permission_rel_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN role_permission_rel_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN role_permission_rel_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN role_permission_rel_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN role_permission_rel_entity.update_time IS 'Update Time';
COMMENT ON COLUMN role_permission_rel_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN role_permission_rel_entity.system_id IS 'System ID';
COMMENT ON COLUMN role_permission_rel_entity.permission_id IS 'Permission ID';
COMMENT ON COLUMN role_permission_rel_entity.role_id IS 'Role ID';

-- 创建表 `user_node_role_rel_entity`：
CREATE TABLE user_node_role_rel_entity
(
    id SERIAL PRIMARY KEY,
    type int DEFAULT '0',
    status int DEFAULT '0',
    "desc" varchar(64),
    content varchar(64),
    creater_id int DEFAULT '0',
    creater_acc varchar(64),
    creater_type int DEFAULT '0',
    updater_id int DEFAULT '0',
    updater_acc varchar(64),
    updater_type int DEFAULT '0',
    create_time timestamp,
    update_time timestamp,
    deleted int DEFAULT '0',
    user_id int DEFAULT '0',
    account varchar(64) DEFAULT '',
    user_type int DEFAULT '0',
    node_id int DEFAULT '0',
    node_type int DEFAULT '0',
    role_id int DEFAULT '0',
    system_id int DEFAULT '0',
    CONSTRAINT idx_user_id FOREIGN KEY (user_id) REFERENCES user_entity(id),
    CONSTRAINT idx_account UNIQUE (account),
    CONSTRAINT idx_user_node_id FOREIGN KEY (node_id) REFERENCES node_entity(id),
    CONSTRAINT idx_role_id FOREIGN KEY (role_id) REFERENCES role_entity(id)
);
COMMENT ON TABLE user_node_role_rel_entity IS 'Table comment for user_node_role_rel_entity';
COMMENT ON COLUMN user_node_role_rel_entity.id IS 'Unique Record';
COMMENT ON COLUMN user_node_role_rel_entity.type IS 'Type';
COMMENT ON COLUMN user_node_role_rel_entity.status IS 'Status';
COMMENT ON COLUMN user_node_role_rel_entity."desc" IS 'Description';
COMMENT ON COLUMN user_node_role_rel_entity.content IS 'Content';
COMMENT ON COLUMN user_node_role_rel_entity.creater_id IS 'Creator ID';
COMMENT ON COLUMN user_node_role_rel_entity.creater_acc IS 'Creator Account';
COMMENT ON COLUMN user_node_role_rel_entity.creater_type IS 'Creator Type';
COMMENT ON COLUMN user_node_role_rel_entity.updater_id IS 'Updater ID';
COMMENT ON COLUMN user_node_role_rel_entity.updater_acc IS 'Updater Account';
COMMENT ON COLUMN user_node_role_rel_entity.updater_type IS 'Updater Type';
COMMENT ON COLUMN user_node_role_rel_entity.create_time IS 'Creation Time';
COMMENT ON COLUMN user_node_role_rel_entity.update_time IS 'Update Time';
COMMENT ON COLUMN user_node_role_rel_entity.deleted IS '0 for Normal, 1 for Deleted';
COMMENT ON COLUMN user_node_role_rel_entity.user_id IS 'User ID';
COMMENT ON COLUMN user_node_role_rel_entity.account IS 'Account';
COMMENT ON COLUMN user_node_role_rel_entity.user_type IS 'User Type';
COMMENT ON COLUMN user_node_role_rel_entity.node_id IS 'Node ID';
COMMENT ON COLUMN user_node_role_rel_entity.node_type IS 'Node Type';
COMMENT ON COLUMN user_node_role_rel_entity.role_id IS 'Role ID';
COMMENT ON COLUMN user_node_role_rel_entity.system_id IS 'System ID';

