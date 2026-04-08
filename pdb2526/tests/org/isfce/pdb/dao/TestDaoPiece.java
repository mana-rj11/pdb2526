package org.isfce.pdb.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.isfce.pdb.dao.DAOFactory.TypePersistance;
import org.isfce.pdb.databases.connexion.ConnexionFromFile;
import org.isfce.pdb.databases.connexion.ConnexionSingleton;
import org.isfce.pdb.databases.uri.Databases;
import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.TypePiece;
import org.isfce.pdb.util.DatabaseUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class TestDaoPiece {

	private static IPieceDao dao;
	static TypePiece tp = new TypePiece("SALON", "Salon", false);
	static Piece p = Piece.builder().id(1).nom("SALON").description("Salon principal")
			.etage(new BigDecimal(0.0).setScale(1)).typePiece(tp).installation(3).build();

	static Piece pn = Piece.builder().nom("SALON2").description("Salon principal2")
			.etage(new BigDecimal(1.0).setScale(1)).typePiece(tp).installation(3).build();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(
				new ConnexionFromFile("./ressources/connexionPDB2526_test.properties", Databases.FIREBIRD));
		// Réinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(), "./ressources/scriptInitDBTest.sql");
		var factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		dao = factory.getPieceDAO();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		// Réinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(), "./ressources/scriptInitDBTest.sql");
		ConnexionSingleton.liberationConnexion();
	}

	@Test
	void testGetFromId() throws SQLException {
		var oObj = dao.getFromID(1);
		assertTrue(oObj.isPresent());
		assertEquals(p, oObj.get());
	}

	@Test
	void testInsert() throws Exception {
		Piece pn2 = dao.insert(pn);
		assertNotNull(pn.getId());
		//get pour voir si OK
		var oObj = dao.getFromID(pn.getId());
		assertTrue(oObj.isPresent());
		assertEquals(pn2, oObj.get());
		//On ne peut pas sauver un objet avec un ID
		assertThrows(AssertionError.class, ()->dao.insert(pn2));		
	}

}
