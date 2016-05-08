package yelp.search;

import java.io.*;
import java.net.*;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class LuceneTester {

	String indexDir = "/home/x/programming/cs410/cs410/Index";
	String dataDir = "/home/x/programming/cs410/cs410/data";
	Indexer indexer;
	Searcher searcher;

	public static void main(String[] args) throws IOException {
		LuceneTester tester = new LuceneTester();
		tester = new LuceneTester();
		tester.createIndex();
		
		Messager m = null;
		try {
			m = new Messager(9005);
			m.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int sz;
		String msg;
		while (true) {
			sz = m.msg_size();
			if (sz > 0) {
				msg = m.pop_msg();
				System.out.println("lucene receives " + msg);
				try {
					tester.search(msg);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void createIndex() throws IOException {
		indexer = new Indexer(indexDir);
		int numIndexed;
		long startTime = System.currentTimeMillis();
		numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
		long endTime = System.currentTimeMillis();
		indexer.close();
		System.out.println(numIndexed + " File indexed, time taken: "
				+ (endTime - startTime) + " ms");
	}

	private void search(String searchQuery) throws IOException, ParseException {
		searcher = new Searcher(indexDir);
		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.search(searchQuery);
		long endTime = System.currentTimeMillis();

		System.out.println(hits.totalHits + " documents found. Time :"
				+ (endTime - startTime));
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.getDocument(scoreDoc);
			System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
		}
		searcher.close();
	}
}