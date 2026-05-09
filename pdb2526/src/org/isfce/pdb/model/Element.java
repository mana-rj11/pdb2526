package org.isfce.pdb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public final class Element {
	private final Integer id;
	private final Appareil appareil;
	private final int qt;
	private final String code;
	private final String info;
	private final int ordre;
}
