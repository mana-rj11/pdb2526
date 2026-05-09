package org.isfce.pdb.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Installation {
	private Integer id;
	
	private LocalDate date;
	
	private Adresse adresse;
	
	private String Installateur;
	
	private String proprietaire;
}
