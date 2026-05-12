package org.isfce.pdb.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.isfce.pdb.model.Svg;

public class CacheSvgDao implements ISvgDao{
	
	private static final Logger logger = Logger.getLogger(CacheSvgDao.class.getName());
	private Map<String, Svg> cache = new HashMap<>();
	private ISvgDao dao;
	
	public CacheSvgDao(ISvgDao dao) {
		this.dao = dao;
	}

	@Override
	public Optional<Svg> getFromId(String id) {
		if (cache.containsKey(id)) {
			logger.info("Cache hit SVG : " + id);
			return Optional.of(cache.get(id));
		}
		Optional<Svg> svg = dao.getFromId(id);
		svg.ifPresent(s -> cache.put(id, s));
		return svg;
	}
}
