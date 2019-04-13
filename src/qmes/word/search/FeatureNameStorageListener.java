package qmes.word.search;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.StorageListener;
import qmes.word.def.FeatureName;

public class FeatureNameStorageListener implements StorageListener{
	
	private static final Logger log = LoggerFactory.getLogger(FeatureNameStorageListener.class);
	
	WordIndexer indexer = null;
	public FeatureNameStorageListener(WordIndexer indexer) {
		this.indexer = indexer;
	}

	public void beforeUpdateObject(Object object) {
		deleteFromIndexer((FeatureName) object);
	}

	public void afterUpdateObject(Object object) {
		addToIndexer((FeatureName)object);
	}

	public void addObject(Object object) {
		addToIndexer((FeatureName)object);
	}
	
	public void removeObject(Object object) {
		deleteFromIndexer((FeatureName) object);
	}

	private void deleteFromIndexer(FeatureName fn) {
		try {
			indexer.initWriter();
			log.info("delete word {} from index",fn);
			indexer.deleteDocument(fn);
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
	
	private void addToIndexer(FeatureName fn) {
		try {
			indexer.initWriter();
			log.info("add new word {} to index",fn);
			indexer.createDocument(fn);
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
