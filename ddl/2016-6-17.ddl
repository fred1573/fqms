--渠道结算表添加补款细分字段
ALTER TABLE "public"."finance_channel_settlement"
ADD COLUMN "current_refund_amount" numeric(12,2) DEFAULT 0,
ADD COLUMN "refunded_amount" numeric(12,2) DEFAULT 0,
ADD COLUMN "next_refund_amount" numeric(12,2) DEFAULT 0,
ADD COLUMN "fq_temp" numeric(12,2) DEFAULT 0;
COMMENT ON COLUMN "public"."finance_channel_settlement"."current_refund_amount" IS '跨期退款,上期未结算，本期平账';

COMMENT ON COLUMN "public"."finance_channel_settlement"."refunded_amount" IS '跨期退款,已结算退款';

COMMENT ON COLUMN "public"."finance_channel_settlement"."next_refund_amount" IS '跨期退款,本期不结算，下期平账';
COMMENT ON COLUMN "public"."finance_channel_settlement"."fq_temp" IS '番茄暂收';

--订单表新增番茄暂收
ALTER TABLE "public"."finance_parent_order"
ADD COLUMN "fq_temp" numeric(12,2) DEFAULT 0,
ADD COLUMN "modify_reason" varchar;

COMMENT ON COLUMN "public"."finance_parent_order"."modify_reason" IS '修改订单原因';
COMMENT ON COLUMN "public"."finance_parent_order"."fq_temp" IS '番茄暂收';



--客栈渠道结算表新增番茄暂收
ALTER TABLE "public"."finance_inn_channel_settlement"
DROP COLUMN "fq_temp",
ADD COLUMN "fq_temp" numeric(12,2) DEFAULT 0;

COMMENT ON COLUMN "public"."finance_inn_channel_settlement"."fq_temp" IS '番茄暂收金额';



