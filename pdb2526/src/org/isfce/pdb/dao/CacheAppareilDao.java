package org.isfce.pdb.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.isfce.pdb.model.Appareil;

public class CacheAppareilDao implements IAppareilDao{
	
	private static final Logger logger = Logger.getLogger(CacheAppareilDao.class.getName());
	private Map<String, Appareil> cache = new HashMap<>();
	private IAppareilDao dao;
	
	public CacheAppareilDao(IAppareilDao dao) {
		this.dao = dao;
	}
	
	@Override
	public Optional<Appareil> getFromId(String id) {
		if (cache.containsKey(id)) {
			logger.info("Cache hit Appareil : " + id);
			return Optional.of(cache.get(id));
		}
		Optional<Appareil> appareil = dao.getFromId(id);
		appareil.ifPresent(a -> cache.put(id, a));
		return appareil;
	}
}
