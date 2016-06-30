
/*==============================================================*/
/* Table: tomato_proxysale_order_complaint                       */
/*==============================================================*/
create table tomato_proxysale_order_complaint (
   id                   SERIAL not null,
   create_time          TIMESTAMP            null,
   update_time          TIMESTAMP            null,
   finish_user_id       INT4                 null,
   finish_user_name     VARCHAR(40)          null,
   finish_time          TIMESTAMP            null,
   complaint_type       VARCHAR(20)          null,
   complaint_status     VARCHAR(20)          null,
   channel_id           VARCHAR(50)          null,
   channel_name         VARCHAR(100)         null,
   channel_child_id     VARCHAR(50)          null,
   channel_code_name    VARCHAR(100)         null,
   region_name          VARCHAR(200)         null,
   inn_id               INT4          null,
   inn_name             VARCHAR(200)         null,
   customer_manager     VARCHAR(20)          null,
   channel_order_no     VARCHAR(100)         null,
   user_name            VARCHAR(20)          null,
   contact              VARCHAR(50)          null,
   total_amount         NUMERIC              null,
   order_time           TIMESTAMP            null,
   order_no             VARCHAR(100)         not null,
   constraint PK_TOMATO_PROXYSALE_ORDER_COMP primary key (id)
);

comment on table tomato_proxysale_order_complaint is
'代销订单投诉';

comment on column tomato_proxysale_order_complaint.finish_user_id is
'完成人id';

comment on column tomato_proxysale_order_complaint.finish_user_name is
'完成人名';

comment on column tomato_proxysale_order_complaint.finish_time is
'完成时间';

comment on column tomato_proxysale_order_complaint.complaint_type is
'最新投诉类型';

comment on column tomato_proxysale_order_complaint.complaint_status is
'投诉处理状态';

comment on column tomato_proxysale_order_complaint.channel_id is
'渠道订单Id';

comment on column tomato_proxysale_order_complaint.channel_name is
'渠道名称';

comment on column tomato_proxysale_order_complaint.channel_child_id is
'子分销商ID';

comment on column tomato_proxysale_order_complaint.channel_code_name is
'子分销商名称';

comment on column tomato_proxysale_order_complaint.region_name is
'目的地';

comment on column tomato_proxysale_order_complaint.inn_id is
'客栈id';

comment on column tomato_proxysale_order_complaint.inn_name is
'客栈名称';

comment on column tomato_proxysale_order_complaint.customer_manager is
'客户经理';

comment on column tomato_proxysale_order_complaint.channel_order_no is
'分销商订单号';

comment on column tomato_proxysale_order_complaint.user_name is
'用户真实姓名';

comment on column tomato_proxysale_order_complaint.contact is
'联系方式';

comment on column tomato_proxysale_order_complaint.total_amount is
'分销商订单总金额';

comment on column tomato_proxysale_order_complaint.order_time is
'下单时间';

comment on column tomato_proxysale_order_complaint.order_no is
'OMS订单号';


ALTER TABLE tomato_proxysale_order_complaint ADD UNIQUE (order_no );


/*==============================================================*/
/* Table: tomato_order_complaint_process_log                       */
/*==============================================================*/
create table tomato_order_complaint_process_log (
   id                   SERIAL not null,
   create_time          TIMESTAMP            null,
   update_time          TIMESTAMP            null,
   process_user_id      INT4                 null,
   process_user_name    VARCHAR(50)          null,
   complaint_type       VARCHAR(20)          null,
   note                 VARCHAR(200)         null,
   order_complaint_id   INT4                 null,
   constraint PK_TOMATO_ORDER_COMPLAINT_PROC primary key (id)
);

comment on table tomato_order_complaint_process_log is
'订单投诉跟进记录';

comment on column tomato_order_complaint_process_log.process_user_id is
'处理人id';

comment on column tomato_order_complaint_process_log.process_user_name is
'处理人名';

comment on column tomato_order_complaint_process_log.complaint_type is
'投诉类型';

comment on column tomato_order_complaint_process_log.note is
'跟进记录描述';

comment on column tomato_order_complaint_process_log.order_complaint_id is
'代销订单投诉id';


ALTER TABLE tomato_order_complaint_process_log ADD complaint_status VARCHAR(20);


/*==============================================================*/
/* Table: tomato_proxysale_order_complain                       */
/*==============================================================*/
create table tomato_proxy_sale_sub_order (
   id                   SERIAL not null,
   create_time          TIMESTAMP            null,
   update_time          TIMESTAMP            null,
   channel_room_type_name VARCHAR(200)         null,
   room_nums            INT2                 null,
   check_in_at          TIMESTAMP            null,
   check_out_at         TIMESTAMP            null,
   order_complaint_id   INT4                 null,
   constraint PK_TOMATO_PROXY_SALE_SUB_ORDER primary key (id)
);

comment on column tomato_proxy_sale_sub_order.channel_room_type_name is
'房型名';

comment on column tomato_proxy_sale_sub_order.room_nums is
'房间数';

comment on column tomato_proxy_sale_sub_order.check_in_at is
'入住日期';

comment on column tomato_proxy_sale_sub_order.check_out_at is
'离店日期';

comment on column tomato_proxy_sale_sub_order.order_complaint_id is
'代销订单投诉id';


--新增客诉/信用住订单权限
update tomato_sys_menu set sys_menu_name='订单管理' where sys_menu_name='代销订单';

insert into tomato_sys_authority(sys_authority_code,sys_authority_name,status,create_user_code,create_time)values('credit_order','信用住订单','ENABLED','root',now());

insert into tomato_sys_role_authority (sys_role_id,sys_authority_id)
select tomato_sys_role.id,tomato_sys_authority.id from tomato_sys_role,tomato_sys_authority where sys_role_name = '番茄来了科技有限公司' and sys_authority_name='信用住订单' ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '订单管理' and sys_authority_name='信用住订单' ;

insert into tomato_sys_authority(sys_authority_code,sys_authority_name,status,create_user_code,create_time)values('order_complaint','客诉管理','ENABLED','root',now());

insert into tomato_sys_role_authority (sys_role_id,sys_authority_id)
select tomato_sys_role.id,tomato_sys_authority.id from tomato_sys_role,tomato_sys_authority where sys_role_name = '番茄来了科技有限公司' and sys_authority_name='客诉管理' ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '订单管理' and sys_authority_name='客诉管理' ;

insert into tomato_sys_authority(sys_authority_code,sys_authority_name,status,create_user_code,create_time)values('order_manager','订单管理','ENABLED','root',now());

insert into tomato_sys_role_authority (sys_role_id,sys_authority_id)
select tomato_sys_role.id,tomato_sys_authority.id from tomato_sys_role,tomato_sys_authority where sys_role_name = '番茄来了科技有限公司' and sys_authority_name='订单管理' ;

insert into tomato_sys_menu_authority (sys_menu_id,sys_authority_id)
select tomato_sys_menu.id,tomato_sys_authority.id from tomato_sys_authority,tomato_sys_menu where sys_menu_name = '订单管理' and sys_authority_name='订单管理' ;