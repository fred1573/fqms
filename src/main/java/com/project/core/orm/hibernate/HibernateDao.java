package com.project.core.orm.hibernate;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.project.core.orm.Page;
import com.project.core.orm.PropertyFilter;
import com.project.core.orm.PropertyFilter.MatchType;
import com.project.core.utils.reflection.ReflectionUtil;
import com.project.utils.ResourceBundleUtil;
import com.project.utils.time.DateUtil;

/**
 * Hibernat DAO泛型基类.
 * 
 * 扩展功能包括分页查询,按属性过滤条件列表查询.
 * 可在Service层直接使用,也可以扩展泛型DAO子类使用,见两个构造函数的注释.
 * 
 * @param <T> DAO操作的对象类型
 * @param <PK> 主键类型
 * 
 * @author mowei
 */
public class HibernateDao<T, PK extends Serializable> extends SimpleHibernateDao<T, PK> {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 用于Dao层子类的构造函数.
	 * 通过子类的泛型定义取得对象类型Class.
	 * eg.
	 * public class UserDao extends HibernateDao<User, Long>{
	 * }
	 */
	public HibernateDao() {
		super();
	}

	/**
	 * 用于省略Dao层, Service层直接使用通用HibernateDao的构造函数.
	 * 在构造函数中定义对象类型Class.
	 * eg.
	 * HibernateDao<User, Long> userDao = new HibernateDao<User, Long>(sessionFactory, User.class);
	 */
	public HibernateDao(final SessionFactory sessionFactory, final Class<T> entityClass) {
		super(sessionFactory, entityClass);
	}

	//-- 分页查询函数 --//

	/**
	 * 分页获取全部对象.
	 */
	@Transactional(readOnly = true)
	public Page<T> getAll(final Page<T> page) {
		return findPage(page);
	}

	/**
	 * 按属性过滤条件列表分页查找对象.
	 */
	@Transactional(readOnly = true)
	public Page<T> findPage(final Page<T> page, final List<PropertyFilter> filters) {
		Criterion[] criterions = buildCriterionByPropertyFilter(filters);
		return findPage(page, criterions);
	}
	
	/**
	 * 按Criteria分页查询.
	 * 
	 * @param page 分页参数.
	 * @param criterions 数量可变的Criterion.
	 * 
	 * @return 分页查询结果.附带结果列表及所有查询输入参数.
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<T> findPage(final Page<T> page, final Criterion... criterions) {
		Assert.notNull(page, "page不能为空");
		Criteria c = createCriteria(criterions);
		if (page.isAutoCount()) {
			long totalCount = countCriteriaResult(c);
			page.setTotalCount(totalCount);
		}
		setPageParameterToCriteria(c, page);
		List<T> result = c.list();
		page.setResult(result);
		return page;
	}
	
	/**
	 * 执行count查询获得本次Criteria查询所能获得的对象总数.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private long countCriteriaResult(final Criteria c) {
		CriteriaImpl impl = (CriteriaImpl) c;
		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;
		try {
			orderEntries = (List<CriteriaImpl.OrderEntry>) ReflectionUtil.getFieldValue(impl, "orderEntries");
			ReflectionUtil.setFieldValue(impl, "orderEntries", new ArrayList());
		} catch (Exception e) {
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}

		// 执行Count查询
		Long totalCountObject = (Long) c.setProjection(Projections.rowCount()).uniqueResult();
		long totalCount = (totalCountObject != null) ? totalCountObject : 0;

		// 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
		c.setProjection(projection);

		if (projection == null) {
			c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			c.setResultTransformer(transformer);
		}
		try {
			ReflectionUtil.setFieldValue(impl, "orderEntries", orderEntries);
		} catch (Exception e) {
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}

		return totalCount;
	}

	/**
	 * 设置分页参数到Criteria对象,辅助函数.
	 */
	private Criteria setPageParameterToCriteria(final Criteria c, final Page<T> page) {
		Assert.isTrue(page.getPageSize() > 0, "Page Size must larger than zero");
		//hibernate的firstResult的序号从0开始
		c.setFirstResult(page.getFirst() - 1);
		c.setMaxResults(page.getPageSize());
		if (page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');
			Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");
			for (int i = 0; i < orderByArray.length; i++) {
				if (Page.ASC.equals(orderArray[i])) {
					c.addOrder(Order.asc(orderByArray[i]));
				} else {
					c.addOrder(Order.desc(orderByArray[i]));
				}
			}
		}
		return c;
	}

	/**
	 * 按属性查找对象列表,支持多种匹配方式.
	 * 
	 * @param matchType 匹配方式,目前支持的取值见PropertyFilter的MatcheType enum.
	 */
	@Transactional(readOnly = true)
	public List<T> findBy(final String propertyName, final Object value, final MatchType matchType) {
		Criterion criterion = buildCriterion(propertyName, value, matchType);
		return find(criterion);
	}

	/**
	 * 按属性条件参数创建Criterion,辅助函数.
	 */
	@SuppressWarnings("rawtypes")
	private Criterion buildCriterion(final String propertyName, Object propertyValue, final MatchType matchType) {
		Assert.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = null;
		String[] propertyValueS;
	    List propertyValueL;
		//根据MatchType构造criterion
		switch (matchType) {
		case EQ:
			criterion = Restrictions.eq(propertyName, propertyValue);
			break;
		case LIKE:
			criterion = Restrictions.like(propertyName, (String) propertyValue, MatchMode.ANYWHERE);
			break;
		case LE:
			if("org.hibernate.dialect.SQLServer2008Dialect".equalsIgnoreCase(ResourceBundleUtil.getString("hibernate.dialect"))){
				if(propertyValue instanceof Date){
					propertyValue = DateUtil.addDay((Date) propertyValue, 1);
				}
			}
			criterion = Restrictions.le(propertyName, propertyValue);
			break;
		case LT:
			criterion = Restrictions.lt(propertyName, propertyValue);
			break;
		case GE:
			criterion = Restrictions.ge(propertyName, propertyValue);
			break;
		case GT:
			criterion = Restrictions.gt(propertyName, propertyValue);
			break;
		case NE:
			criterion = Restrictions.ne(propertyName, propertyValue);
			break;
		case BE:
			if (propertyValue instanceof String[]) {
				propertyValueS = (String[])propertyValue;
				criterion = Restrictions.between(propertyName, propertyValueS[0], propertyValueS[1]);
			}
			if (propertyValue instanceof List) {
				propertyValueL = (List)propertyValue;
				criterion = Restrictions.between(propertyName, propertyValueL.get(0), propertyValueL.get(1));
			}
			break;
		case IN:
			if (propertyValue instanceof String[]) {
				propertyValueS = (String[])propertyValue;
				criterion = Restrictions.in(propertyName, propertyValueS);
			}
			if (propertyValue instanceof List) {
				propertyValueL = (List)propertyValue;
				criterion = Restrictions.in(propertyName, propertyValueL);
			}
			break;
		case ISNULL:
			criterion = Restrictions.isNull(propertyName);
			break;
		case iSNOTNULL:
			criterion = Restrictions.isNotNull(propertyName);
			break;
		case EQP:
			criterion = Restrictions.eqProperty(propertyName, propertyValue.toString());
			break;
		case NEP:
			criterion = Restrictions.neProperty(propertyName, propertyValue.toString());
			break;
		case LTP:
			criterion = Restrictions.ltProperty(propertyName, propertyValue.toString());
			break;
		case LEP:
			criterion = Restrictions.leProperty(propertyName, propertyValue.toString());
			break;
		case GTP:
			criterion = Restrictions.gtProperty(propertyName, propertyValue.toString());
			break;
		case GEP:
			criterion = Restrictions.geProperty(propertyName, propertyValue.toString());
		}

		return criterion;
	}
	
	/**
	 * 按属性过滤条件PropertyFilter列表查找对象列表.
	 */
	@Transactional(readOnly = true)
	public List<T> find(final List<PropertyFilter> filters) {
		Criterion[] criterions = buildCriterionByPropertyFilter(filters);
		return find(criterions);
	}

	/**
	 * 按属性条件列表创建Criterion数组,辅助函数.
	 */
	private Criterion[] buildCriterionByPropertyFilter(final List<PropertyFilter> filters) {
		List<Criterion> criterionList = new ArrayList<Criterion>();
		for (PropertyFilter filter : filters) {
			if (!filter.hasMultiProperties()) { //只有一个属性需要比较的情况.
				Criterion criterion = buildCriterion(filter.getPropertyName(), filter.getMatchValue(), filter.getMatchType());
				criterionList.add(criterion);
			} else {//包含多个属性需要比较的情况,进行or处理.
				Disjunction disjunction = Restrictions.disjunction();
				for (String param : filter.getPropertyNames()) {
					Criterion criterion = buildCriterion(param, filter.getMatchValue(), filter.getMatchType());
					disjunction.add(criterion);
				}
				criterionList.add(disjunction);
			}
		}
		return criterionList.toArray(new Criterion[criterionList.size()]);
	}
	
	/**
	 * 按HQL分页查询.
	 * 
	 * @param page 分页参数. 注意不支持其中的orderBy参数.
	 * @param hql hql语句.
	 * @param values 数量可变的数组集合查询参数,按顺序绑定.
	 * @return 分页查询结果, 附带结果列表及所有查询输入参数.
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<T> findPage(final Page<T> page, final String hql, final Object... values) {
		Assert.notNull(page, "page不能为空");
		Query q = createQuery(hql, values);
		if (page.isAutoCount()) {
			long totalCount = super.countForLongWithHql(hql, values);
			page.setTotalCount(totalCount);
		}
		setPageParameterToQuery(q, page);
		List<T> result = q.list();
		page.setResult(result);
		return page;
	}
	
	//------------------------------------------以下为sql语句查询以及分页---------------------------------------------//
	/**
	 * 使用指定的检索标准检索数据并分页返回<T>对象集合数据
	 * @param page 分页参数. 注意不支持其中的orderBy参数.
	 * @param sql 注意必须用select * from table的形式或select a.*,b.* from table a,table b
	 * @param values 数量可变的数组集合查询参数,按顺序绑定.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<T> findPageWithSql(final Page<T> page, final String sql, final Object... values) {
		Assert.notNull(page, "page不能为空");
		Query q = createSqlQuery(sql, values);
		if (page.isAutoCount()) {
			long totalCount = super.countForLongWithSql(sql, values);
			page.setTotalCount(totalCount);
		}
		setPageParameterToQuery(q, page);
		List<T> result = q.list();
		page.setResult(result);
		return page;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<T> findPageWithSql(final boolean isPage, final Page<T> page, final String sql, final Object... values) {
		Assert.notNull(page, "page不能为空");
		Query q = createSqlQuery(sql, values);
		if (page.isAutoCount()) {
			long totalCount = super.countForLongWithSql(sql, values);
			page.setTotalCount(totalCount);
		}
		if(isPage){
			setPageParameterToQuery(q, page);
		}
		List<T> result = q.list();
		page.setResult(result);
		return page;
	}
	
	/**
	 * 使用指定的检索标准检索数据并分页返回<T>对象集合数据
	 * @param clazz 对象类型，注意自定义的bean必须使用@Entity和@id注解为一个实体,而且必须被Hibernate-sessionFactory扫描到此实体
	 * @param page 分页参数. 注意不支持其中的orderBy参数.
	 * @param sql语句 注意必须select全部clazz中的属性
	 * @param values 数量可变的数组集合查询参数,按顺序绑定.
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<?> findPageWithSql(final Class<?> clazz, final Page<?> page, final String sql, final Object... values) {
		Assert.notNull(page, "page不能为空");
		Query q = createSqlQuery(clazz, sql, values);
		if (page.isAutoCount()) {
			long totalCount = super.countForLongWithSql(sql, values);
			page.setTotalCount(totalCount);
		}
		setPageParameterToQuery(q, page);
		List<?> result = q.list();
		page.setResult(result);
		return page;
	}
	
	/**
	 * 使用指定的检索标准检索数据并分页返回Map集合数据
	 * @param page 分页参数. 注意不支持其中的orderBy参数.
	 * @param sql
	 * @param values 数量可变的数组集合查询参数,按顺序绑定.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<Map<String, Object>> findListMapPageWithSql(final Page<Map<String, Object>> page, final String sql, final Object... values) {
		Assert.notNull(page, "page不能为空");
		Query q = createSqlQueryToMap(sql, values);
		if (page.isAutoCount()) {
			long totalCount = super.countForLongWithSql(sql, values);
			page.setTotalCount(totalCount);
		}
		setPageParameterToQuery(q, page);
		List<Map<String, Object>> result = q.list();
		page.setResult(result);
		return page;
	}
	
	/**
	 * 设置分页参数到Query对象,辅助函数.
	 */
	private Query setPageParameterToQuery(final Query q, final Page<?> page) {
		Assert.isTrue(page.getPageSize() > 0, "Page Size must larger than zero");
		//hibernate的firstResult的序号从0开始
		q.setFirstResult(page.getFirst() - 1);
		q.setMaxResults(page.getPageSize());
		return q;
	}
	
	public void setTotalCount(String sql, Page<?> page){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT COUNT(*) AS num FROM(");
		sb.append(sql);
		sb.append(") t");
		Map<String,Object> row = this.findMapWithSql(sb.toString());
		if(row != null){
			BigInteger num = (BigInteger) row.get("num");
			page.setTotalCount(num.longValue());
		}else{
			page.setTotalCount(0);
		}
		sb = null;
	}
	
	public void setPageResult(String sql, int pageNo, Page<?> page){
		StringBuilder sb = new StringBuilder();
		int pageSize = page.getPageSize();
		int offset = pageSize * (pageNo - 1);
		sb = new StringBuilder();
		sb.append(sql);
		sb.append(" LIMIT " + page.getPageSize() + " OFFSET " + offset);
		List<Map<String,Object>> rows = this.findListMapWithSql(sb.toString());
		page.setResult(rows2Obj(rows));
		page.setPageNo(pageNo);
		sb = null;
	}

	public List<?> rows2Obj(List<Map<String, Object>> rows) {
		return null;
	}
	
}