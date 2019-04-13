package qmes.word.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.StorageListener;
import qmes.word.def.FeatureState;

public class FeatureStateStorageListener implements StorageListener {

	private static final Logger log = LoggerFactory.getLogger(FeatureStateStorageListener.class);
	
	WordIndexer indexer = null;
	public FeatureStateStorageListener(WordIndexer indexer) {
		this.indexer = indexer;
	}

	public void beforeUpdateObject(Object object) {
		deleteFromIndexer((FeatureState) object);
	}

	public void afterUpdateObject(Object object) {

		addToIndexer((FeatureState)object);
	}

	public void addObject(Object object) {
		addToIndexer((FeatureState)object);
	}
	
	public void removeObject(Object object) {
		deleteFromIndexer((FeatureState) object);
	}

	private void deleteFromIndexer(FeatureState fs) {
		try {
			indexer.initWriter();
			log.info("delete word {} from index",fs);
			indexer.deleteDocument(fs);
			indexer.close();
		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			try {
				indexer.close();
			}catch(Exception ex2) {
				ex2.printStackTrace();
				log.error(ex2.getMessage());
			}
		}
	}
	
	private void addToIndexer(FeatureState fs) {
		try {
			indexer.initWriter();
			log.info("add new word {} to index",fs);
			indexer.createDocument(fs);
			indexer.close();
		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			try {
				indexer.close();
			}catch(Exception ex2) {
				ex2.printStackTrace();
				log.error(ex2.getMessage());
			}
		}
	}
}
