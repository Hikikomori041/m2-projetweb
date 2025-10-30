package com.michelin.restaurants.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class EvaluationIndexService {

    private final IndexWriter indexWriter;
    private DirectoryReader directoryReader;
    private IndexSearcher indexSearcher;
    private final QueryParser evaluationQueryParser;

    public EvaluationIndexService() throws IOException {
        Path path = Paths.get("evaluation-index");
        Directory index = FSDirectory.open(path);

        StandardAnalyzer analyzer = new StandardAnalyzer();
        this.evaluationQueryParser = new QueryParser("comment", analyzer);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        this.indexWriter = new IndexWriter(index, config);

        if (DirectoryReader.indexExists(index)) {
            this.directoryReader = DirectoryReader.open(index);
            this.indexSearcher = new IndexSearcher(directoryReader);
        } else {
            this.indexWriter.commit();
            this.directoryReader = DirectoryReader.open(index);
            this.indexSearcher = new IndexSearcher(directoryReader);
        }
    }

    private void refreshIndex() throws IOException {
        DirectoryReader newReader = DirectoryReader.openIfChanged(this.directoryReader);
        if (newReader != null) {
            this.directoryReader.close();
            this.directoryReader = newReader;
            this.indexSearcher = new IndexSearcher(this.directoryReader);
        }
    }

    // Indexation d'une évaluation
    public void indexEvaluation(String id, String comment) {
        try {
            Document doc = new Document();
            doc.add(new StringField("id", id, Field.Store.YES));
            doc.add(new TextField("comment", comment, Field.Store.YES));
            this.indexWriter.addDocument(doc);
            this.indexWriter.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Recherche par mots-clés
    public List<String> searchEvaluation(String textToSearch) {
        try {
            refreshIndex();
            Query query = evaluationQueryParser.parse(textToSearch);
            TopDocs topDocs = this.indexSearcher.search(query, 10);

            return Arrays.stream(topDocs.scoreDocs)
                    .map(scoreDoc -> {
                        try {
                            Document doc = this.indexSearcher.storedFields().document(scoreDoc.doc);
                            return doc.get("id");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .distinct().toList();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEvaluation(String id) {
        try {
            indexWriter.deleteDocuments(new Term("id", id));
            indexWriter.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
