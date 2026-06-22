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
				   + "FROM TAPPAREIL WHERE TRIM(CODE_APP) = ?";
		String code = null;
		String nom = null;
		String fkSvg = null;
		String classeStr = null;
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					code = rs.getString("CODE_APP").trim();
					nom = rs.getString("NOM_APP");
					fkSvg = rs.getString("FKSVG_APP");
					classeStr = rs.getString("CLASSE_APP");
				} else {
					return Optional.empty();
				}
			}
		} catch (SQLException e) {
			logger.severe("SQLAppareilDao.getFromId: " + e.getMessage());
			return Optional.empty();
		}
		// ResultSet fermé a partir d'ici : on peut interroger svgDao sur la meme connexion
		Optional<Svg> svg = svgDao.getFromId(fkSvg);
		Appareil.Classe classe = Appareil.Classe.valueOf(classeStr);
		return Optional.of(new Appareil(code, nom, svg.orElse(null), classe));
	}
}
