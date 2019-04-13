package qmes.rule.search;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleSearch {

	private static final Logger log = LoggerFactory.getLogger(RuleSearch.class);

	private Directory dir = null;
	private IndexSearcher searcher = null;
	private String basepath = null;

	public RuleSearch(String basepath) throws Exception {
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
		// return first
		long start = System.currentTimeMillis();

		TopDocs hits = searcher.search(q, 100);

		long end = System.currentTimeMillis();

		log.info("Found {} document(s) (in {} milliseconds) that match query" + q + "'", hits.totalHits, (end - start));

		List<Document> results = new ArrayList<Document>();
		if (hits.scoreDocs != null && hits.scoreDocs.length > 0) {
			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				log.info("found document]{}", doc.toString());
				// Explanation explanation = searcher.explain(q, scoreDoc.doc);
				// log.info(explanation.toString());
				
				results.add(doc);
			}
		}
		return results;
	}

	public List<Document> search(String[] type, String[] queryString)
			throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {
		SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
		
		return executeQuery(MultiFieldQueryParser.parse(queryString, type, analyzer));
	}

	public void close() throws Exception {
		dir.close();
	}

}
