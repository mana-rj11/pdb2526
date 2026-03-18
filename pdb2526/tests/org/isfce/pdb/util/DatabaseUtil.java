package org.isfce.pdb.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;

import org.apache.ibatis.jdbc.ScriptRunner;

public class DatabaseUtil {

	/**
	 * Initialise la base de données de test Utilise MyBatis pour exécuter le script
	 * SQL
	 * 
	 * @param con  connection à la base de données
	 * @param file le fichier SQL à exécuter
	 * @throws IOException
	 */

	public static void executeScriptSQL(Connection con, String file) {
		// Initialize the script runner
		ScriptRunner sr = new ScriptRunner(con);
		// Creating a reader object
		try (Reader reader = new BufferedReader(
					new FileReader(file, 
							Charset.forName("utf8")))){
			// Running the script
			sr.runScript(reader);
		} catch (IOException e) {
			System.err.println("ERREUR CHARGEMENT SCRIPT: "+ e.getMessage());
		}
	}
}
