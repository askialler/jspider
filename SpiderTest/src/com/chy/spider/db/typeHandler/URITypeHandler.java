package com.chy.spider.db.typeHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class URITypeHandler extends BaseTypeHandler<URI> {

	@Override
	public URI getNullableResult(ResultSet arg0, String arg1) throws SQLException {
		URI uri=null;
		try {
			uri= new URI(arg0.getString(arg1));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}

	@Override
	public URI getNullableResult(ResultSet arg0, int arg1) throws SQLException {
		URI uri=null;
		try {
			uri= new URI(arg0.getString(arg1));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}

	@Override
	public URI getNullableResult(CallableStatement arg0, int arg1) throws SQLException {

		URI uri=null;
		try {
			uri= new URI(arg0.getString(arg1));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;

	}

	@Override
	public void setNonNullParameter(PreparedStatement arg0, int arg1, URI arg2, JdbcType arg3) throws SQLException {
		arg0.setString(arg1, arg2.toString());
	}

}
