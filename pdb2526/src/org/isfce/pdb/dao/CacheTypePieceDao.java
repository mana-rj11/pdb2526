package org.isfce.pdb.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.isfce.pdb.model.TypePiece;

public class CacheTypePieceDao implements ITypePieceDao {
	private ITypePieceDao dao;
	private Map<String, TypePiece> cache = new LinkedHashMap<String, TypePiece>();
	private boolean firstLoad = true;

	public CacheTypePieceDao(ITypePieceDao dao) {
		this.dao = dao;
	}

	@Override
	public Optional<TypePiece> getFromID(String id) {
		if (cache.containsKey(id))// existe dans le cache?
			return Optional.of(cache.get(id).clone());
		// va chercher l'objet en BD
		var oObj = dao.getFromID(id);
		// rajoute l'objet au cache s'il existe
		oObj.ifPresent((obj) -> cache.put(id, obj));
		return oObj.map(TypePiece::clone);
	}

	@Override
	public List<TypePiece> getListe(String regExpr) {
		if (firstLoad) {
			var liste = dao.getListe(null);
			//cache.clear();
			//rajoute tous les objets dans le cache
			liste.forEach((e) -> cache.put(e.getCode(), e));
			firstLoad = false;
		}
		// renvoie des clones depuis le cache, jamais les originaux
		return cache.values().stream().map(TypePiece::clone).collect(Collectors.toList());
		
	}

	@Override
	public TypePiece insert(TypePiece objet) throws Exception {
		objet = dao.insert(objet);
		cache.put(objet.getCode(), objet);
		return objet.clone();
	}

}
