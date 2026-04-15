package org.isfce.pdb.dao;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.isfce.pdb.dao.DAOFactory.TypePersistance;
import org.isfce.pdb.databases.connexion.ConnexionFromFile;
import org.isfce.pdb.databases.connexion.ConnexionSingleton;
import org.isfce.pdb.databases.uri.Databases;
import org.isfce.pdb.exceptions.CheckException;
import org.isfce.pdb.exceptions.PKException;
import org.isfce.pdb.model.TypePiece;
import org.isfce.pdb.util.DatabaseUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestDaoTypePiece {

	private static ITypePieceDao dao;
	static TypePiece p = new TypePiece("SALON", "Salon", false);

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(
				new ConnexionFromFile("./ressources/connexionPDB2526_test.properties", Databases.FIREBIRD));
		// Réinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(), "./ressources/scriptInitDBTest.sql");
		var factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		dao = factory.getTypePieceDAO();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		// Réinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(), "./ressources/scriptInitDBTest.sql");
		ConnexionSingleton.liberationConnexion();
	}

	@Test
	void testGetFromId() throws SQLException {
		var oObj = dao.getFromID("salon");
		assertTrue(oObj.isPresent());
		assertEquals(p, oObj.get());
	}

	@Test
	void testGetListe() throws SQLException {
		var liste = dao.getListe(null);
		assertEquals(4,liste.size());
		assertEquals(p, liste.get(1));
	}
	@Test
	void testInsert() {
		TypePiece tp1=new TypePiece("SALON", "doublon!!", false);
		assertThrows(PKException.class,()-> dao.insert(tp1));
		
		TypePiece tp2=new TypePiece("BROL", "", false);
		assertThrows(CheckException.class,()-> dao.insert(tp2));
		
	}

}
