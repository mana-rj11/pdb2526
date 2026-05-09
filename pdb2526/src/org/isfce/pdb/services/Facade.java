package org.isfce.pdb.services;

import java.util.List;

import org.isfce.pdb.dao.DAOFactory;
import org.isfce.pdb.model.Element;
import org.isfce.pdb.model.Installation;
import org.isfce.pdb.model.Piece;

public class Facade {

	private DAOFactory factory;
	private Installation installation;
	//pièces de l'installation
	private List<Piece> pieces;
	//élements de l'installation
	private List<Element> elements;

	public Facade(DAOFactory factory) {
		this.factory=factory;
	}
	
	public void chargeInstallation() {
		
		
		
		
	}
	
	

}
