package org.isfce.pdb;

import java.sql.Connection;

import org.isfce.pdb.databases.connexion.ConnexionFromFile;
import org.isfce.pdb.databases.connexion.ConnexionSingleton;
import org.isfce.pdb.databases.connexion.IConnexionInfos;
import org.isfce.pdb.databases.connexion.PersistanceException;
import org.isfce.pdb.databases.uri.Databases;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestConnect {

	public static void main(String[] args) {

		IConnexionInfos info;
		try {
			info = new ConnexionFromFile("ressources/connexionPDB2526.properties", Databases.FIREBIRD);
			ConnexionSingleton.setInfoConnexion(info);
			Connection connect = ConnexionSingleton.getConnexion();
			log.info("Connection réussie à la base de Données PDB2526");

			ConnexionSingleton.liberationConnexion();
		} catch (PersistanceException e) {
			System.err.println("ERREUR: " + e.getMessage());
		}

	}

}
