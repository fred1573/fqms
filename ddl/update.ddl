--20150915升级DDL开始--
----------代销客栈表添加客栈名称字段
alter table tomato_proxysale_inn add COLUMN inn_name VARCHAR;
----------添加详细地址字段
alter table tomato_proxysale_inn add COLUMN inn_addr VARCHAR;
-----------删除客栈详情表detail字段
alter table tomato_proxysale_inn_detail DROP COLUMN detail;
-----------删除客栈详情表contract_url字段
alter table tomato_proxysale_inn_detail DROP COLUMN contract_url;

DROP TABLE IF EXISTS "public"."tomato_proxysale_audit";
CREATE TABLE "public"."tomato_proxysale_audit" (
"id" serial,
"inn_id" int4,
"record_no" varchar COLLATE "default",
"status" varchar COLLATE "default",
"auditor" int4,
"audit_time" timestamp(6),
"type" int2,
"pattern" int2,
"reason" varchar COLLATE "default"
)
WITH (OIDS=FALSE);

COMMENT ON COLUMN "public"."tomato_proxysale_audit"."inn_id" IS '客栈ID';
COMMENT ON COLUMN "public"."tomato_proxysale_audit"."record_no" IS '合同编号/价格审核单号';
COMMENT ON COLUMN "public"."tomato_proxysale_audit"."status" IS '审核状态';
COMMENT ON COLUMN "public"."tomato_proxysale_audit"."auditor" IS '审核人ID';
COMMENT ON COLUMN "public"."tomato_proxysale_audit"."audit_time" IS '审核时间';
COMMENT ON COLUMN "public"."tomato_proxysale_audit"."type" IS '1-价格   2-合同';
COMMENT ON COLUMN "public"."tomato_proxysale_audit"."pattern" IS '合同审核没有该字段    1-精品    2-普通';
COMMENT ON COLUMN "public"."tomato_proxysale_audit"."reason" IS '审核失败原因';

ALTER TABLE "public"."tomato_proxysale_audit" ADD PRIMARY KEY ("id");


-- 线上操作
--新建临时表temp_inn_id_name
DROP TABLE IF EXISTS "public"."temp_inn_id_name";
CREATE TABLE "public"."temp_inn_id_name" (
"inn_id" int4 NOT NULL,
"inn_name" varchar COLLATE "default",
"inn_addr" varchar COLLATE "default"
)
WITH (OIDS=FALSE);
---查询需要迁移的数据
select DISTINCT t1.inn_id, t2.brand_name, t2.addr
from tomato_oms_open_account t1 INNER JOIN tomato_oms_inn t2 on t1.oms_user_id=t2.oms_user_id
where t1.ota_id=102;

-- 更新客栈名称和客栈详细地址到tomato_proxysale_inn表
UPDATE tomato_proxysale_inn
SET inn_name = temp_inn_id_name.inn_name, inn_addr = temp_inn_id_name.inn_addr
FROM
 temp_inn_id_name
WHERE
 tomato_proxysale_inn.inn = temp_inn_id_name.inn_id;

 drop table IF EXISTS finance_parent_order;

create table finance_parent_order (
   id                   int4                 not null,
   user_name            varchar(32)          null,
   contact              varchar(16)          null,
   channel_id           int2                 null,
   channel_order_no     varchar(50)          null,
   channel_price_policy char                 null,
   channel_up_ratio     DECIMAL(12,2)        null,
   channel_commission_ratio DECIMAL(12,2)        null,
   inn_price_policy     char                 null,
   inn_commission_ratio DECIMAL(12,2)        null,
   total_amount         DECIMAL(12,2)        null,
   paid_amount          DECIMAL(12,2)        null,
   paid_payment         DECIMAL(12,2)        null,
   operated_user        varchar(64)          null,
   order_time           timestamp            null,
   pay_time             timestamp            null,
   balance_time         timestamp            null,
   is_balance           char                 null,
   status               char                 null,
   inn_id               int4                 null,
   fx_channel_id        int2                 null,
   account_id           int4                 null,
   remark               varchar              null,
   pay_type             varchar              null,
   inn_name             varchar(64)          null,
   channel_name         varchar(64)          null,
   price_strategy       char                 null,
   increase_rate        DECIMAL(12,2)        null,
   channe_commission_rate DECIMAL(12,2)        null,
   fq_commission_rate   DECIMAL(12,2)        null,
   cost_type            char                 null,
   is_audit             BOOL                 null,
   is_arrival           BOOL                 null,
   settlement_time      varchar(10)          null,
   constraint PK_FINANCE_PARENT_ORDER primary key (id)
);

comment on table finance_parent_order is
'父订单表';

comment on column finance_parent_order.id is
'主键ID';

comment on column finance_parent_order.user_name is
'下单人姓名';

comment on column finance_parent_order.contact is
'下单人联系电话';

comment on column finance_parent_order.channel_id is
'渠道来源标示';

comment on column finance_parent_order.channel_order_no is
'渠道订单编号';

comment on column finance_parent_order.channel_price_policy is
'渠道价格策略(1:底价 2:卖价)';

comment on column finance_parent_order.channel_up_ratio is
'渠道上浮比例';

comment on column finance_parent_order.channel_commission_ratio is
'渠道分佣比例';

comment on column finance_parent_order.inn_price_policy is
'客栈价格策略(1:底价 2:卖价 3:底价+卖价)';

comment on column finance_parent_order.inn_commission_ratio is
'客栈卖价时抽佣比例';

comment on column finance_parent_order.total_amount is
'订单总金额';

comment on column finance_parent_order.paid_amount is
'订单已付金额';

comment on column finance_parent_order.paid_payment is
'订单已付押金';

comment on column finance_parent_order.operated_user is
'操作人';

comment on column finance_parent_order.order_time is
'下单时间';

comment on column finance_parent_order.pay_time is
'支付时间';

comment on column finance_parent_order.balance_time is
'结算时间';

comment on column finance_parent_order.is_balance is
'是否结算（0:未结算,1:已结算）';

comment on column finance_parent_order.status is
'状态（0:未处理、1:已接受（已分房）、2:已拒绝、3:已取消、4:验证失败、5：已接受（未分房））';

comment on column finance_parent_order.inn_id is
'关联客栈id';

comment on column finance_parent_order.fx_channel_id is
'分销渠道来源标示';

comment on column finance_parent_order.account_id is
'渠道开通编号(客栈)';

comment on column finance_parent_order.remark is
'备注';

comment on column finance_parent_order.pay_type is
'支付类型，prepay(预付)、assure(担保) ';

comment on column finance_parent_order.inn_name is
'客栈名称';

comment on column finance_parent_order.channel_name is
'渠道名称';

comment on column finance_parent_order.price_strategy is
'价格策略(1:底价 2:卖价)';

comment on column finance_parent_order.increase_rate is
'底价模式番茄加价比例';

comment on column finance_parent_order.channe_commission_rate is
'卖价时渠道佣金比例';

comment on column finance_parent_order.fq_commission_rate is
'卖价时番茄佣金比例';

comment on column finance_parent_order.cost_type is
'费用类型(1:房费，2:违约金)';

comment on column finance_parent_order.is_audit is
'是否核单';

comment on column finance_parent_order.is_arrival is
'渠道商款项是否收到';

comment on column finance_parent_order.settlement_time is
'对账时间';


drop table IF EXISTS finance_order;

create table finance_order (
   id                   int4                 not null,
   check_in_at          date                 null,
   check_out_at         date                 null,
   original_price       float8               null,
   book_price           float8               null,
   sale_price           float8               null,
   channel_room_type_name varchar(32)          null,
   room_no              varchar(32)          null,
   main_id              int4                 null,
   room_type_nums       int4                 null,
   room_type_id         int4                 null,
   constraint PK_FINANCE_ORDER primary key (id)
);

comment on table finance_order is
'子订单表';

comment on column finance_order.id is
'主键ID';

comment on column finance_order.check_in_at is
'入住日期';

comment on column finance_order.check_out_at is
'退房日期';

comment on column finance_order.original_price is
'代销平台中录入的原价';

comment on column finance_order.book_price is
'进价，也是下单的预定价格';

comment on column finance_order.sale_price is
'给第三方渠道的售价';

comment on column finance_order.channel_room_type_name is
'所关联的房型名称';

comment on column finance_order.room_no is
'所关联的房间号';

comment on column finance_order.main_id is
'主订单id';

comment on column finance_order.room_type_nums is
'房型间数';

comment on column finance_order.room_type_id is
'房型ID';

alter table finance_order
   add constraint FK_FINANCE__REFERENCE_FINANCE_ foreign key (main_id)
      references finance_parent_order (id)
      on delete restrict on update restrict;


drop table IF EXISTS finance_channel_settlement;

/*==============================================================*/
/* Table: financ_channel_settlement                             */
/*==============================================================*/
create table financ_channel_settlement (
   id                   int4                 not null,
   channel_id           int4                 null,
   channel_name         varchar(64)          null,
   total_order          int4                 null,
   channel_settlement_amount DECIMAL(12,2)        null,
   is_audit             BOOL                 null,
   is_arrival           BOOL                 null,
   settlement_time      varchar(10)          null,
   constraint PK_FINANC_CHANNEL_SETTLEMENT primary key (id)
);

comment on table financ_channel_settlement is
'渠道结算表';

comment on column financ_channel_settlement.id is
'主键ID';

comment on column financ_channel_settlement.channel_id is
'渠道ID';

comment on column financ_channel_settlement.channel_name is
'渠道名称';

comment on column financ_channel_settlement.total_order is
'订单总数';

comment on column financ_channel_settlement.channel_settlement_amount is
'渠道结算金额';

comment on column financ_channel_settlement.is_audit is
'是否核单';

comment on column financ_channel_settlement.is_arrival is
'渠道商款项是否收到';

comment on column financ_channel_settlement.settlement_time is
'结算时间';


drop table IF EXISTS finance_inn_settlement;

/*==============================================================*/
/* Table: financ_innl_settlement                                */
/*==============================================================*/
create table financ_inn_settlement (
   id                   int4                 not null,
   inn_id               int4                 null,
   inn_name             varchar(64)          null,
   inn_contact          varchar(16)          null,
   inn_collection_info  varchar(256)         null,
   total_order          int4                 null,
   channel_settlement_amount DECIMAL(12,2)        null,
   fq_settlement_amount DECIMAL(12,2)        null,
   innl_settlement_amount DECIMAL(12,2)        null,
   is_confirm           BOOL                 null,
   is_settlement        BOOL                 null,
   settlement_time      varchar(10)          null,
   constraint PK_FINANC_INN_SETTLEMENT primary key (id)
);

comment on table financ_inn_settlement is
'客栈结算表';

comment on column financ_inn_settlement.id is
'主键ID';

comment on column financ_inn_settlement.inn_id is
'客栈ID';

comment on column financ_inn_settlement.inn_name is
'客栈名称';

comment on column financ_inn_settlement.inn_contact is
'客栈联系电话';

comment on column financ_inn_settlement.inn_collection_info is
'客栈收款信息';

comment on column financ_inn_settlement.total_order is
'订单总数';

comment on column financ_inn_settlement.channel_settlement_amount is
'渠道商结算金额';

comment on column financ_inn_settlement.fq_settlement_amount is
'番茄结算金额';

comment on column financ_inn_settlement.innl_settlement_amount is
'客栈结算金额';

comment on column financ_inn_settlement.is_confirm is
'客栈是否确认';

comment on column financ_inn_settlement.is_settlement is
'是否结算';

comment on column financ_inn_settlement.settlement_time is
'结算时间';


/*==============================================================*/
/* Table: tomato_proxysale_close_log                            */
/*==============================================================*/
create table tomato_proxysale_close_log (
   id                   SERIAL not null,
   date_created         timestamp            null,
   date_updated         timestamp            null,
   creator              int4                 null,
   modifior             int4                 null,
   close_type           varchar(20)          null,
   area_id              int4                 null,
   inn_id               int4                 null,
   constraint PK_TOMATO_PROXYSALE_CLOSE_LOG primary key (id)
);

comment on table tomato_proxysale_close_log is
'关房记录表';

comment on column tomato_proxysale_close_log.id is
'主键ID';

comment on column tomato_proxysale_close_log.date_created is
'创建时间';

comment on column tomato_proxysale_close_log.date_updated is
'最后修改时间';

comment on column tomato_proxysale_close_log.creator is
'创建人';

comment on column tomato_proxysale_close_log.modifior is
'最后修改人';

comment on column tomato_proxysale_close_log.close_type is
'关房类型';

comment on column tomato_proxysale_close_log.area_id is
'区域ID,关房类型为AREA时有值';

comment on column tomato_proxysale_close_log.inn_id is
'客栈ID,关房类型为INN时有值';


/*==============================================================*/
/* Table: tomato_proxysale_close_date                           */
/*==============================================================*/
create table tomato_proxysale_close_date (
   id                   SERIAL not null,
   log_id               int4                 null,
   close_begin_date     varchar(10)          null,
   close_end_date       varchar(10)          null,
   status               varchar(1)           null,
   constraint PK_TOMATO_PROXYSALE_CLOSE_DATE primary key (id)
);

comment on table tomato_proxysale_close_date is
'关房时间表';

comment on column tomato_proxysale_close_date.id is
'主键ID';

comment on column tomato_proxysale_close_date.log_id is
'关房记录ID';

comment on column tomato_proxysale_close_date.close_begin_date is
'关房开始日期';

comment on column tomato_proxysale_close_date.close_end_date is
'关房结束日期';

comment on column tomato_proxysale_close_date.status is
'状态(0:有效,1:无效)';

alter table tomato_proxysale_close_date
   add constraint FK_TOMATO_P_REFERENCE_TOMATO_P foreign key (log_id)
      references tomato_proxysale_close_log (id)
      on delete restrict on update restrict;


--20150915升级DDL结束--

drop table if EXISTS finance_parent_order;
create table finance_parent_order (
   id                   int4                 not null,
   user_name            varchar(32)          null,
   contact              varchar(16)          null,
   channel_id           int2                 null,
   channel_order_no     varchar(50)          null,
   channel_price_policy char                 null,
   channel_up_ratio     DECIMAL(12,2)        null,
   channel_commission_ratio DECIMAL(12,2)        null,
   inn_price_policy     char                 null,
   inn_commission_ratio DECIMAL(12,2)        null,
   total_amount         DECIMAL(12,2)        null,
   paid_amount          DECIMAL(12,2)        null,
   paid_payment         DECIMAL(12,2)        null,
   operated_user        varchar(64)          null,
   order_time           timestamp            null,
   pay_time             timestamp            null,
   balance_time         timestamp            null,
   is_balance           char                 null,
   status               char                 null,
   inn_id               int4                 null,
   fx_channel_id        int2                 null,
   account_id           int4                 null,
   remark               varchar              null,
   pay_type             varchar              null,
   inn_name             varchar(64)          null,
   channel_name         varchar(64)          null,
   price_strategy       char                 null,
   increase_rate        DECIMAL(12,2)        null,
   channel_commission_rate DECIMAL(12,2)        null,
   fq_commission_rate   DECIMAL(12,2)        null,
   cost_type            char                 null,
   audit_status         varchar(2)           null,
   channel_settlement_amount DECIMAL(12,2)        null,
   fq_settlement_amount DECIMAL(12,2)        null,
   inn_settlement_amount DECIMAL(12,2)        null,
   is_arrival           BOOL                 null,
   settlement_time      varchar(10)          null,
   constraint PK_FINANCE_PARENT_ORDER primary key (id)
);
comment on table finance_parent_order is '父订单表';
comment on column finance_parent_order.id is '主键ID';
comment on column finance_parent_order.user_name is '下单人姓名';
comment on column finance_parent_order.contact is '下单人联系电话';
comment on column finance_parent_order.channel_id is '渠道来源标示';
comment on column finance_parent_order.channel_order_no is '渠道订单编号';
comment on column finance_parent_order.channel_price_policy is '渠道价格策略(1:底价 2:卖价)';
comment on column finance_parent_order.channel_up_ratio is '渠道上浮比例';
comment on column finance_parent_order.channel_commission_ratio is '渠道分佣比例';
comment on column finance_parent_order.inn_price_policy is '客栈价格策略(1:底价 2:卖价 3:底价+卖价)';
comment on column finance_parent_order.inn_commission_ratio is '客栈卖价时抽佣比例';
comment on column finance_parent_order.total_amount is '订单总金额';
comment on column finance_parent_order.paid_amount is '订单已付金额';
comment on column finance_parent_order.paid_payment is '订单已付押金';
comment on column finance_parent_order.operated_user is '操作人';
comment on column finance_parent_order.order_time is '下单时间';
comment on column finance_parent_order.pay_time is '支付时间';
comment on column finance_parent_order.balance_time is '结算时间';
comment on column finance_parent_order.is_balance is '是否结算（0:未结算,1:已结算）';
comment on column finance_parent_order.status is '状态（0:未处理、1:已接受（已分房）、2:已拒绝、3:已取消、4:验证失败、5：已接受（未分房））';
comment on column finance_parent_order.inn_id is '关联客栈id';
comment on column finance_parent_order.fx_channel_id is '分销渠道来源标示';
comment on column finance_parent_order.account_id is '渠道开通编号(客栈)';
comment on column finance_parent_order.remark is '备注';
comment on column finance_parent_order.pay_type is '支付类型，prepay(预付)、assure(担保) ';
comment on column finance_parent_order.inn_name is '客栈名称';
comment on column finance_parent_order.channel_name is '渠道名称';
comment on column finance_parent_order.price_strategy is '价格策略(1:底价 2:卖价)';
comment on column finance_parent_order.increase_rate is '底价模式番茄加价比例';
comment on column finance_parent_order.channel_commission_rate is '卖价时渠道佣金比例';
comment on column finance_parent_order.fq_commission_rate is '卖价时番茄佣金比例';
comment on column finance_parent_order.cost_type is '费用类型(1:房费，2:违约金)';
comment on column finance_parent_order.audit_status is '核单状态(0:未核,1:已核成功,2:已核失败)';
comment on column finance_parent_order.channel_settlement_amount is '渠道商结算金额';
comment on column finance_parent_order.fq_settlement_amount is '番茄结算金额';
comment on column finance_parent_order.inn_settlement_amount is '客栈结算金额';
comment on column finance_parent_order.is_arrival is '渠道商款项是否收到';
comment on column finance_parent_order.settlement_time is '对账时间';

drop table if EXISTS finance_order;
create table finance_order (
   id                   int4                 not null,
   check_in_at          date                 null,
   check_out_at         date                 null,
   original_price       float8               null,
   book_price           float8               null,
   sale_price           float8               null,
   channel_room_type_name varchar(32)          null,
   room_no              varchar(32)          null,
   main_id              int4                 null,
   room_type_nums       int4                 null,
   room_type_id         int4                 null,
   nights               int4                 null,
   constraint PK_FINANCE_ORDER primary key (id)
);
comment on table finance_order is '子订单表';
comment on column finance_order.id is '主键ID';
comment on column finance_order.check_in_at is '入住日期';
comment on column finance_order.check_out_at is '退房日期';
comment on column finance_order.original_price is '代销平台中录入的原价';
comment on column finance_order.book_price is '进价，也是下单的预定价格';
comment on column finance_order.sale_price is '给第三方渠道的售价';
comment on column finance_order.channel_room_type_name is '所关联的房型名称';
comment on column finance_order.room_no is '所关联的房间号';
comment on column finance_order.main_id is '主订单id';
comment on column finance_order.room_type_nums is '房型间数';
comment on column finance_order.room_type_id is '房型ID';
comment on column finance_order.nights is '夜数';

drop table if EXISTS finance_channel_settlement;
create table finance_channel_settlement (
   id                   SERIAL not null,
   channel_id           int4                 null,
   channel_name         varchar(64)          null,
   total_order          int4                 null,
   channel_settlement_amount DECIMAL(12,2)        null,
   audit_status         varchar(2)           null,
   is_arrival           BOOL                 null,
   settlement_time      varchar(10)          null,
   constraint PK_FINANCE_CHANNEL_SETTLEMENT primary key (id)
);
comment on table finance_channel_settlement is '渠道结算表';
comment on column finance_channel_settlement.id is '主键ID';
comment on column finance_channel_settlement.channel_id is '渠道ID';
comment on column finance_channel_settlement.channel_name is '渠道名称';
comment on column finance_channel_settlement.total_order is '订单总数';
comment on column finance_channel_settlement.channel_settlement_amount is '渠道结算金额';
comment on column finance_channel_settlement.audit_status is '核单状态(0:未核,1:已核成功,2:已核失败)';
comment on column finance_channel_settlement.is_arrival is '渠道商款项是否收到';
comment on column finance_channel_settlement.settlement_time is '结算时间';


drop table if EXISTS finance_inn_settlement;
create table finance_inn_settlement (
   id                   SERIAL not null,
   inn_id               int4                 null,
   inn_name             varchar(64)          null,
   inn_contact          varchar(16)          null,
   bank_type            varchar(10)          null,
   bank_account         varchar(64)          null,
   bank_code            varchar(64)          null,
   bank_name            varchar(64)          null,
   bank_region          varchar(64)          null,
   total_order          int4                 null,
   channel_settlement_amount DECIMAL(12,2)        null,
   fq_settlement_amount DECIMAL(12,2)        null,
   inn_settlement_amount DECIMAL(12,2)        null,
   confirm_status       BOOL                 null,
   is_settlement        BOOL                 null,
   settlement_time      varchar(10)          null,
   bill_status          BOOL                 null,
   constraint PK_FINANCE_INN_SETTLEMENT primary key (id)
);
comment on table finance_inn_settlement is '客栈结算表';
comment on column finance_inn_settlement.id is '主键ID';
comment on column finance_inn_settlement.inn_id is '客栈ID';
comment on column finance_inn_settlement.inn_name is '客栈名称';
comment on column finance_inn_settlement.inn_contact is '客栈联系电话';
comment on column finance_inn_settlement.bank_type is '开户类型(1:个人,1:对公)';
comment on column finance_inn_settlement.bank_account is '开户人姓名';
comment on column finance_inn_settlement.bank_code is '银行卡号';
comment on column finance_inn_settlement.bank_name is '开户行';
comment on column finance_inn_settlement.bank_region is '开户行地址';
comment on column finance_inn_settlement.total_order is '订单总数';
comment on column finance_inn_settlement.channel_settlement_amount is '渠道商结算金额';
comment on column finance_inn_settlement.fq_settlement_amount is '番茄结算金额';
comment on column finance_inn_settlement.inn_settlement_amount is '客栈结算金额';
comment on column finance_inn_settlement.confirm_status is '客栈是否确认';
comment on column finance_inn_settlement.is_settlement is '是否结算';
comment on column finance_inn_settlement.settlement_time is '结算时间';
comment on column finance_inn_settlement.bill_status is '账单状态';


drop table if EXISTS finance_operation_log;
create table finance_operation_log (
   id                   SERIAL not null,
   operate_user         varchar(64)          null,
   operate_time         TIMESTAMP            null,
   settlement_time      varchar(10)          null,
   inn_id               int4                 null,
   inn_name             varchar(64)          null,
   channel_id           int4                 null,
   channel_name         varchar(64)          null,
   operate_content      varchar(256)         null,
   operate_type         varchar(64)          null
);
comment on table finance_operation_log is '财务对账操作记录';
comment on column finance_operation_log.id is '主键ID';
comment on column finance_operation_log.operate_user is '操作人';
comment on column finance_operation_log.operate_time is '操作时间';
comment on column finance_operation_log.settlement_time is '结算时间';
comment on column finance_operation_log.inn_id is '客栈ID';
comment on column finance_operation_log.inn_name is '客栈名称';
comment on column finance_operation_log.channel_id is '渠道ID';
comment on column finance_operation_log.channel_name is '渠道名称';
comment on column finance_operation_log.operate_content is '操作内容';
comment on column finance_operation_log.operate_type is '操作类型';
--20151009升级DDL结束--
--20151016升级DDL结束--
ALTER TABLE "public"."finance_inn_settlement"
ALTER COLUMN "confirm_status" TYPE varchar(2);
--20151016升级DDL结束--

--2015-10-12升级DDL开始--
--添加客栈上下架推送字段
alter table tomato_proxysale_channel add inn_push_url VARCHAR;
--添加佣金推送字段
alter table tomato_proxysale_channel add commission_push_url VARCHAR;
--添加OTA链接字段
alter table tomato_proxysale_inn add ota_link VARCHAR;
--添加手机号字段
alter table tomato_proxysale_inn add phone VARCHAR;
--从tomato_proxysale_inn_detail迁移ota_link和phone到tomato_proxysale_inn
update tomato_proxysale_inn t1 set phone=t2.phone, ota_link=t2.ota_link
from tomato_proxysale_inn_detail t2
where t1."id"=t2.proxy_inn
--更新TP淘宝客栈上下架推送地址和佣金推送地址
update tomato_proxysale_channel set inn_push_url='http://toms.fanqiele.com/api/hotel/update.json', commission_push_url='http://toms.fanqiele.com/api/commission/update.json'
where id=903;
--2015-10-12升级DDL结束--

--2015-11-19升级DDL结束--
-- 客栈结算表添加是否标注字段
ALTER TABLE "public"."finance_inn_settlement"
ADD COLUMN "is_tagged" bool;
-- 客栈结算表添加订单总金额字段
ALTER TABLE "public"."finance_inn_settlement"
ADD COLUMN "total_amount" numeric(12,2);
COMMENT ON COLUMN "public"."finance_inn_settlement"."total_amount" IS '订单总金额';
-- 渠道结算表添加订单总金额字段
ALTER TABLE "public"."finance_channel_settlement"
ADD COLUMN "total_amount" numeric(12,2);
COMMENT ON COLUMN "public"."finance_channel_settlement"."total_amount" IS '订单总金额';

--创建代销客栈上下架日志流水表
DROP TABLE IF EXISTS "public"."tomato_proxysale_inn_onoff";
create table tomato_proxysale_inn_onoff (
   id                   SERIAL               not null,
   proxy_inn            INT4                 null,
   pattern              INT2                 null,
   "time"               TIMESTAMP            null,
   operator             INT4                 null,
   operate_type         VARCHAR              null,
   remark               VARCHAR              null,
   constraint PK_TOMATO_PROXYSALE_INN_ONOFF primary key (id)
);
comment on table tomato_proxysale_inn_onoff is '代销客栈上下架记录表';
comment on column tomato_proxysale_inn_onoff.id is '主键ID';
comment on column tomato_proxysale_inn_onoff.proxy_inn is '代销客栈ID';
comment on column tomato_proxysale_inn_onoff.pattern is '价格模式';
comment on column tomato_proxysale_inn_onoff."time" is '操作时间';
comment on column tomato_proxysale_inn_onoff.operator is '操作人';
comment on column tomato_proxysale_inn_onoff.operate_type is '操作类型';
comment on column tomato_proxysale_inn_onoff.remark is '备注(下架原因)';

--数据迁移tomato_sys_authority和tomato_sys_role_authority表
--序列迁移tomato_sys_authority_id_seq和tomato_sys_role_authority_id_seq

--修改客栈渠道关联关系表
ALTER TABLE "public"."tomato_proxysale_channel_inn"
ADD COLUMN "strategy" int2;
COMMENT ON COLUMN "public"."tomato_proxysale_channel_inn"."strategy" IS '价格策略';
ALTER TABLE "public"."tomato_proxysale_channel_inn"
ADD COLUMN "create_time" timestamp;
COMMENT ON COLUMN "public"."tomato_proxysale_channel_inn"."create_time" IS '创建时间';
ALTER TABLE "public"."tomato_proxysale_channel_inn"
ADD COLUMN "valid" bool;
COMMENT ON COLUMN "public"."tomato_proxysale_channel_inn"."valid" IS '是否删除';
ALTER TABLE "public"."tomato_proxysale_channel_inn"
ADD COLUMN "operator" int4;
COMMENT ON COLUMN "public"."tomato_proxysale_channel_inn"."operator" IS '操作人ID';

--执行数据迁移的存储过程
-- Function: test2()
-- DROP FUNCTION test2();
CREATE OR REPLACE FUNCTION test2()
  RETURNS character varying AS
$BODY$
        declare
         v_count integer;
         v_proxy_inn integer;
         v_channel_id integer;
          select_all cursor for   select proxy_inn,channel from  tomato_proxysale_channel_inn  ;
         begin
           open select_all;
           loop
		v_count := v_count + 1;
		fetch select_all into v_proxy_inn,v_channel_id;
		exit when v_proxy_inn is null;
               PERFORM 1   from  tomato_proxysale_price_strategy where channel = v_channel_id and valid  = true  and  strategy = 1;  --判断渠道是否有开通低价
               if  found
               then
                   PERFORM 1   from  tomato_proxysale_price_pattern where proxy_inn = v_proxy_inn and valid  = true  and  pattern = 1; -- 如果渠道开通低价 判断客栈是否开通低价
                   if found
                   then
                insert into tomato_proxysale_channel_inn(proxy_inn,channel,strategy,valid) values (v_proxy_inn,v_channel_id,1,true);
                 end if;
                end if;
                  PERFORM 1  from  tomato_proxysale_price_strategy where channel = v_channel_id and valid  = true  and  strategy = 2;
               if  found
               then
                   PERFORM 1  from  tomato_proxysale_price_pattern where proxy_inn = v_proxy_inn and valid  = true  and  pattern = 2;
               if found
                   then
                  insert into tomato_proxysale_channel_inn(proxy_inn,channel,strategy,valid) values (v_proxy_inn,v_channel_id,2,true);
               end if;
                end if;
           end loop;
         return 'ok';
         end;
       $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION test2()
  OWNER TO ota;

  select test2();

--删除历史数据
DELETE FROM tomato_proxysale_channel_inn WHERE "valid" is null;

--修改新增数据的创建时间和创建人字段
UPDATE tomato_proxysale_channel_inn SET create_time =now(),"operator"=1;
--2015-11-19升级DDL结束--
--客栈结算标注添加默认值
UPDATE finance_inn_settlement SET is_tagged=FALSE;
--删除重复的渠道的价格策略
delete from tomato_proxysale_price_strategy where "id" in(120, 124);

--20151204升级DDL开始
--创建移除记录表
DROP TABLE IF EXISTS "public"."tomato_proxysale_inn_del_log";
CREATE TABLE "public"."tomato_proxysale_inn_del_log" (
"id" SERIAL NOT NULL,
"proxy_inn" int4,
"del_time" timestamp(6),
"_user" int4,
"reason" varchar COLLATE "default",
constraint PK_TOMATO_PROXYSALE_INN_DEL_LOG primary key (id)
)
WITH (OIDS=FALSE);
COMMENT ON COLUMN "public"."tomato_proxysale_inn_del_log"."del_time" IS '删除时间';
COMMENT ON COLUMN "public"."tomato_proxysale_inn_del_log"."reason" IS '移除原因';

--删除代销客栈表唯一约束
ALTER TABLE "public"."tomato_proxysale_inn"
DROP CONSTRAINT "inn_unique" ;
--代销日志操作记录添加操作对象字段
ALTER TABLE "public"."finance_operation_log"
ADD COLUMN "operate_object" varchar(256);
--同步tomato_sys_authority、tomato_sys_role_authority到生产环境
--序列迁移tomato_sys_authority_id_seq和tomato_sys_role_authority_id_seq
--20151204升级DDL结束

--20151215升级DDL开始
-- 客栈结算表添加微信open id 字段
ALTER TABLE "public"."finance_inn_settlement"
ADD COLUMN "wx_open_id" varchar(64);
COMMENT ON COLUMN "public"."finance_inn_settlement"."wx_open_id" IS '微信唯一标识';
--20151215升级DDL结束
--1、客栈结算表添加短信发送手机号码字段
ALTER TABLE "public"."finance_inn_settlement"
ADD COLUMN "contact1" varchar(11),
ADD COLUMN "contact2" varchar(11);

COMMENT ON COLUMN "public"."finance_inn_settlement"."contact1" IS '短信发送手机号码1';

COMMENT ON COLUMN "public"."finance_inn_settlement"."contact2" IS '短信发送手机号码2';
--2、子订单表添加是否删除字段
ALTER TABLE "public"."finance_order"
ADD COLUMN "deleted" bool DEFAULT FALSE;

COMMENT ON COLUMN "public"."finance_order"."deleted" IS '是否删除(f:未删除,t:删除)';
--3、增加报表统计权限
INSERT INTO "public"."tomato_sys_authority" (
	"id",
	"sys_authority_code",
	"sys_authority_name",
	"status",
	"create_user_code",
	"create_time",
	"update_user_code",
	"update_time",
	"rmk"
)
VALUES
	(
		(SELECT nextval('tomato_sys_authority_id_seq')),
		'report_statistics',
		'报表统计',
		'1',
		'root',
		'2015-11-30 00:04:10',
		NULL,
		NULL,
		NULL
	);

--4、给ROOT用户授予报表统计权限
INSERT INTO "public"."tomato_sys_role_authority" (
"id",
"sys_role_id",
"sys_authority_id")
VALUES
	(
		(SELECT nextval('tomato_sys_role_authority_id_seq')),
		1,
		(SELECT id from tomato_sys_authority WHERE sys_authority_name='报表统计')
	);
--20151215升级DDL结束

--20151225升级DDL开始
--增加订单间夜数
--增加总房间数，总夜数栏
ALTER TABLE "public"."finance_parent_order"
ADD COLUMN "room_nights" int4 DEFAULT 0;
ADD COLUMN "rooms" int4 DEFAULT 0,
ADD COLUMN "nights" int4 DEFAULT 0;
ADD COLUMN "reservation_days" int4 DEFAULT 0,
ADD COLUMN "stay_days" int4 DEFAULT 0;
COMMENT ON COLUMN "public"."finance_parent_order"."room_nights" IS '订单间夜数';
COMMENT ON COLUMN "public"."finance_parent_order"."rooms" IS '总房间数';
COMMENT ON COLUMN "public"."finance_parent_order"."nights" IS '总夜数';
COMMENT ON COLUMN "public"."finance_parent_order"."reservation_days" IS '提前预定天数';
COMMENT ON COLUMN "public"."finance_parent_order"."stay_days" IS '停留天数';

--渠道结算表添加实收金额、备注字段
ALTER TABLE "public"."finance_channel_settlement"
ADD COLUMN "income_amount" numeric(12,2) DEFAULT 0.00,
ADD COLUMN "remarks" varchar;
COMMENT ON COLUMN "public"."finance_channel_settlement"."income_amount" IS '实收金额';
COMMENT ON COLUMN "public"."finance_channel_settlement"."remarks" IS '实收情况备注';
--20151225升级DDL结束

--20160111升级DDL开始
	--修改finance_parent_order表的settlement_time长度为32
	ALTER TABLE "public"."finance_parent_order"
	ALTER COLUMN "settlement_time" TYPE varchar(32) COLLATE "default";
	--finance_parent_order表的settlement_time由yyyy-MM格式改为yyyy-MM-dd至yyyy-MM-dd
	UPDATE finance_parent_order SET settlement_time='2015-09-01至2015-09-30' WHERE settlement_time='2015-09';
	UPDATE finance_parent_order SET settlement_time='2015-10-01至2015-10-31' WHERE settlement_time='2015-10';
	UPDATE finance_parent_order SET settlement_time='2015-11-01至2015-11-30' WHERE settlement_time='2015-11';
	UPDATE finance_parent_order SET settlement_time='2015-12-01至2015-12-31' WHERE settlement_time='2015-12';

--修改finance_channel_settlement表的settlement_time长度为32
ALTER TABLE "public"."finance_channel_settlement"
ALTER COLUMN "settlement_time" TYPE varchar(32) COLLATE "default";
--finance_channel_settlement表的settlement_time由yyyy-MM格式改为yyyy-MM-dd至yyyy-MM-dd
UPDATE finance_channel_settlement SET settlement_time='2015-09-01至2015-09-30' WHERE settlement_time='2015-09';
UPDATE finance_channel_settlement SET settlement_time='2015-10-01至2015-10-31' WHERE settlement_time='2015-10';
UPDATE finance_channel_settlement SET settlement_time='2015-11-01至2015-11-30' WHERE settlement_time='2015-11';
UPDATE finance_channel_settlement SET settlement_time='2015-12-01至2015-12-31' WHERE settlement_time='2015-12';

--修改finance_inn_settlement表的settlement_time长度为32
ALTER TABLE "public"."finance_inn_settlement"
ALTER COLUMN "settlement_time" TYPE varchar(32) COLLATE "default";
--finance_inn_settlement表的settlement_time由yyyy-MM格式改为yyyy-MM-dd至yyyy-MM-dd
UPDATE finance_inn_settlement SET settlement_time='2015-09-01至2015-09-30' WHERE settlement_time='2015-09';
UPDATE finance_inn_settlement SET settlement_time='2015-10-01至2015-10-31' WHERE settlement_time='2015-10';
UPDATE finance_inn_settlement SET settlement_time='2015-11-01至2015-11-30' WHERE settlement_time='2015-11';
UPDATE finance_inn_settlement SET settlement_time='2015-12-01至2015-12-31' WHERE settlement_time='2015-12';

--修改finance_operation_log表的settlement_time长度为32
ALTER TABLE "public"."finance_operation_log"
ALTER COLUMN "settlement_time" TYPE varchar(32) COLLATE "default";
--finance_operation_log表的settlement_time由yyyy-MM格式改为yyyy-MM-dd至yyyy-MM-dd
UPDATE finance_operation_log SET settlement_time='2015-09-01至2015-09-30' WHERE settlement_time='2015-09';
UPDATE finance_operation_log SET settlement_time='2015-10-01至2015-10-31' WHERE settlement_time='2015-10';
UPDATE finance_operation_log SET settlement_time='2015-11-01至2015-11-30' WHERE settlement_time='2015-11';
UPDATE finance_operation_log SET settlement_time='2015-12-01至2015-12-31' WHERE settlement_time='2015-12';

--创建账期表
DROP TABLE IF EXISTS finance_account_period;
CREATE TABLE finance_account_period (
	ID SERIAL NOT NULL,
	date_created TIMESTAMP NULL,
	date_updated TIMESTAMP NULL,
	creator int4 NULL,
	modifior int4 NULL,
	VERSION int4 NULL,
	deleted BOOL NULL DEFAULT FALSE,
	settlement_time VARCHAR (32) NULL,
	account_status VARCHAR (2) NULL DEFAULT '0',
	send_bill_status BOOL NULL DEFAULT FALSE,
	CONSTRAINT PK_FINANCE_ACCOUNT_PERIOD PRIMARY KEY (ID)
);
comment on table finance_account_period is '账期表';
comment on column finance_account_period.id is '主键ID';
comment on column finance_account_period.date_created is '创建时间';
comment on column finance_account_period.date_updated is '最后修改时间';
comment on column finance_account_period.creator is '创建人';
comment on column finance_account_period.modifior is '最后修改人';
comment on column finance_account_period.version is '版本号';
comment on column finance_account_period.deleted is '是否逻辑删除';
comment on column finance_account_period.settlement_time is '结算周期(yyyy-MM-dd至yyyy-MM-dd)';
comment on column finance_account_period.account_status is '账期状态(0:正常,1:锁定)';
comment on column finance_account_period.send_bill_status is '是否发送账单（默认false:未发送,true:发送）';
--为账期表的结算周期字段添加唯一索引
ALTER TABLE finance_account_period ADD UNIQUE (settlement_time)
--初始化账期表数据
--重置序列select setval('finance_account_period_id_seq', 1, false)
INSERT INTO "public"."finance_account_period" ("id", "date_created", "date_updated", "creator", "modifior", "version", "deleted", "settlement_time", "account_status", "send_bill_status") VALUES (nextval('finance_account_period_id_seq'), now(), now(), '1', '1', '0', 'f', '2015-09-01至2015-09-30', '1', 't');
INSERT INTO "public"."finance_account_period" ("id", "date_created", "date_updated", "creator", "modifior", "version", "deleted", "settlement_time", "account_status", "send_bill_status") VALUES (nextval('finance_account_period_id_seq'), now(), now(), '1', '1', '0', 'f', '2015-10-01至2015-10-31', '1', 't');
INSERT INTO "public"."finance_account_period" ("id", "date_created", "date_updated", "creator", "modifior", "version", "deleted", "settlement_time", "account_status", "send_bill_status") VALUES (nextval('finance_account_period_id_seq'), now(), now(), '1', '1', '0', 'f', '2015-11-01至2015-11-30', '1', 't');
INSERT INTO "public"."finance_account_period" ("id", "date_created", "date_updated", "creator", "modifior", "version", "deleted", "settlement_time", "account_status", "send_bill_status") VALUES (nextval('finance_account_period_id_seq'), now(), now(), '1', '1', '0', 'f', '2015-12-01至2015-12-31', '1', 'f');

--增加结算账期权限
INSERT INTO "public"."tomato_sys_authority" (
	"id",
	"sys_authority_code",
	"sys_authority_name",
	"status",
	"create_user_code",
	"create_time",
	"update_user_code",
	"update_time",
	"rmk"
)
VALUES
	(
		(SELECT nextval('tomato_sys_authority_id_seq')),
		'account_period',
		'结算账期',
		'1',
		'root',
		'2015-11-30 00:40:01',
		NULL,
		NULL,
		NULL
	);

--给ROOT用户授予结算账期权限
INSERT INTO "public"."tomato_sys_role_authority" (
"id",
"sys_role_id",
"sys_authority_id")
VALUES
	(
		(SELECT nextval('tomato_sys_role_authority_id_seq')),
		1,
		(SELECT id from tomato_sys_authority WHERE sys_authority_name='结算账期')
	);

	--客栈表添加间夜量字段
ALTER TABLE "public"."finance_inn_settlement"
ADD COLUMN "room_nights" int4 DEFAULT 0;
COMMENT ON COLUMN "public"."finance_inn_settlement"."room_nights" IS '间夜量';

--同步订单表的间夜数到客栈表
UPDATE finance_inn_settlement  SET room_nights=(SELECT SUM(fpo.room_nights) FROM finance_parent_order fpo WHERE fpo.inn_id=finance_inn_settlement.inn_id AND fpo.settlement_time=finance_inn_settlement.settlement_time AND fpo.status='1')
--20160111升级DDL结束
--20160114升级DDL开始
--创建客栈结算基本信息表
drop table IF EXISTS finance_inn_settlement_info;
create table finance_inn_settlement_info (
    id                   int4                 not null,
   inn_name             varchar(64)          null,
   region_id            int4                 null,
   region_name          varchar(64)          null,
   inn_contact          varchar(16)          null,
   contact1             varchar(11)          null,
   contact2             varchar(11)          null,
   wx_open_id           varchar(64)          null,
   bank_type            varchar(10)          null,
   bank_account         varchar(64)          null,
   bank_code            varchar(64)          null,
   bank_name            varchar(64)          null,
   bank_region          varchar(64)          null,
   date_updated         timestamp            null,
   constraint PK_FINANCE_INN_SETTLEMENT_INFO primary key (id)
);
comment on table finance_inn_settlement_info is '客栈结算基本信息表';
comment on column finance_inn_settlement_info.id is 'PMS客栈ID';
comment on column finance_inn_settlement_info.inn_name is '客栈名称';
comment on column finance_inn_settlement_info.region_id is '目的地ID';
comment on column finance_inn_settlement_info.region_name is '目的地名称';
comment on column finance_inn_settlement_info.inn_contact is '客栈联系电话(inn_admin的mobile)';
comment on column finance_inn_settlement_info.contact1 is '短信发送手机号码1(inn的contact1)';
comment on column finance_inn_settlement_info.contact2 is '短信发送手机号码2(inn的contact2)';
comment on column finance_inn_settlement_info.wx_open_id is '微信openId';
comment on column finance_inn_settlement_info.bank_type is '开户类型(1:个人,1:对公)';
comment on column finance_inn_settlement_info.bank_account is '开户人姓名';
comment on column finance_inn_settlement_info.bank_code is '银行卡号';
comment on column finance_inn_settlement_info.bank_name is '开户行';
comment on column finance_inn_settlement_info.bank_region is '开户行地址';
comment on column finance_inn_settlement_info.date_updated is '最后修改时间';
--20160114升级DDL结束--20160111升级DDL结束

-- 20160119升级DDL开始
-- 创建新的区域表，tomato_area作废
CREATE TABLE "public"."tomato_base_area" (
		"id" serial NOT NULL,
		"code" varchar(20) COLLATE "default",
		"name" varchar(100) COLLATE "default",
		"parent" int4,
		"level" int2
	);

-- 更新渠道表区域字段
	UPDATE tomato_proxysale_channel_area
	SET area = t."id"
	from
	(SELECT t2."id" ,t1."id" tid	from tomato_area t1
	LEFT JOIN tomato_base_area t2 ON t2."name" LIKE '%' || t1."name" || '%'
	WHERE
		t1."level" = 2
	AND t2."level" = 2) t WHERE t.tid=area;

-- 更新客栈表区域字段
UPDATE tomato_proxysale_inn
SET area = tt.id2
FROM
	(
		SELECT
			t1."id" AS id1,
			t2."id" AS id2
		FROM
			tomato_area t1
		LEFT JOIN tomato_base_area t2 ON t2."name" LIKE '%' || t1."name" || '%'
	) tt
WHERE
	tt.id1 = area;

-- 客栈表添加目的地字段
ALTER TABLE "public"."tomato_proxysale_inn" ADD COLUMN region int4;

-- 填充目的地
UPDATE tomato_proxysale_inn
	SET region = tt.reg_id
	FROM
		(
			SELECT
				t1. ID AS inn_id,
				t2."id" AS reg_id
			FROM
				tomato_inn t1
			INNER JOIN tomato_inn_region t2 ON t1.region_id = t2."id"
		) tt
	WHERE
		tt.inn_id = inn;
-- 20160119升级DDL结束
--20160121升级DDL开始
--客栈结算信息表添加开户行所在省、开户行所在城市字段
ALTER TABLE "public"."finance_inn_settlement_info"
ADD COLUMN "bank_province" varchar(20),
ADD COLUMN "bank_city" varchar(20);
COMMENT ON COLUMN "public"."finance_inn_settlement_info"."bank_province" IS '开户行所在省';
COMMENT ON COLUMN "public"."finance_inn_settlement_info"."bank_city" IS '开户行所在城市';
--创建客栈渠道账期结算基本信息表
drop table IF EXISTS finance_inn_channel_settlement;
create table finance_inn_channel_settlement (
   id                   SERIAL not null,
   date_updated         timestamp            null,
   inn_id               int4                 null,
   channel_id           int4                 null,
   channel_name         varchar(64)          null,
   settlement_time      varchar(32)          null,
   total_order          int4                 null,
   total_amount         DECIMAL(12,2)        null,
   fq_settlement_amount DECIMAL(12,2)        null,
   inn_settlement_amount DECIMAL(12,2)        null,
   channel_settlement_amount DECIMAL(12,2)        null,
   is_match             bool                 null,
   room_nights          int4                 null,
   real_payment         DECIMAL(12,2)        null,
   payment_remark       varchar              null,
   constraint PK_FINANCE_INN_CHANNEL_SETTLEM primary key (id)
);
comment on table finance_inn_channel_settlement is '客栈渠道结算表';
comment on column finance_inn_channel_settlement.id is '主键ID';
comment on column finance_inn_channel_settlement.date_updated is '最后修改时间';
comment on column finance_inn_channel_settlement.inn_id is 'PMS客栈ID';
comment on column finance_inn_channel_settlement.channel_id is '渠道ID';
comment on column finance_inn_channel_settlement.channel_name is '渠道名称';
comment on column finance_inn_channel_settlement.settlement_time is '账期';
comment on column finance_inn_channel_settlement.total_order is '订单总个数';
comment on column finance_inn_channel_settlement.total_amount is '订单总金额';
comment on column finance_inn_channel_settlement.fq_settlement_amount is '番茄结算金额';
comment on column finance_inn_channel_settlement.inn_settlement_amount is '客栈结算金额';
comment on column finance_inn_channel_settlement.channel_settlement_amount is '渠道结算金额';
comment on column finance_inn_channel_settlement.is_match is '是否账实相符';
comment on column finance_inn_channel_settlement.room_nights is '间夜量';
comment on column finance_inn_channel_settlement.real_payment is '实付金额';
comment on column finance_inn_channel_settlement.payment_remark is '实付备注';

--修改客栈结算表
ALTER TABLE "public"."finance_inn_settlement"
ADD COLUMN "settlement_status" varchar(2) DEFAULT 0;
COMMENT ON COLUMN "public"."finance_inn_settlement"."settlement_status" IS '结算状态（0:未结算，1:已结算,2:纠纷延期）';
--历史数据迁移
update finance_inn_settlement SET settlement_status='0' where is_settlement=false;
update finance_inn_settlement SET settlement_status='1' where is_settlement=true;
--删除抽出来的字段
ALTER TABLE "public"."finance_inn_settlement"
DROP COLUMN "inn_name",
DROP COLUMN "inn_contact",
DROP COLUMN "bank_type",
DROP COLUMN "bank_account",
DROP COLUMN "bank_code",
DROP COLUMN "bank_name",
DROP COLUMN "bank_region",
DROP COLUMN "is_settlement",
DROP COLUMN "contact1",
DROP COLUMN "contact2",
DROP COLUMN "wx_open_id";

--3、增加结算信息权限
INSERT INTO "public"."tomato_sys_authority" (
	"id",
	"sys_authority_code",
	"sys_authority_name",
	"status",
	"create_user_code",
	"create_time",
	"update_user_code",
	"update_time",
	"rmk"
)
VALUES
	(
		(SELECT nextval('tomato_sys_authority_id_seq')),
		'settlement_info',
		'结算信息',
		'1',
		'root',
		'2015-11-30 00:04:10',
		NULL,
		NULL,
		NULL
	);

--4、给ROOT用户授予结算信息权限
INSERT INTO "public"."tomato_sys_role_authority" (
"id",
"sys_role_id",
"sys_authority_id")
VALUES
	(
		(SELECT nextval('tomato_sys_role_authority_id_seq')),
		1,
		(SELECT id from tomato_sys_authority WHERE sys_authority_name='结算信息')
	);

drop sequence if exists seq_tomato_no_id cascade;
create sequence seq_tomato_no_id
minvalue 100;
--20160121升级DDL结束

--20160303升级DDL开始
--1、扩展父订单表,添加番茄加减价、渠道商订单价格、OMS订单号
ALTER TABLE "public"."finance_parent_order"
ADD COLUMN "extra_price" numeric(12,2) DEFAULT 0.00,
ADD COLUMN "channel_amount" numeric(12,2) DEFAULT 0.00,
ADD COLUMN "order_no" varchar;
COMMENT ON COLUMN "public"."finance_parent_order"."extra_price" IS '番茄加减价';
COMMENT ON COLUMN "public"."finance_parent_order"."channel_amount" IS '渠道商订单价格';
COMMENT ON COLUMN "public"."finance_parent_order"."order_no" IS 'OMS订单号';
--2、扩展子订单表，添加番茄加减价、渠道商单价
ALTER TABLE "public"."finance_order"
ADD COLUMN "extra_price" numeric(12,2) DEFAULT 0.00,
ADD COLUMN "channel_amount" numeric(12,2) DEFAULT 0.00;
COMMENT ON COLUMN "public"."finance_order"."extra_price" IS '番茄加减价';
COMMENT ON COLUMN "public"."finance_order"."channel_amount" IS '渠道商单价';
--3、修改客栈结算表，新增分销商订单总额
ALTER TABLE "public"."finance_inn_settlement"
ALTER COLUMN "total_amount" SET DEFAULT 0.00,
ADD COLUMN "channel_amount" numeric(12,2) DEFAULT 0.00;
COMMENT ON COLUMN "public"."finance_inn_settlement"."total_amount" IS '客栈订单总额';
COMMENT ON COLUMN "public"."finance_inn_settlement"."channel_amount" IS '分销商订单总额';
--4、修改渠道结算表，新增分销商订单总额
ALTER TABLE "public"."finance_channel_settlement"
ALTER COLUMN "total_amount" SET DEFAULT 0.00,
ADD COLUMN "channel_amount" numeric(12,2) DEFAULT 0.00;
COMMENT ON COLUMN "public"."finance_channel_settlement"."total_amount" IS '客栈订单总额';
COMMENT ON COLUMN "public"."finance_channel_settlement"."channel_amount" IS '分销商订单总额';
--5、修改客栈、渠道结算表，，新增分销商订单总额
ALTER TABLE "public"."finance_inn_channel_settlement"
ALTER COLUMN "total_amount" SET DEFAULT 0.00,
ADD COLUMN "channel_amount" numeric(12,2) DEFAULT 0.00;
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."total_amount" IS '客栈订单总额';
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."channel_amount" IS '分销商订单总额';


--6、调价管理授权给root用户
insert into tomato_sys_authority(id, sys_authority_code, sys_authority_name, status, create_user_code, create_time)
values(57, 'api_sale_platform_price', '代销平台-调价管理', 1, 'root', now());
insert into tomato_sys_role_authority(sys_role_id, sys_authority_id) values(1,57);

--7、订单表和自订单表的历史数据迁移（线上测试未执行）
UPDATE finance_parent_order SET channel_amount=total_amount WHERE channel_amount IS NULL;
UPDATE finance_order SET channel_amount=book_price WHERE channel_amount IS NULL;

--8、发送账单等业务中的短信不能正常发送
UPDATE finance_inn_settlement_info t1
SET contact1 = t2.contact1,
 contact2 = t2.contact2
FROM
	tomato_inn t2
WHERE
	t1. ID = t2. ID
--9、订单表新增客栈订单价格字段
ALTER TABLE "public"."finance_parent_order"
ADD COLUMN "inn_amount" numeric(12,2) DEFAULT 0.00;
COMMENT ON COLUMN "public"."finance_parent_order"."inn_amount" IS '客栈订单价格（PMS订单价格）';
--历史数据迁移
UPDATE finance_parent_order SET inn_amount = total_amount;

ALTER TABLE "public"."finance_order"
ADD COLUMN "inn_amount" numeric(12,2) DEFAULT 0.00;
COMMENT ON COLUMN "public"."finance_order"."inn_amount" IS '客栈订单价格（PMS订单价格）';
--历史数据迁移
UPDATE finance_order SET inn_amount = book_price;
--20160303升级DDL结束

--20160413升级DDL开始
-- 创建关房任务表
DROP TABLE
IF EXISTS tomato_proxysale_close_task;
CREATE TABLE tomato_proxysale_close_task (
	ID SERIAL NOT NULL,
	date_created TIMESTAMP NULL,
	date_updated TIMESTAMP NULL,
	creator VARCHAR (64) NULL,
	modifior VARCHAR (64) NULL,
	off_type VARCHAR (16) NULL,
	area_id int4 NULL,
	inn_id int4 NULL,
	begin_date VARCHAR (16) NULL,
	end_date VARCHAR (16) NULL,
	execute_time int4 NULL,
	CONSTRAINT PK_TOMATO_PROXYSALE_CLOSE_TASK PRIMARY KEY (ID)
);
COMMENT ON COLUMN tomato_proxysale_close_task. ID IS '主键ID';
COMMENT ON COLUMN tomato_proxysale_close_task.date_created IS '创建时间';
COMMENT ON COLUMN tomato_proxysale_close_task.date_updated IS '最后修改时间';
COMMENT ON COLUMN tomato_proxysale_close_task.creator IS '创建人';
COMMENT ON COLUMN tomato_proxysale_close_task.modifior IS '最后修改人';
COMMENT ON COLUMN tomato_proxysale_close_task.off_type IS '接口类型(1:OMS关房,2:分销商锁房)';
COMMENT ON COLUMN tomato_proxysale_close_task.area_id IS '区域ID';
COMMENT ON COLUMN tomato_proxysale_close_task.inn_id IS 'PMS客栈ID';
COMMENT ON COLUMN tomato_proxysale_close_task.begin_date IS '关房开始日期';
COMMENT ON COLUMN tomato_proxysale_close_task.end_date IS '关房结束日期';
COMMENT ON COLUMN tomato_proxysale_close_task.execute_time IS '执行次数';

--修改操作日志表中字段长度
ALTER TABLE "public"."finance_operation_log"
ALTER COLUMN "operate_content" TYPE varchar COLLATE "default";
--20160413升级DDL结束
--20160426升级DDL开始
--1、创建特殊账单表
DROP TABLE IF EXISTS finance_special_order;
CREATE TABLE finance_special_order (
	ID SERIAL NOT NULL,
	order_id VARCHAR(32) NULL,
	channel_debit DECIMAL (12, 2) NULL,
	inn_payment DECIMAL (12, 2) NULL,
	fq_bear DECIMAL (12, 2) NULL,
	fq_income DECIMAL (12, 2) NULL,
	channel_refund DECIMAL (12, 2) NULL,
	inn_refund DECIMAL (12, 2) NULL,
	fq_refund_commission DECIMAL (12, 2) NULL,
	contacts_status INT2 NULL,
	fq_refund_contacts DECIMAL (12, 2) NULL,
	inn_settlement bool DEFAULT FALSE,
	fq_replenishment DECIMAL (12, 2) NULL,
	CONSTRAINT PK_FINANCE_SPECIAL_ORDER PRIMARY KEY (ID)
);
COMMENT ON TABLE finance_special_order IS '特殊账单表';
COMMENT ON COLUMN finance_special_order. ID IS '主键ID';
COMMENT ON COLUMN finance_special_order.order_id IS '账单ID';
COMMENT ON COLUMN finance_special_order.channel_debit IS '分销商扣赔付金额(赔付)';
COMMENT ON COLUMN finance_special_order.inn_payment IS '客栈赔付金额(赔付)';
COMMENT ON COLUMN finance_special_order.fq_bear IS '客栈赔付番茄承担(赔付)';
COMMENT ON COLUMN finance_special_order.fq_income IS '客栈赔付番茄收入(赔付)';
COMMENT ON COLUMN finance_special_order.channel_refund IS '分销商扣退款金额(退款)';
COMMENT ON COLUMN finance_special_order.inn_refund IS '客栈退款金额(退款)';
COMMENT ON COLUMN finance_special_order.fq_refund_commission IS '番茄退佣金收入(退款)';
COMMENT ON COLUMN finance_special_order.contacts_status IS '往来状态(退款)';
COMMENT ON COLUMN finance_special_order.fq_refund_contacts IS '番茄退往来款(退款)';
COMMENT ON COLUMN finance_special_order.inn_settlement IS '是否与客栈结算(退款)';
COMMENT ON COLUMN finance_special_order.fq_replenishment IS '番茄补款金额(补款)';
--为账单ID添加唯一索引
ALTER TABLE "public"."finance_special_order"
ADD CONSTRAINT "UQ_ORDER_ID" UNIQUE ("order_id");

--2、创建挂账表
DROP table  IF EXISTS finance_arrear_inn;
create table finance_arrear_inn (
   id                   SERIAL               not null,
   inn_id               INT4                 null,
   status               VARCHAR(64)          null,
   arrear_past          DECIMAL(12)          DEFAULT 0,
   arrear_remaining     DECIMAL(12)          DEFAULT 0,
   settlement_time      VARCHAR(64)          null,
 		operate_time					timestamp(6)        ,
 		remark                 text,
 		manual_level            bool             DEFAULT false,
   constraint PK_FINANCE_ARREAR_INN primary key (id)
);
COMMENT ON COLUMN "public"."finance_arrear_inn"."inn_id" IS '客栈id';
COMMENT ON COLUMN "public"."finance_arrear_inn"."status" IS '客栈挂账状态(1,平账2,部分平账3,挂账)';
COMMENT ON COLUMN "public"."finance_arrear_inn"."arrear_past" IS '往期挂账';
COMMENT ON COLUMN "public"."finance_arrear_inn"."arrear_remaining" IS '剩余挂账';
COMMENT ON COLUMN "public"."finance_arrear_inn"."settlement_time" IS '账期';
COMMENT ON COLUMN "public"."finance_arrear_inn"."operate_time" IS '操作时间';
COMMENT ON COLUMN "public"."finance_arrear_inn"."remark" IS '平账备注';
COMMENT ON COLUMN "public"."finance_arrear_inn"."manual_level" IS '手动平账(f:否t:是)';


-- 3、创建无订单赔付表
DROP TABLE IF EXISTS "public"."finance_manual_order";
CREATE TABLE "public"."finance_manual_order" (
 "id" SERIAL NOT NULL,
"order_id" varchar(50),
"remark" varchar(200),
"refund" numeric(12,2),
"available" bool,
"create_time" timestamp(6),
"update_time" timestamp(6),
"create_user" int4,
"update_user" int4,
"channel" int4,
"settlement_time" varchar(50)
)
WITH (OIDS=FALSE);
COMMENT ON COLUMN "public"."finance_manual_order"."order_id" IS '订单号';
COMMENT ON COLUMN "public"."finance_manual_order"."remark" IS '备注';
COMMENT ON COLUMN "public"."finance_manual_order"."refund" IS '分销商扣番茄金额';
COMMENT ON COLUMN "public"."finance_manual_order"."available" IS '是否可用（删除-false，未删除-true）';
COMMENT ON COLUMN "public"."finance_manual_order"."create_time" IS '添加订单时间';
COMMENT ON COLUMN "public"."finance_manual_order"."update_time" IS '编辑时间';
COMMENT ON COLUMN "public"."finance_manual_order"."create_user" IS '添加订单人ID';
COMMENT ON COLUMN "public"."finance_manual_order"."update_user" IS '编辑人ID';
COMMENT ON COLUMN "public"."finance_manual_order"."channel" IS '分销商';
COMMENT ON COLUMN "public"."finance_manual_order"."settlement_time" IS '账期时间';
ALTER TABLE "public"."finance_manual_order" ADD PRIMARY KEY ("id");
-- 创建联合唯一索引
CREATE UNIQUE INDEX index_channelid_orderid ON finance_manual_order (channel, order_id);

--4、修改父账单表
--新增字段：产生周期，produce_time，添加结算状态字段
ALTER TABLE "public"."finance_parent_order"
ADD COLUMN "produce_time" varchar(32),
ADD COLUMN "settlement_status" varchar(2) DEFAULT 0;
COMMENT ON COLUMN "public"."finance_parent_order"."produce_time" IS '产生时间(默认等于settlement_time)';
COMMENT ON COLUMN "public"."finance_parent_order"."settlement_status" IS '结算状态（0:未结算，1:已结算,2:纠纷延期）';
--将父订单表的id字段修改为字符串类型
--修改父订单状态字段长度
ALTER TABLE "public"."finance_parent_order"
ALTER COLUMN "id" TYPE varchar(32),
ALTER COLUMN "status" TYPE varchar(16) COLLATE "default";

--5、修改子当单表
--修改父订单id为字符串
ALTER TABLE "public"."finance_order"
ALTER COLUMN "main_id" TYPE varchar(32),
ALTER COLUMN "id" TYPE varchar(32);

--6、修改finance_inn_settlement
ALTER TABLE "public"."finance_inn_settlement"
ADD COLUMN "inn_payment" numeric(12,2)      DEFAULT 0,
ADD COLUMN "refund_amount" numeric(12,2)      DEFAULT 0,
ADD COLUMN "fq_replenishment" numeric(12,2)     DEFAULT 0,
ADD COLUMN "is_arrears" varchar(2)  DEFAULT 0,
ADD COLUMN "after_payment_amount" numeric(12,2)     DEFAULT 0,
ADD COLUMN "after_arrears_amount" numeric(12,2)     DEFAULT 0,
ADD COLUMN "arrears_past" numeric(12,2)     DEFAULT 0,
ADD COLUMN "arrears_remaining" numeric(12,2)     DEFAULT 0,
ADD COLUMN "is_special" bool DEFAULT FALSE,
ADD COLUMN "is_match" bool DEFAULT false,
ADD COLUMN "channel_real_settlement" numeric(12,2) DEFAULT 0;
COMMENT ON COLUMN "public"."finance_inn_settlement"."channel_real_settlement" IS '分销商实际结算';
COMMENT ON COLUMN "public"."finance_inn_settlement"."is_match" IS '账实是否相符';
COMMENT ON COLUMN "public"."finance_inn_settlement"."is_special" IS '是否含有特殊订单';
COMMENT ON COLUMN "public"."finance_inn_settlement"."after_payment_amount" IS '经特殊订单处理后客栈结算金额';
COMMENT ON COLUMN "public"."finance_inn_settlement"."after_arrears_amount" IS '经挂账处理后的客栈结算金额';
COMMENT ON COLUMN "public"."finance_inn_settlement"."is_arrears" IS '客栈挂账状态(0,不存在挂账1,平账结算2,部分平账3,挂账)';
COMMENT ON COLUMN "public"."finance_inn_settlement"."inn_payment" IS '客栈赔付金额';
COMMENT ON COLUMN "public"."finance_inn_settlement"."refund_amount" IS '客栈退款金额';
COMMENT ON COLUMN "public"."finance_inn_settlement"."fq_replenishment" IS '番茄补款金额';
COMMENT ON COLUMN "public"."finance_inn_settlement"."arrears_past" IS '往期挂账';
COMMENT ON COLUMN "public"."finance_inn_settlement"."arrears_remaining" IS '剩余挂账';

--7、修改finance_inn_channel_settlement
ALTER TABLE "public"."finance_inn_channel_settlement"
ADD COLUMN "inn_payment" numeric(12,2)     DEFAULT 0,
ADD COLUMN "refund_amount" numeric(12,2)    DEFAULT 0,
ADD COLUMN "fq_replenishment" numeric(12,2)    DEFAULT 0,
ADD COLUMN "is_special" bool DEFAULT FALSE,
ADD COLUMN "fq_bear_amount" numeric(12,2),
ADD COLUMN "fq_income_amount" numeric(12,2),
ADD COLUMN "fq_refund_commission_amount" numeric(12,2),
ADD COLUMN "cur_fq_refund_contracts_amount" numeric(12,2),
ADD COLUMN "aft_fq_refund_contracts_amount" numeric(12,2),
ADD COLUMN "channel_real_settlement_amount" numeric(12,2),
ADD COLUMN "fq_normal_income" numeric(12,2),
ADD COLUMN "inn_real_settlement" numeric(12,2) DEFAULT 0;
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."inn_real_settlement" IS '客栈实际结算';
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."fq_normal_income" IS '番茄正常订单收入';
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."channel_real_settlement_amount" IS '分销商实际结算金额';
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."fq_refund_commission_amount" IS '番茄退佣金收入';
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."cur_fq_refund_contracts_amount" IS '本期番茄退往来金额';
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."aft_fq_refund_contracts_amount" IS '后期番茄退往来金额';
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."fq_bear_amount" IS '客栈赔付番茄承担金额总和';
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."fq_income_amount" IS '客栈赔付番茄收入金额总和';
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."inn_payment" IS ' 客栈赔付金额';
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."refund_amount" IS '本期客栈退款金额';
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."fq_replenishment" IS '番茄补款金额';
COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."is_special" IS '是否含有特殊订单';

--8、修改finance_channel_settlement
ALTER TABLE "public"."finance_channel_settlement"
add column no_order_debit_amount NUMERIC(12,2)  DEFAULT 0,
add column channel_debit NUMERIC(12,2)    DEFAULT 0,
add column channel_refund NUMERIC(12,2)    DEFAULT 0,
add column fq_real_income NUMERIC(12,2)    DEFAULT 0,
add column channel_real_amount NUMERIC(12,2)    DEFAULT 0;
COMMENT ON COLUMN "public"."finance_channel_settlement"."no_order_debit_amount" IS '无订单赔付金额总和';
COMMENT ON COLUMN "public"."finance_channel_settlement"."channel_debit" IS '分销商扣番茄赔付金额';
COMMENT ON COLUMN "public"."finance_channel_settlement"."channel_refund" IS '分销商扣番茄退款金额';
COMMENT ON COLUMN "public"."finance_channel_settlement"."fq_real_income" IS '番茄实际收入';
COMMENT ON COLUMN "public"."finance_channel_settlement"."channel_real_amount" IS '分销商实际结算金额';

--9、修改finance_inn_settlement_info，添加字段 城市编号 城市名
ALTER TABLE "public"."finance_inn_settlement_info"
ADD COLUMN "city_code" varchar(10),
ADD COLUMN "city_name" varchar(50);
COMMENT ON COLUMN "public"."finance_inn_settlement_info"."city_code" IS '城市编号';
COMMENT ON COLUMN "public"."finance_inn_settlement_info"."city_name" IS '城市名';

--10、修改tomato_proxysale_channel，分销商基础表添加字段 分销商名称
ALTER TABLE "public"."tomato_proxysale_channel"
ADD COLUMN "channel_name" varchar;
COMMENT ON COLUMN "public"."tomato_proxysale_channel"."channel_name" IS '分销商名称';

--11、历史数据迁移
UPDATE finance_parent_order SET produce_time=settlement_time;

--12、修改已结算账期的账单结算状态为已结算


--1、增加房态切换权限
INSERT INTO "public"."tomato_sys_authority" (
	"id",
	"sys_authority_code",
	"sys_authority_name",
	"status",
	"create_user_code",
	"create_time",
	"update_user_code",
	"update_time",
	"rmk"
)
VALUES
	(
		(
			SELECT
				nextval(
					'tomato_sys_authority_id_seq'
				)
		),
		'status_switch',
		'房态切换',
		'1',
		'root',
		'2015-11-30 00:05:10',
		NULL,
		NULL,
		NULL
	);

--2、给ROOT用户授予房态切换权限
INSERT INTO "public"."tomato_sys_role_authority" (
	"id",
	"sys_role_id",
	"sys_authority_id"
)
VALUES
	(
		(
			SELECT
				nextval(
					'tomato_sys_role_authority_id_seq'
				)
		),
		1,
		(
			SELECT
				ID
			FROM
				tomato_sys_authority
			WHERE
				sys_authority_name = '房态切换'
		)
	);
--20160426升级DDL结束

--20160510升级DDL开始
DROP TABLE IF EXISTS "public"."cancel_order_log";
CREATE TABLE "public"."cancel_order_log" (
 "id" SERIAL NOT NULL,
"channel_order_no" varchar(32),
"operate_user" varchar(32),
"remark" text,
"operate_time" varchar(32),
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON COLUMN "public"."cancel_order_log"."channel_order_no" IS '订单分销商ID';

COMMENT ON COLUMN "public"."cancel_order_log"."operate_user" IS '操作人';

COMMENT ON COLUMN "public"."cancel_order_log"."remark" IS '备注';

COMMENT ON COLUMN "public"."cancel_order_log"."operate_time" IS '操作时间';


--1、增加房态切换权限
INSERT INTO "public"."tomato_sys_authority" (
	"id",
	"sys_authority_code",
	"sys_authority_name",
	"status",
	"create_user_code",
	"create_time",
	"update_user_code",
	"update_time",
	"rmk"
)
VALUES
	(
		(
			SELECT
				nextval(
					'tomato_sys_authority_id_seq'
				)
		),
		'cancel_order',
		'取消订单',
		'1',
		'root',
		'2016-5-9 00:05:10',
		NULL,
		NULL,
		NULL
	);

	--2、给ROOT用户授予房态切换权限
INSERT INTO "public"."tomato_sys_role_authority" (
	"id",
	"sys_role_id",
	"sys_authority_id"
)
VALUES
	(
		(
			SELECT
				nextval(
					'tomato_sys_role_authority_id_seq'
				)
		),
		1,
		(
			SELECT
				ID
			FROM
				tomato_sys_authority
			WHERE
				sys_authority_name = '取消订单'
		)
	);
	--1、修改父账单表，添加渠道代码字段
ALTER TABLE "public"."finance_parent_order"
ADD COLUMN "channel_code" varchar;
COMMENT ON COLUMN "public"."finance_parent_order"."channel_code" IS '渠道代码';
	--20160510升级DDL结束

--20160603升级ddl结束
INSERT INTO "public"."tomato_sys_authority" (
	"id",
	"sys_authority_code",
	"sys_authority_name",
	"status",
	"create_user_code",
	"create_time",
	"update_user_code",
	"update_time",
	"rmk"
)
VALUES
	(
		(
			SELECT
				nextval(
					'tomato_sys_authority_id_seq'
				)
		),
		'status_switch',
		'下架房型',
		'1',
		'root',
		'2016-5-10 00:05:10',
		NULL,
		NULL,
		NULL
	);

--2、给ROOT用户授予房态切换权限
INSERT INTO "public"."tomato_sys_role_authority" (
	"id",
	"sys_role_id",
	"sys_authority_id"
)
VALUES
	(
		(
			SELECT
				nextval(
					'tomato_sys_role_authority_id_seq'
				)
		),
		1,
		(
			SELECT
				ID
			FROM
				tomato_sys_authority
			WHERE
				sys_authority_name = '下架房型'
		)
	);


--3、增加运营活动权限
INSERT INTO "public"."tomato_sys_authority" (
	"id",
	"sys_authority_code",
	"sys_authority_name",
	"status",
	"create_user_code",
	"create_time",
	"update_user_code",
	"update_time",
	"rmk"
)
VALUES
	(
		(SELECT nextval('tomato_sys_authority_id_seq')),
		'report_statistics',
		'运营活动',
		'1',
		'root',
		'2016-5-17 00:04:10',
		NULL,
		NULL,
		NULL
	);

--4、给ROOT用户授予运营活动权限
INSERT INTO "public"."tomato_sys_role_authority" (
"id",
"sys_role_id",
"sys_authority_id")
VALUES
	(
		(SELECT nextval('tomato_sys_role_authority_id_seq')),
		1,
		(SELECT id from tomato_sys_authority WHERE sys_authority_name='运营活动')
	);

	--新增活动表
DROP TABLE IF EXISTS "public"."tomato_operation_activity";
CREATE TABLE "public"."tomato_operation_activity" (
"id" SERIAL NOT NULL,
"activity_name" varchar(32) COLLATE "default",
"cover_picture" varchar(256) COLLATE "default",
"date_line" varchar(32) COLLATE "default",
"operate_user" varchar(32),
"operate_time" timestamp,
"start_time" varchar(32) COLLATE "default",
"end_time" varchar(32) COLLATE "default",
"content" varchar(200) COLLATE "default",
"require" text COLLATE "default",
 "status" varchar(32) DEFAULT 1,
 "recommend" bool DEFAULT false,
 "publish_time" timestamp
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."tomato_operation_activity"."activity_name" IS '活动名称';
COMMENT ON COLUMN "public"."tomato_operation_activity"."cover_picture" IS '封面图';
COMMENT ON COLUMN "public"."tomato_operation_activity"."date_line" IS '截止日期';
COMMENT ON COLUMN "public"."tomato_operation_activity"."start_time" IS '活动开始日期';
COMMENT ON COLUMN "public"."tomato_operation_activity"."end_time" IS '活动截止日期';
COMMENT ON COLUMN "public"."tomato_operation_activity"."content" IS '内容';
COMMENT ON COLUMN "public"."tomato_operation_activity"."require" IS '要求';
COMMENT ON COLUMN "public"."tomato_operation_activity"."operate_user" IS '最后操作人';
COMMENT ON COLUMN "public"."tomato_operation_activity"."operate_time" IS '最后操作时间';
COMMENT ON COLUMN "public"."tomato_operation_activity"."publish_time" IS '发布时间';
COMMENT ON COLUMN "public"."tomato_operation_activity"."recommend" IS '是否推荐';

COMMENT ON COLUMN "public"."tomato_operation_activity"."status" IS '结束状态(1:未结束，0:结束)';

ALTER TABLE "public"."tomato_operation_activity" ADD PRIMARY KEY ("id");

--客栈活动关联表
DROP TABLE IF EXISTS "public"."activity_inn";
CREATE TABLE "public"."activity_inn" (
"inn_id" int4 NOT NULL,
"activity_id" int4 NOT NULL,
"recommend" bool DEFAULT false,
"status" varchar(16) COLLATE "default"
)
WITH (OIDS=FALSE) ;
COMMENT ON COLUMN "public"."activity_inn"."inn_id" IS 'pms客栈id';
COMMENT ON COLUMN "public"."activity_inn"."activity_id" IS '运营活动id';
COMMENT ON COLUMN "public"."activity_inn"."status" IS '活动状态(1:审核中，2：通过，3：拒绝)';
COMMENT ON COLUMN "public"."activity_inn"."recommend" IS '是否推荐';

--增加批量调价权限
INSERT INTO "public"."tomato_sys_authority" (
	"id",
	"sys_authority_code",
	"sys_authority_name",
	"status",
	"create_user_code",
	"create_time",
	"update_user_code",
	"update_time",
	"rmk"
)
VALUES
	(
		(SELECT nextval('tomato_sys_authority_id_seq')),
		'report_statistics',
		'批量调价',
		'1',
		'root',
		'2016-05-30 00:20:10',
		NULL,
		NULL,
		NULL
	);

--给ROOT用户授予批量调价权限
INSERT INTO "public"."tomato_sys_role_authority" (
"id",
"sys_role_id",
"sys_authority_id")
VALUES
	(
		(SELECT nextval('tomato_sys_role_authority_id_seq')),
		1,
		(SELECT id from tomato_sys_authority WHERE sys_authority_name='批量调价')
	);
--20160603升级ddl结束

--20160613升级ddl开始
ALTER TABLE tomato_sys_role ADD path varchar(40);
ALTER TABLE tomato_sys_role ADD level int2;

update tomato_sys_role set path='/',level=0 where id=1;

update tomato_sys_role set path=b.path||b.id||'/',level=b.child_levle
from (select *,level+1 as child_levle from tomato_sys_role where id=1)as b
where tomato_sys_role.parent_id=1;

update tomato_sys_role set path=b.path||b.id||'/',level=b.child_levle
from (select *,level+1 as child_levle from tomato_sys_role where id=2)as b
where tomato_sys_role.parent_id=2;

update tomato_sys_role set path=b.path||b.id||'/',level=b.child_levle
from (select *,level+1 as child_levle from tomato_sys_role where id=106)as b
where tomato_sys_role.parent_id=106;

--修改角色表status字段类型，并更新数据
alter table tomato_sys_role alter column status type varchar(20);
ALTER TABLE tomato_sys_role ALTER COLUMN status SET DEFAULT 'ENABLED';
update tomato_sys_role set status='DISABLE' where status='0';
update tomato_sys_role set status='ENABLED' where status='1';

--修改用户表status字段类型，并更新数据
alter table tomato_sys_user alter column status type varchar(20);
ALTER TABLE tomato_sys_user ALTER COLUMN status SET DEFAULT 'ENABLED';
update tomato_sys_user set status='DISABLE' where status='0';
update tomato_sys_user set status='ENABLED' where status='1';

--修权限表status字段类型，并更新数据
alter table tomato_sys_authority alter column status type varchar(20);
ALTER TABLE tomato_sys_authority ALTER COLUMN status SET DEFAULT 'ENABLED';
update tomato_sys_authority set status='DISABLE' where status='0';
update tomato_sys_authority set status='ENABLED' where status='1';
--为角色名添加唯一索引
ALTER TABLE tomato_sys_role ADD UNIQUE (sys_role_name );


--重建菜单和权限关系
delete from tomato_sys_menu;
delete from tomato_sys_menu_authority;
delete from tomato_sys_authority where sys_authority_name in ('打印统计','小站统计','客栈统计');

insert into tomato_sys_menu(sys_menu_name,parent_id) values('权限管理',0);
insert into tomato_sys_menu(sys_menu_name,parent_id) values('结算信息',0);
insert into tomato_sys_menu(sys_menu_name,parent_id) values('房态切换',0);
insert into tomato_sys_menu(sys_menu_name,parent_id) values('统计管理',0);
insert into tomato_sys_menu(sys_menu_name,parent_id) values('小站对账',0);
insert into tomato_sys_menu(sys_menu_name,parent_id) values('地区后台',0);
insert into tomato_sys_menu(sys_menu_name,parent_id) values('代销平台',0);
insert into tomato_sys_menu(sys_menu_name,parent_id) values('代销订单',0);
insert into tomato_sys_menu(sys_menu_name,parent_id) values('代销数据',0);
insert into tomato_sys_menu(sys_menu_name,parent_id) values('财务结算',0);
insert into tomato_sys_menu(sys_menu_name,parent_id) values('直连订单',0);
insert into tomato_sys_menu(sys_menu_name,parent_id) values('运营活动',0);


insert into tomato_sys_authority(sys_authority_code,sys_authority_name,status,create_user_code,create_time)values('authority_manager','权限管理','ENABLED','root',now());
insert into tomato_sys_authority (sys_authority_code, sys_authority_name, status, create_user_code, create_time)VALUES ('proxy_sale_data', '代销数据', 'ENABLED', 'root', now());
insert into tomato_sys_authority(sys_authority_code,sys_authority_name,status,create_user_code,create_time)values('api_sale_platform_inn_batch_cancel_root','客栈管理-批量关房','ENABLED','root',now());
insert into tomato_sys_authority(sys_authority_code,sys_authority_name,status,create_user_code,create_time)values('api_sale_platform_inn_batch_up','客栈管理-批量下线分销商','ENABLED','root',now());
insert into tomato_sys_authority(sys_authority_code,sys_authority_name,status,create_user_code,create_time)values('api_sale_platform_inn_batch_down','客栈管理-批量上线分销商','ENABLED','root',now());


insert into tomato_sys_role_authority (sys_role_id,sys_authority_id)
select tomato_sys_role.id,tomato_sys_authority.id from tomato_sys_role,tomato_sys_authority where sys_role_name = '番茄来了科技有限公司' and sys_authority_name='权限管理' ;
insert into tomato_sys_role_authority (sys_role_id,sys_authority_id)
select tomato_sys_role.id,tomato_sys_authority.id from tomato_sys_role,tomato_sys_authority where sys_role_name = '番茄来了科技有限公司' and sys_authority_name='代销数据' ;
insert into tomato_sys_role_authority (sys_role_id,sys_authority_id)
select tomato_sys_role.id,tomato_sys_authority.id from tomato_sys_role,tomato_sys_authority where sys_role_name = '番茄来了科技有限公司' and sys_authority_name='客栈管理-批量关房' ;
insert into tomato_sys_role_authority (sys_role_id,sys_authority_id)
select tomato_sys_role.id,tomato_sys_authority.id from tomato_sys_role,tomato_sys_authority where sys_role_name = '番茄来了科技有限公司' and sys_authority_name='客栈管理-批量下线分销商' ;
insert into tomato_sys_role_authority (sys_role_id,sys_authority_id)
select tomato_sys_role.id,tomato_sys_authority.id from tomato_sys_role,tomato_sys_authority where sys_role_name = '番茄来了科技有限公司' and sys_authority_name='客栈管理-批量上线分销商' ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '权限管理' and sys_authority_name='权限管理' ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '结算信息' and sys_authority_name='结算信息' ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '房态切换' and sys_authority_name='房态切换' ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '统计管理' and sys_authority_name in('报表统计','入住统计','打印统计','小站统计','客栈统计') ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '小站对账' and sys_authority_name='小站对账' ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '地区后台' and sys_authority_name='地区后台' ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '代销平台' and sys_authority_name in
('代销管理','代销平台','代销平台-渠道管理','代销平台-客栈管理','代销平台-代销管理','代销平台-已移除客栈','渠道管理-编辑','客栈管理-批量关房','客栈管理-批量下线分销商','客栈管理-批量上线分销商'
,'客栈管理-上/下架精品代销','客栈管理-上/下架普通代销','客栈管理-设置渠道','客栈管理-抽佣比例','客栈管理-关房','客栈管理-移除','代销平台-代销审核','代销审核-房价审核','代销审核-合同审核'
,'房价审核-合作模式','房价审核-通过/否决','合同审核-通过/否决','代销平台-调价管理','下架房型','代销操作记录') ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '代销订单' and sys_authority_name in('代销订单','取消订单') ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '代销数据' and sys_authority_name='代销数据' ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '财务结算' and sys_authority_name in
('财务操作记录','账单核对','结算账期','财务结算','进账核算','出账核算',,'账单核对-上传核单','进账核算-未收到','出账核算-发送账单'
,'出账核算一键结算','出账核算-未结算','出账核算-打标签') ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '财务结算' and sys_authority_name in
('生成进出账单') ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '直连订单' and sys_authority_name='直连订单' ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '运营活动' and sys_authority_name='运营活动' ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '代销平台' and sys_authority_name='批量调价' ;
--20160613升级ddl结束