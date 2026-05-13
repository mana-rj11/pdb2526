package org.isfce.pdb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

import org.isfce.pdb.model.Appareil;
import org.isfce.pdb.model.Svg;

public class SQLAppareilDao implements IAppareilDao{
	
	private static final Logger logger = Logger.getLogger(SQLAppareilDao.class.getName());
	private Connection connection;
	private ISvgDao svgDao;
	
	public SQLAppareilDao(DAOFactory factory) {
		this.connection = factory.getConnection();
		this.svgDao = factory.getSvgDao();
	}
	
	@Override 
	public Optional<Appareil> getFromId(String id) {
		String sql = "SELECT CODE_APP, NOM_APP, FKSVG_APP, CLASSE_APP "
				   + "FROM TAPPAREIL WHERE CODE_APP = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Optional<Svg> svg = svgDao.getFromId(rs.getString("FKSVG_APP"));
					Appareil.Classe classe = Appareil.Classe.valueOf(
						rs.getString("CLASSE_APP"));
					return Optional.of(new Appareil(
						rs.getString("CODE_APP"),
						rs.getString("NOM_APP"),
						svg.orElse(null),
						classe
						));
				}
			}
		} catch (SQLException e) {
			logger.severe("SQLAppareilDao.getFromId: " + e.getMessage());
		}
		return Optional.empty();
	}
}
