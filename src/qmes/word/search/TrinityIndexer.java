package qmes.word.search;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.CONST;
import qmes.core.Model;
import qmes.word.def.FeatureName;
import qmes.word.def.FeatureState;
import qmes.word.def.Word;
import qmes.word.storage.FeatureNameStorage;
import qmes.word.storage.FeatureStateStorage;
import qmes.word.storage.WordStorageHelper;


public class TrinityIndexer {
	
	private static final Logger log = LoggerFactory.getLogger(TrinityIndexer.class);

	private Model model = null;
	private String basepath = null;
	private IndexWriter writer = null;
	private FieldType contentType = null;
	private FieldType infoType = null;

	public TrinityIndexer(Model model, String basepath) throws Exception{
		this.model = model;
		this.basepath = basepath;

		Directory dir = FSDirectory.open(Paths.get(basepath));
		SmartChineseAnalyzer sa = new SmartChineseAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(sa);
		writer = new IndexWriter(dir, iwc);
	}
	
	public int getCount(){
		return writer.numDocs();
	}
	
	
	private List<WordSequence> createWordSequence(String namespace, FeatureName fn, FeatureState fs, String group) {
		ArrayList<WordSequence> wss = new ArrayList<WordSequence>();
		
		
		List<String> wordsleft = new ArrayList<String>();
		wordsleft.add(fn.getValue());
		wordsleft.addAll(fn.getAffiliates());
		
		List<String> wordsright = new ArrayList<String>();
		wordsright.add(fs.getValue());
		wordsright.addAll(fs.getAffiliates());
		
		for(int p=0;p<fn.getFeatureClasses().size();p++) {
			
			String clazz = fn.getFeatureClasses().get(p);
			
			for(int i=0;i<wordsleft.size();i++) {
				String lword = wordsleft.get(i);
				
				
				for(int j=0;j<wordsright.size();j++) {
					String rword = wordsright.get(j);
					
					WordSequence ws = new WordSequence(namespace, clazz, fn.getValue(), fs.getValue(), lword, rword, group);
					wss.add(ws);
				}
			}
		}
		
		return wss;
	}
	
	
	private List<WordSequence> createWordSequence(String namespace, FeatureName fn, List<FeatureState> fss, String group) {
		ArrayList<WordSequence> wss = new ArrayList<WordSequence>();
		if (fss.size() > 0) {
			for (int i = 0; i < fss.size(); i++) {
				wss.addAll(createWordSequence(namespace, fn, fss.get(i), group));
			}
		}
		return wss;
	}
	
	public void createIndex() {
		try {
			index();
			close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			try {
				close();
			} catch (Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
			}
		}
	}

	private int index() throws IOException {
		
		log.info("start to indexing word-combinations (WORD-SEQUENCE or TRINITY) for wordnet");

		List<WordSequence> wss = new ArrayList<WordSequence>();

		FeatureNameStorage fnws = (FeatureNameStorage)model.getWordStorage(CONST.NAMESPACE, CONST.TYPE_FEATURE_NAMES);
		FeatureStateStorage fsws = (FeatureStateStorage)model.getWordStorage(CONST.NAMESPACE, CONST.TYPE_FEATURE_STATES);

		List<Word> list = fnws.listWords();
		if (list.size() > 0) {
			for (int p = 0; p < list.size(); p++) {
				FeatureName fn = (FeatureName) list.get(p);
				List<String> groups = fn.getGroups();
				if (groups.size() > 0) {
					for (int t = 0; t < groups.size(); t++) {
						String group = groups.get(t);
						List<FeatureState> fss = fsws.findFeatureStateByGroup(group);
						wss.addAll(createWordSequence(CONST.NAMESPACE, fn, fss, group));
					}
				}
				
				String group = CONST.EXIST_GROUP;
				List<FeatureState> fss = WordStorageHelper.listExistState();
				wss.addAll(createWordSequence(CONST.NAMESPACE, fn, fss, group));
			}
		}
		
		list = fsws.listWords();
		if(list.size()>0) {
			for(int p=0;p<list.size();p++) {
				FeatureState fs  = (FeatureState)list.get(p);
				List<String> feature_names = fs.getFeatureNames();
				for(int k=0;k<feature_names.size();k++) {
					String feature_name = feature_names.get(k);
					FeatureName fn = (FeatureName)fnws.findWord(feature_name);
					if(fn!=null) {
						wss.addAll(createWordSequence(CONST.NAMESPACE, fn, fs, ""));
					}else {
						log.error("find out NO FEATURE NAME with \"{}\"", feature_name);
					}
				}
			}
		}
		
		SearchIndicatorSHelper sish = new SearchIndicatorSHelper(model);
		wss.addAll(sish.createIndicatorS());
		wss.addAll(sish.createIndicatorTS());

		if(wss.size()>0) {
			for (int i=0;i<wss.size();i++) {
				WordSequence ws = wss.get(i);
				Document doc = new Document();
				log.debug("No.{} word-cominbiation={}", (i+1), ws.toString());
				doc.add(new Field(CONST.SEARCH_CONTENT, ws.toSearchString(), getContentType()));
				doc.add(new Field(CONST.SEARCH_FEATURE_NAME,ws.getFeatureName(), getInfoType()));
				doc.add(new Field(CONST.SEARCH_FEATURE_STATE,nullWrapper(ws.getFeatureState()), getInfoType()));
				doc.add(new Field(CONST.SEARCH_FEATURE_NAME_SYNONYM,nullWrapper(ws.getFnword()), getInfoType()));
				doc.add(new Field(CONST.SEARCH_FEATURE_STATE_SYNONYM,nullWrapper(ws.getFsword()), getInfoType()));
				doc.add(new Field(CONST.SEARCH_CLASS,ws.getClazz(), getInfoType()));
				doc.add(new Field(CONST.SEARCH_GROUP,ws.getGroup(), getInfoType()));
				doc.add(new Field(CONST.SEARCH_NAMESPACE,ws.getNamespace(), getInfoType()));
				writer.addDocument(doc);
			}
		}
		log.info("finish indexing {} word-combinations (WORD-SEQUENCE or TRINITY) for wordnet", wss.size());

		return writer.numDocs();
	}
	
	private String nullWrapper(String n) {
		if(n!=null)return n;
		else return "";
	}

	public void close() throws IOException {
		writer.close();
	}

	public void deleteAll() throws IOException {
		writer.deleteAll();
	}
	
	private FieldType getContentType() {
		if (contentType == null) {
			contentType = new FieldType();
			contentType.setStored(true);
			contentType.setTokenized(true);
			contentType.setIndexOptions(IndexOptions.DOCS);
			contentType.setStoreTermVectors(true);
			contentType.setStoreTermVectorOffsets(true);
			contentType.setStoreTermVectorPayloads(true);
			contentType.setStoreTermVectorPositions(true);
		}
		return contentType;
	}
	
	private FieldType getInfoType() {
		if (infoType == null) {
			infoType = new FieldType();
			infoType.setStored(true);
			infoType.setTokenized(false);
			infoType.setIndexOptions(IndexOptions.NONE);
			infoType.setStoreTermVectors(false);
			infoType.setStoreTermVectorOffsets(false);
			infoType.setStoreTermVectorPayloads(false);
			infoType.setStoreTermVectorPositions(false);
		}
		return infoType;
	}
}
