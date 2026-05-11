package org.isfce.pdb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

import org.isfce.pdb.model.Svg;

public class SQLSvgDao implements ISvgDao {
	
	private static final Logger logger = Logger.getLogger(SQLSvgDao.class.getName());
	private Connection connection;
	
	public SQLSvgDao(DAOFactory factory) {
		this.connection = factory.getConnection();
	}

	@Override
	public Optional<Svg> getFromId(String id) {
		String sql = "SELECT CODE_SVG, SVG_SVG, SVGX_SVG, SVGY_SVG, "
				   + "SVGWIDTH_SVG, SVGHEIGHT_SVG FROM TSVG WHERE CODE_SVG = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(new Svg(
						rs.getString("CODE_SVG"),
						rs.getString("SVG_SVG"),
						rs.getDouble("SVGX_SVG"),
						rs.getDouble("SVGY_SVG"),
						rs.getDouble("SVGWIDTH_SVG"),
						rs.getDouble("SVGHEIGHT_SVG")
					));
				}
			}
		} catch (SQLException e) {
			logger.severe("SQLSvgDao.getFromId: " + e.getMessage());
		}
		return Optional.empty();
	}
}
