package qmes.word.search;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Searcher {

	private static final Logger log = LoggerFactory.getLogger(Searcher.class);

	private Directory dir = null;
	private IndexSearcher searcher = null;
	private String basepath = null;

	public Searcher(String basepath) throws Exception {
		dir = FSDirectory.open(Paths.get(basepath));
		searcher = new IndexSearcher(DirectoryReader.open(dir));
	}

	public void watch() throws Exception {

		IndexReader ir = searcher.getIndexReader();
		int count = 10;
		int z = ir.maxDoc();
		if (z < 10)
			count = z;
		log.info("try to print {} indexed document.", count);
		for (int i = 0; i < count; i++) {
			log.info("No.{} of indexed document={}", (i + 1), searcher.doc(i).toString());
		}
	}

	private List<Document> executeQuery(Query q) throws IOException {

		long start = System.currentTimeMillis();
		TopDocs hits = searcher.search(q, 1000);	//TODO 有可能词语超过了1000行
		long end = System.currentTimeMillis();

		log.info("list {} document(s) (in {} milliseconds) that match query" + q + "'", hits.totalHits, (end - start));

		List<Document> results = new ArrayList<Document>();
		if (hits.scoreDocs != null && hits.scoreDocs.length > 0) {
			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				log.debug("found document]{}", doc.toString());
				results.add(doc);
			}
		}
		return results;
	}
	
	private void queryAll()throws IOException{
        int count = searcher.getIndexReader().numDocs();
        log.info("total document count={}", count);
        for (int i = 0; i < count; i++){
            Document doc = searcher.doc(i);
            List<IndexableField> listField = doc.getFields();
            for ( int j = 0;j < listField.size(); j++){
                IndexableField index = listField.get(j);
                log.info("No.{}, index name={}, index value={}",(i+1),index.name(),index.stringValue());
            }
        }
	}
	
	public Query createQuery(String type, String queryString)throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {
		SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
		QueryParser queryParser = new QueryParser(type, analyzer);
		return queryParser.parse(queryString);
	}
	
	public List<Document> search(List<Query> queries)throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException{
		
		Builder b = new BooleanQuery.Builder();
		for(int i=0;i<queries.size();i++) {
			b.add(queries.get(i), BooleanClause.Occur.MUST);  
		}
		BooleanQuery bq = b.build();
		return executeQuery(bq);
		
	}

	public List<Document> search(String type, String queryString)
			throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {
		SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
		QueryParser queryParser = new QueryParser(type, analyzer);
		return executeQuery(queryParser.parse(queryString));
	}

	public void close() throws Exception {
		dir.close();
	}

}
