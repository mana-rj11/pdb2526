package org.isfce.pdb.databases.connexion;

import java.util.Properties;

@FunctionalInterface
public interface IConnexionInfos { 
 /**
  * Retourne une map avec les infos de connexion précisant:
  * user
  * password
  * encoding
  * url
  * ...
  * @return
  */
 Properties getProperties();
}
