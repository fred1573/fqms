package com.project.dao.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.project.exception.ServiceException;
import com.project.utils.ResourceBundleUtil;

/**
 * @author 
 * mowei
 */
@Component
public class JdbcThinDao {
	
	@Autowired
	private DriverManagerDataSource jdbcDataSource;
	
	private static Logger logger = LoggerFactory.getLogger(JdbcThinDao.class);
	
	// 几个数据库变量
	Connection c = null;
	ResultSet rs = null;
	PreparedStatement pstmt = null;

	public Boolean WhetherConnectSuccess(String theUser,String thePw){
		try {
			Class.forName(ResourceBundleUtil.getString("jdbc.driver", "application")).newInstance();
			// 与url指定的数据源建立连接
			c = DriverManager.getConnection(ResourceBundleUtil.getString("jdbc.url", "application"), theUser, thePw);
			if(c!=null && !c.isClosed())
				return true;
			else
				return false;
		} catch (Exception e) {
			logger.error( e.getMessage() );
			return false;
		}
	}
	
	// 执行查询
	public List<Map<String, Object>> executeQuery(final String sql, final Object... values) {
		try {
			c = jdbcDataSource.getConnection();
		} catch (SQLException e) {
			logger.error( e.getMessage() );
			throw new ServiceException( e.getMessage() );
		}
		rs = null;
		try {
			logger.info( "query by sql: "+sql );
			pstmt = c.prepareStatement( sql );
			if ( values != null && values.length > 0) {
				logger.info( "values: " + values.toString() + " " );
				for ( int i = 0; i < values.length; i++ ) {
					pstmt.setObject(i + 1,values[i]);
				}
			}
			rs = pstmt.executeQuery();
			ResultSetMetaData resultSetMetaData = rs.getMetaData();
			int col = resultSetMetaData.getColumnCount();
			List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			while ( rs.next() ) {
				map = new HashMap<String, Object>();
				for ( int j = 0; j < col; j++ ) {
					map.put( ( resultSetMetaData.getColumnName( j + 1 ) ).toLowerCase(), rs.getObject( j + 1 ) );
				}
				tempList.add( map );
			}
			return tempList;
		} catch (SQLException e) {
			logger.error( e.getMessage() );
			throw new ServiceException( e.getMessage() );
		} finally {
			try {
				closeConnection( c, rs, pstmt );
			} catch ( Exception e1 ) {
				logger.error( e1.getMessage() );
				throw new ServiceException( e1.getMessage() );
			}
		}
	}
	
	/**
	 * 关闭连接
	 * 
	 * @param conn
	 * @param rs
	 * @param pstmt
	 * @throws SQLException
	 */
	public void closeConnection(Connection c, ResultSet rs, PreparedStatement pstmt) throws SQLException {
		if ( rs != null ) {
			rs.close();
		}
		if ( pstmt != null ) {
			pstmt.close();
		}
		if ( c != null ) {
			c.close();
		}
	}
}
