/**
 * Copyright (c) 2005-2010 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: SimpleHibernateDao.java 1205 2010-09-09 15:12:17Z calvinxiu $
 */
package com.project.core.orm.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.project.core.utils.reflection.ReflectionUtil;
import com.project.utils.CollectionsUtil;

/**
 * 封装Hibernate原生API的DAO泛型基类.
 * 
 * 可在Service层直接使用, 也可以扩展泛型DAO子类使用, 见两个构造函数的注释.
 * 取消了HibernateTemplate, 直接使用Hibernate原生API.
 * 
 * @param <T> DAO操作的对象类型
 * @param <PK> 主键类型
 * 
 * @author mowei
 */
@SuppressWarnings("unchecked")
public class SimpleHibernateDao<T, PK extends Serializable> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	protected SessionFactory sessionFactory;

	protected Class<T> entityClass;
	
	/**
	 * 用于Dao层子类使用的构造函数.
	 * 通过子类的泛型定义取得对象类型Class.
	 * eg.
	 * public class UserDao extends SimpleHibernateDao<User, Long>
	 */
	public SimpleHibernateDao() {
		this.entityClass = ReflectionUtil.getClassGenricType(getClass());
	}

	/**
	 * 用于用于省略Dao层, 在Service层直接使用通用SimpleHibernateDao的构造函数.
	 * 在构造函数中定义对象类型Class.
	 * eg.
	 * SimpleHibernateDao<User, Long> userDao = new SimpleHibernateDao<User, Long>(sessionFactory, User.class);
	 */
	public SimpleHibernateDao(final SessionFactory sessionFactory, final Class<T> entityClass) {
		this.sessionFactory = sessionFactory;
		this.entityClass = entityClass;
	}

	/**
	 * 取得sessionFactory.
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * 采用@Autowired按类型注入SessionFactory, 当有多个SesionFactory的时候在子类重载本函数.
	 */
	@Autowired
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * 取得当前Session.
	 */
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	/**
	 * 保存新增或修改的对象.
	 */
	public void save(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		getSession().saveOrUpdate(entity);
		logger.debug("save entity: {}", entity);
	}

	/**
	 * 删除对象.
	 * 
	 * @param entity 对象必须是session中的对象或含id属性的transient对象.
	 */
	public void delete(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		getSession().delete(entity);
		logger.debug("delete entity: {}", entity);
	}

	/**
	 * 按id删除对象.
	 */
	public void delete(final PK id) {
		Assert.notNull(id, "id不能为空");
		delete(get(id));
		logger.debug("delete entity {},id is {}", entityClass.getSimpleName(), id);
	}

	/**
	 * 按id获取对象.
	 */
	@Transactional(readOnly = true)
	public T get(final PK id) {
		Assert.notNull(id, "id不能为空");
		return (T) getSession().load(entityClass, id);
	}

	/**
	 * 按id列表获取对象列表.
	 */
	@Transactional(readOnly = true)
	public List<T> get(final Collection<PK> ids) {
		return find(Restrictions.in(getIdName(), ids));
	}

	/**
	 *	获取全部对象.
	 */
	@Transactional(readOnly = true)
	public List<T> getAll() {
		return find();
	}

	/**
	 *	获取全部对象, 支持按属性行序.
	 */
	@Transactional(readOnly = true)
	public List<T> getAll(String orderByProperty, boolean isAsc) {
		Criteria c = createCriteria();
		if (isAsc) {
			c.addOrder(Order.asc(orderByProperty));
		} else {
			c.addOrder(Order.desc(orderByProperty));
		}
		return c.list();
	}

	/**
	 * 按属性查找对象列表, 匹配方式为相等.
	 */
	@Transactional(readOnly = true)
	public List<T> findBy(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return find(criterion);
	}
	
	/**
	 * 按多个属性查找对象列表, 匹配方式为相等.
	 */
	@Transactional(readOnly = true)
	public List<T> findBy(final String[] propertyNames, final Object[] values) {
		Assert.hasText(propertyNames.toString(), "propertyNames不能为空");
		return createCriteria(propertyNames,values).list();
	}

	/**
	 * 按属性查找唯一对象, 匹配方式为相等.
	 */
	@Transactional(readOnly = true)
	public T findUniqueBy(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return (T) createCriteria(criterion).uniqueResult();
	}
	
	/**
	 * 按多个属性查找唯一对象, 匹配方式为相等.
	 */
	@Transactional(readOnly = true)
	public T findUniqueBy(final String[] propertyNames, final Object[] values) {
		Assert.hasText(propertyNames.toString(), "propertyNames不能为空");
		return (T) createCriteria(propertyNames,values).uniqueResult();
	}

	/**
	 * 按HQL查询对象列表.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	@Transactional(readOnly = true)
	public <X> List<X> find(final String hql, final Object... values) {
		return createQuery(hql, values).list();
	}

	/**
	 * 按Criteria查询对象列表.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	@Transactional(readOnly = true)
	public List<T> find(final Criterion... criterions) {
		return createCriteria(criterions).list();
	}

	/**
	 * 按HQL查询唯一对象.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	@Transactional(readOnly = true)
	public <X> X findUnique(final String hql, final Object... values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	/**
	 * 按Criteria查询唯一对象.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	@Transactional(readOnly = true)
	public T findUnique(final Criterion... criterions) {
		return (T) createCriteria(criterions).uniqueResult();
	}
	
	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 */
	@Transactional(readOnly = true)
	public long countForLongWithHql(final String hql, final Object... values) {
		Assert.hasText(hql, "hql语句不能为空!");
		String countHql = this.prepareCountSHql(hql);
		try {
			return findUnique(countHql, values);
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
		}
	}
	
	/**
	 * 组装计数sql以及hql，语句必须小写
	 * @param shql
	 * @return
	 */
	private String prepareCountSHql(String shql) {
//		//select子句与order by子句会影响count查询,进行简单的排除.
//		String fromHql = "from " + StringUtils.substringAfter(shql, "from ");
//		fromHql = StringUtils.substringBefore(fromHql, " order by ");
//		//如果有group by 
//		if(-1 != fromHql.indexOf("group by") && !fromHql.endsWith(")")){
//			return "select count(*) from ( select count(*) " + fromHql + ") t";
//		};
//		return "select count(*) " + fromHql;
		return "select count(*) from ( " + shql + " ) t";
	}
	
	/**
	 * 执行HQL进行批量修改/删除操作.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 * @return 更新记录数.
	 */
	public int batchExecute(final String hql, final Object... values) {
		Assert.hasText(hql, "hql语句不能为空!");
		return createQuery(hql, values).executeUpdate();
	}

	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * 
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
	 */
	@Transactional(readOnly = true)
	public boolean isPropertyUnique(final String propertyName, final Object newValue, final Object oldValue) {
		Assert.hasText(propertyName, "propertyName不能为空");
		if (newValue == null || newValue.equals(oldValue)) {
			return true;
		}
		Object object = findUniqueBy(propertyName, newValue);
		return (object == null);
	}

	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 * 与find()函数可进行更加灵活的操作.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	@Transactional(readOnly = true)
	public Query createQuery(final String hql, final Object... values) {
		Assert.hasText(hql, "hql语句不能为空!");
		Query query = getSession().createQuery(hql);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}
	
	/**
	 * 根据Criterion条件创建Criteria.
	 * 与find()函数可进行更加灵活的操作.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	@Transactional(readOnly = true)
	public Criteria createCriteria(final Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}
	
	/**
	 * 根据propertyNames以及values创建Criteria.
	 * 与find()函数可进行更加灵活的操作.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	@Transactional(readOnly = true)
	public Criteria createCriteria(final String[] propertyNames, final Object[] values) {
		Criteria criterias = getSession().createCriteria(entityClass);
		if(propertyNames.length != values.length)
			Assert.hasText(propertyNames.length+"|"+values.length, "propertyNames和values参数个数不同");
		for(int i=0;i<propertyNames.length;i++){
			criterias.add(Restrictions.eq(propertyNames[i], values[i]));
		}
		return criterias;
	}

	/**
	 * 初始化对象.
	 * 使用load()方法得到的仅是对象Proxy, 在传到View层前需要进行初始化.
	 * 如果传入entity, 则只初始化entity的直接属性,但不会初始化延迟加载的关联集合和属性.
	 * 如需初始化关联属性,需执行:
	 * Hibernate.initialize(user.getRoles())，初始化User的直接属性和关联集合.
	 * Hibernate.initialize(user.getDescription())，初始化User的直接属性和延迟加载的Description属性.
	 */
	public void initProxyObject(Object proxy) {
		Hibernate.initialize(proxy);
	}

	/**
	 * Flush当前Session.
	 */
	public void flush() {
		getSession().flush();
	}

	/**
	 * 为Query添加distinct transformer.
	 * 预加载关联对象的HQL会引起主对象重复, 需要进行distinct处理.
	 */
	protected Query distinct(Query query) {
		query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return query;
	}

	/**
	 * 为Criteria添加distinct transformer.
	 * 预加载关联对象的HQL会引起主对象重复, 需要进行distinct处理.
	 */
	protected Criteria distinct(Criteria criteria) {
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria;
	}

	/**
	 * 取得对象的主键名.
	 */
	public String getIdName() {
		ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
		return meta.getIdentifierPropertyName();
	}
	
	//------------------------------------------以下为sql语句调用方法---------------------------------------------//
	/**
	 * 根据sql语句，返回对象集合
	 * @param sql语句 注意必须用select * from table的形式或select a.*,b.* from table a,table b
	 * @param values 数组参数值
	 */
	@Transactional(readOnly = true)
	public List<T> findWithSql(final String sql, final Object... values) {
		Assert.hasText(sql, "sql语句不能为空!");
		return createSqlQuery(sql,values).list();
	}
	
	/**
	 * 根据sql语句，返回对象集合
	 * @param sql语句 注意必须select全部clazz中的属性
	 * @param clazz 对象类型，注意自定义的bean必须使用@Entity和@id注解为一个实体,而且必须被Hibernate-sessionFactory扫描到此实体
	 * @param values 数组参数值
	 */
	@Transactional(readOnly = true)
	public List<?> findWithSql(final Class<?> clazz, final String sql, final Object... values){
		Assert.notNull(clazz,"对象类型不能为空!");
		Assert.hasText(sql, "sql语句不能为空!");
		return createSqlQuery(clazz,sql,values).list();
	}
	
	/**
	 * 根据sql语句，返回对象集合
	 * @param sql语句 注意必须select全部clazz中的属性
	 * @param clazz 对象类型，注意自定义的bean必须使用@Entity和@id注解为一个实体,而且必须被Hibernate-sessionFactory扫描到此实体
	 * @param values 数组参数值
	 */
	@Transactional(readOnly = false)
	public List<?> findWithNoIdSql(final Class<?> clazz, String sql, final Object... values){
		sql = "select nextval('seq_tomato_no_id') as id,t.* from ( "+sql;
		sql += " ) t";
		Assert.notNull(clazz,"对象类型不能为空!");
		Assert.hasText(sql, "sql语句不能为空!");
		return createSqlQuery(clazz,sql,values).list();
	}
	
	/**
	 * 根据sql语句，返回对象
	 * @param sql语句 注意必须用select * from table的形式或select a.*,b.* from table a,table b
	 * @param values 数组参数值
	 */
	@Transactional(readOnly = true)
	public T findUniqueWithSql(final String sql, final Object... values){
		Assert.hasText(sql, "sql语句不能为空!");
		return (T) createSqlQuery(sql,values).uniqueResult();
	}
	
	/**
	 * 根据sql语句，返回clazz参数对应的对象
	 * @param sql语句 注意必须select全部clazz中的属性
	 * @param clazz 对象类型，注意自定义的bean必须使用@Entity和@id注解为一个实体,而且必须被Hibernate-sessionFactory扫描到此实体
	 * @param values 数组参数值
	 */
	@Transactional(readOnly = true)
	public Object findUniqueWithSql(final Class<?> clazz, final String sql, final Object... values){
		Assert.notNull(clazz,"对象类型不能为空!");
		Assert.hasText(sql,"sql语句不正确!");
		return createSqlQuery(clazz,sql,values).uniqueResult();
	}
	
	/**
	 * 根据sql语句,返回Map对象集合,Key为字段名,value为值
	 * @param sql语句
	 * @param values 数组参数值
	 */
	@Transactional(readOnly = true)
	public List<Map<String,Object>> findListMapWithSql(final String sql,final Object... values){
		Assert.hasText(sql,"sql语句不正确!");
		return createSqlQueryToMap(sql,values).list();
	}
	
	/**
	 * 根据sql语句,返回Map对象,对于某些项目来说，没有准备Bean对象，则可以使用Map代替，Key为字段名,value为值
	 * @param sql语句
	 * @param values 数组参数值
	 */
	@Transactional(readOnly = true)
	public Map<String,Object> findMapWithSql(final String sql,final Object... values){
		Assert.hasText(sql,"sql语句不正确!");
		return (Map<String, Object>) createSqlQueryToMap(sql,values).uniqueResult();
	}
	
	/**
	 * 根据查询sql与参数列表创建Query对象.(默认为所查询的实体类)
	 * @param sql
	 * @param values
	 * @return
	 */
	@Transactional(readOnly = true)
	public Query createSqlQuery(final String sql, final Object... values) {
		Query query = getSession().createSQLQuery(sql).addEntity(entityClass);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}
	
	/**
	 * 根据查询sql与参数列表创建Query对象.（clazz传参形式）
	 * @param clazz，返回clazz参数对应的对象
	 * @param sql
	 * @param values 数量可变的参数,按顺序绑定.
	 * @return
	 */
	@Transactional(readOnly = true)
	public Query createSqlQuery(final Class<?> clazz, final String sql, final Object... values) {
		Query query = getSession().createSQLQuery(sql).addEntity(clazz);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}
	
	/**
	 * 根据查询sql与参数列表创建Query对象.（map形式）
	 * @param sql
	 * @param values 数量可变的参数,按顺序绑定.
	 * @return
	 */
	@Transactional(readOnly = true)
	public Query createSqlQueryToMap(final String sql, final Object... values) {
		Query query = getSession().createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}
	
	/**
	 * 根据sql语句，返回数值型返回结果
	 * @param sql语句
	 * @param values 数组参数值
	 */
	@Transactional(readOnly = true)
	public long countForLongWithSql(final String sql, final Object... values){
		Assert.hasText(sql,"sql语句不能为空!");
		String countSql = this.prepareCountSHql(sql);
		try {
			List<Map<String, Object>> result = createSqlQueryToMap(countSql, values).list();
			if(CollectionsUtil.isEmpty(result))
				return 0;
			else{
				Object count = result.get(0).get("count");
				if(count!=null && !"".equals(count.toString()))
					return Integer.parseInt(count.toString());
				else
					return 0;
			}
		} catch (Exception e) {
			throw new RuntimeException("sql can't be auto count, sql is:" + countSql, e);
		}
	}
	
	/**
	 * 根据sql语句，执行insert，update，delete等操作
	 * @param sql
	 * @param values 数组参数值
	 */
	public int executeUpdateWithSql(final String sql, final Object... values){
		Assert.hasText(sql,"sql语句不正确!");
		return createSqlQueryToMap(sql,values).executeUpdate();
	}
	
}