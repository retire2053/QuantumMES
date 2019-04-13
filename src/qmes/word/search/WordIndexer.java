package qmes.word.search;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qmes.base.BASEUI;
import qmes.base.CONST;
import qmes.base.IndexerBase;
import qmes.core.Model;
import qmes.word.def.FeatureName;
import qmes.word.def.FeatureState;
import qmes.word.def.Word;
import qmes.word.storage.WordStorage;

public class WordIndexer extends IndexerBase{
	
	private static final Logger log = LoggerFactory.getLogger(WordIndexer.class);

	private Model model = null;
	private String basepath = null;
	private IndexWriter writer = null;
	
	private SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();

	public WordIndexer(Model model, String basepath) throws Exception{
		this.model = model;
		this.basepath = basepath;

		initWriter();
	}
	
	protected void initWriter()throws Exception {
		Directory dir = FSDirectory.open(Paths.get(basepath));
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(dir, iwc);
	}
	
	public int getCount(){
		return writer.numDocs();
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
		
		log.info("start to create search engine for SINGLE-WORD-SEARCH");
		int count = 0;
		
		WordStorage fnws = model.getWordStorage(CONST.NAMESPACE, CONST.TYPE_FEATURE_NAMES);
		WordStorage fsws = model.getWordStorage(CONST.NAMESPACE, CONST.TYPE_FEATURE_STATES);
		
		//创建监听器
		fnws.addStorageListener(new FeatureNameStorageListener(this));
		fsws.addStorageListener(new FeatureStateStorageListener(this));
		
		try {
			List<Word> list = fnws.listWords();
			if (list.size() > 0) {
				for(int k=0;k<list.size();k++) {
					FeatureName w = (FeatureName)list.get(k);
					log.debug("No.{} word-cominbiation={}", (1), w.getValue());
					createDocument(w);
					count++;
				}
			}
			
			
			list = fsws.listWords();
			if (list.size() > 0) {
				
				for(int k=0;k<list.size();k++) {
					FeatureState w = (FeatureState)list.get(k);
					log.debug("No.{} word-cominbiation={}", (1), w.getValue());
					createDocument(w);
					count++;
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
		
		log.info("finish indexing {} SINGLE-WORD-SEARCH for wordnet", count);

		return writer.numDocs();
	}

	public void close() throws IOException {
		writer.close();
	}

	public void deleteAll() throws IOException {
		writer.deleteAll();
	}
	
	protected void createDocument(FeatureName w)throws Exception {
		Document doc = new Document();
		doc.add(new Field(CONST.SEARCH_FN_OR_FS, CONST.SEARCH_FEATURE_NAME, getContentType()));
		doc.add(new Field(CONST.SEARCH_FEATURE_NAME,w.getValue(), getContentType()));
		doc.add(new Field(CONST.SEARCH_FEATURE_NAME_SYNONYM,BASEUI.stringArrayToString(w.getAffiliates()), getContentType()));
		doc.add(new Field(CONST.SEARCH_GROUP,BASEUI.stringArrayToString(w.getGroups()), getContentType()));
		doc.add(new Field(CONST.SEARCH_CLASS,BASEUI.stringArrayToString(w.getFeatureClasses()), getContentType()));
		if(w.getNamespace()!=null)
		doc.add(new Field(CONST.SEARCH_NAMESPACE, w.getNamespace(), getContentType()));
		
		writer.addDocument(doc);
		writer.commit();
	}
	
	protected void createDocument(FeatureState w)throws Exception {
		Document doc = new Document();
		doc.add(new Field(CONST.SEARCH_FN_OR_FS, CONST.SEARCH_FEATURE_STATE, getContentType()));
		doc.add(new Field(CONST.SEARCH_FEATURE_STATE,w.getValue(), getContentType()));
		doc.add(new Field(CONST.SEARCH_FEATURE_STATE_SYNONYM,BASEUI.stringArrayToString(w.getAffiliates()), getContentType()));
		doc.add(new Field(CONST.SEARCH_GROUP,w.getGroup(), getContentType()));
		if(w.getNamespace()!=null)
		doc.add(new Field(CONST.SEARCH_NAMESPACE, w.getNamespace(), getContentType()));
		writer.addDocument(doc);
		writer.commit();
	}
	
	//当我们删除一个索引的时候，要用TermQuery，否则避免索引删除的扩大化
	protected void deleteDocument(Word w)throws Exception{

		Term term = null;
		if(w instanceof FeatureName) {
			term = new Term(CONST.SEARCH_FEATURE_NAME, w.getValue());
			
		}else if(w instanceof FeatureState) {
			term = new Term(CONST.SEARCH_FEATURE_STATE, w.getValue());
		}else {
			log.error("Unimplemented branch for deleteDocument");
			return;
		}

		writer.deleteDocuments(term);
		writer.commit();
		log.info("after remove \"{}\" from index, there are {} index left", w.getValue(), writer.numDocs());

	}
	
}
