package yelp.search;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

	private IndexWriter writer;

	/**
	 * initialize indexer
	 * 
	 * @param indexDirectoryPath
	 * @throws IOException
	 */
	public Indexer(String indexDirectoryPath) throws IOException {
		Directory indexDirectory = FSDirectory
				.open(new File(indexDirectoryPath));
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
				analyzer);
		writer = new IndexWriter(indexDirectory, config);
	}

	/**
	 * close indexer
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

	/**
	 * create document object by index files
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private Document getDocument(File file) throws IOException {
		Document document = new Document();

		// index file contents
		Field contentField = new Field(LuceneConstants.CONTENTS,
				new FileReader(file));
		// index file name
		Field fileNameField = new Field(LuceneConstants.FILE_NAME,
				file.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED);
		// index file path
		Field filePathField = new Field(LuceneConstants.FILE_PATH,
				file.getCanonicalPath(), Field.Store.YES,
				Field.Index.NOT_ANALYZED);

		document.add(contentField);
		document.add(fileNameField);
		document.add(filePathField);

		return document;
	}

	/**
	 * create document object and add it into writer
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void indexFile(File file) throws IOException {
		System.out.println("Indexing " + file.getCanonicalPath());
		Document document = getDocument(file);
		writer.addDocument(document);
	}

	/**
	 * iterate through files in the directory, and index these files
	 * 
	 * @param dataDirPath
	 * @param filter
	 * @return
	 * @throws IOException
	 */
	public int createIndex(String dataDirPath, FileFilter filter)
			throws IOException {
		// get all files in the data directory
		File[] files = new File(dataDirPath).listFiles();

		for (File file : files) {
			if (!file.isDirectory() && !file.isHidden() && file.exists()
					&& file.canRead() && filter.accept(file)) {
				indexFile(file);
			}
		}
		return writer.numDocs();
	}
}