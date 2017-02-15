import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;


public class SimpleFileIndexer
{

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		File indexDir = new File("C:/index/");
        File dataDir = new File("C:\\Users\\Tato\\Desktop\\Team Project\\Term_def\\terms\\");
        String suffix = ".txt";
        
        SimpleFileIndexer indexer = new SimpleFileIndexer();
        
        int numIndex = indexer.index(indexDir, dataDir, suffix);
        
        System.out.println("Total files indexed " + numIndex);

	}
private int index(File indexDir, File dataDir, String suffix) throws Exception {
        
        IndexWriter indexWriter = new IndexWriter(
                FSDirectory.open(indexDir), 
                new SimpleAnalyzer(),
                true,
                IndexWriter.MaxFieldLength.LIMITED);
        indexWriter.setUseCompoundFile(false);
        
        indexDirectory(indexWriter, dataDir, suffix);
        
        int numIndexed = indexWriter.maxDoc();
        indexWriter.optimize();
        indexWriter.close();
        
        return numIndexed;
        
    }
    
    private void indexDirectory(IndexWriter indexWriter, File dataDir, 
           String suffix) throws IOException {

        File[] files = dataDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            
        	File f1 = files[i];
            if(!f1.getName().endsWith("_RT.txt") && !f1.getName().endsWith("_IL.txt")){
            	File f = f1;
            
            if (f.isDirectory() ) {
                indexDirectory(indexWriter, f, suffix);
            }
            else {
                indexFileWithIndexWriter(indexWriter, f, suffix);
            }
        }
        }
    }
    
    private void indexFileWithIndexWriter(IndexWriter indexWriter, File f, 
            String suffix) throws IOException {

        if (f.isHidden() || f.isDirectory() || !f.canRead() || !f.exists()) {
            return;
        }
        if (suffix!=null && !f.getName().endsWith(suffix) ) {
            return;
        }
        System.out.println("Indexing file " + f.getCanonicalPath());
        
        String str = FileUtils.readFileToString(f, "UTF-8");
        String text = f.getName().substring(0, f.getName().length()-4) ;
        System.out.println(str);
        System.out.println(text);
        Document doc = new Document();
        
        doc.add(new Field("contents", str, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));        
        doc.add(new Field("filename",  text, Field.Store.YES, Field.Index.ANALYZED));
        
        indexWriter.addDocument(doc);

    }
    


}
