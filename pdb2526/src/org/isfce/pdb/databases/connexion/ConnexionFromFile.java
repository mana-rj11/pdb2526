package org.isfce.pdb.databases.connexion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.isfce.pdb.databases.uri.Databases;

import lombok.extern.slf4j.Slf4j;

//@formatter:off
/**
 * !!! Respecter la casse des propriétés
 * Permet de configurer les paramètres de connexion 
 * dans un fichier xxx.properties avec les clés suivantes 
 * user 	  = ... (obligatoire) 
 * password   = ... (obligatoire)  
 * file 	  = ... (obligatoire si pas url)
 * encoding   = ... (default None) 
 * role 	  = ... (default pas de role) 
 * port 	  = ... (default celui du SGBDR) 
 * ip 		  = ... (default localhost)
 * autoCommit = ... (default true)
 * si on ne précise pas la propriété file, il est possible de définir
 * le chemin de l'url du serveur via la propriété suivante:
 * url		  = ... (chemin complet jdbc d'accès au fichier) pas obligatoire.	
 * @author vo
 *
 */
//@formatter:on
@Slf4j
public class ConnexionFromFile implements IConnexionInfos {
	// map de propriétés
	private Properties props = new Properties();

	//@formatter:off
	/**
	 * Le chemin et le nom du fichier contenant les propriétés info:
	 * ./ressources/connexion.properties (représente un fichier avec un chemin relatif) 
	 *  s'il est à null le chemin "./ressources/connexion.properties" sera utilisé
	 * 
	 * @param filename le nom du fichier qui contient les informations de connexion
	 * @param sgbd     le type de base de données
	 * @throws PersistanceException
	 */
	
	public ConnexionFromFile(String filename, Databases sgbd) throws PersistanceException {
		// Try with resources pour la connexion au fichier
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			props.load(br);// charge toutes les proprités du fichier dans la map
			// construit l'URL si elle n'est pas présente
			props.putIfAbsent("url", 
					sgbd.buildServeurURL(props.getProperty("file"),
										 props.getProperty("ip", "localhost"),
					                     props.containsKey("port") ? 
					                    		 Integer.parseInt(props.getProperty("port")) 
											     :sgbd.getDefaultPort()));	
			//si on ne précise pas l'autoCommit ==> sera à true
			props.putIfAbsent("autoCommit", "true");

			log.info("url: " + props.getProperty("url"));
		} catch (IOException e) {
			log.error("Problème de chargement du fichier " + filename + " : " + e.getMessage());
			throw new PersistanceException("Problème de chargement du fichier " + filename + " : " + e.getMessage());
		}

	}
	//@formatter:on
	@Override
	public Properties getProperties() {
		return props;
	}

}
