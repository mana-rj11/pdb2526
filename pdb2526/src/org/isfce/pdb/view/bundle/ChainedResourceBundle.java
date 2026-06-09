package org.isfce.pdb.view.bundle;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class ChainedResourceBundle extends ResourceBundle {
	
	private final List<ResourceBundle> bundleChain;
	
	public ChainedResourceBundle(List<ResourceBundle> bundles) {
		this.bundleChain = bundles;
	}
	
	@Override 
	protected Object handleGetObject(String key) {
		for (ResourceBundle bundle : bundleChain) {
			if (bundle.containsKey(key))
				return bundle.getObject(key);
		}
		return null; // La clé n'a été trouvée dans aucun bundle
	}
	
	@Override
	public Enumeration<String> getKeys() {
		// Combine les clés de tous les bundles, en évitant les doublons
		Set<String> allKeys = new HashSet<>();
		for (ResourceBundle bundle : bundleChain)
			allKeys.addAll(Collections.list(bundle.getKeys()));
		return Collections.enumeration(allKeys);
	}

}
