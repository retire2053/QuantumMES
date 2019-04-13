package qmes.rule.search;

import java.io.IOException;
import java.nio.file.Paths;

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

import qmes.core.Model;
import qmes.rule.def.DefRule;


public class RuleIndex {
	
	private static final Logger log = LoggerFactory.getLogger(RuleIndex.class);

	private Model model = null;
	private String basepath = null;
	private IndexWriter writer = null;
	private FieldType contentType = null;
	private FieldType infoType = null;

	public RuleIndex(Model model, String basepath) throws Exception{
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
		
		log.info("start to create search engine for rule using lucene");

		for(int i=0;i<model.getRules().size();i++) {
			DefRule dRule = model.getRules().get(i);
			//规则解析
			RuleLhsResolve rLhsResolve = new RuleLhsResolve(dRule,model);
			Document doc = new Document();
			doc.add(new Field("lhs", rLhsResolve.getLhsRaw(), getContentType()));
			doc.add(new Field("rhs", rLhsResolve.getRhsRaw(), getContentType()));
			doc.add(new Field("ruleName", dRule.getName(), getContentType()));
			writer.addDocument(doc);
		}
		return writer.numDocs();
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
