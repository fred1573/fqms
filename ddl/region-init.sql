/*==============================================================*/
/* Table: tomato_inn_area_region       
 * 地区后台 数据初始化
 *                          */
/*==============================================================*/

	delete from tomato_inn_area_region where region_id = 6;
	Insert  Into tomato_inn_area_region(inn_id,region_id,createtime) 
Select t.id,t.region_id,to_timestamp('2014-03-01 00:00:00','yyyy-mm-dd hh24:mi:ss')
 From tomato_inn t where  t.region_id=6 ;

