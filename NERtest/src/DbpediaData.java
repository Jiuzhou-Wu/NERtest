import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.jena.query.* ;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP ;

public class DbpediaData
{
    static public void main(String[] argv) throws IOException
    {
    	int size = 500;
    	List<String> content = new ArrayList<String>();
    	try(Stream<Path> paths = Files.walk(Paths.get("lib/wiki page"))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            try {
						List<String> s = Files.readAllLines(filePath);
						String title = s.get(1);
						title = title.substring(11, title.length()-8);
						title = title.replaceAll(" ", "_");
//						title = title.toLowerCase();
						content.add(title);
//						System.out.println(title);
					} catch (Exception e) {
						// TODO Auto-generated catch block
//						System.out.println(title);
						e.printStackTrace();
					}
		        }
		    });
		}
    	System.out.println("get data from ontology");
    	int numOfClass = 0;
    	for(int i = 0; i < Math.min(size, content.size()); i++){
    		
    		String title = content.get(i);
    		
    		String result = DbData(title);
    		title = title.replaceAll("_", " ");
//    		System.out.println(result);
    		
      	    if(result == "instance"){
      	    	File file = new File("lib/data/" + (i+501- numOfClass));
        		file.createNewFile();
      	    	BufferedWriter fw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));
            	fw.write("\n");
    			fw.write(title);
    			fw.write("\n");
            	fw.write("instance");
            	fw.close();
      	    } else {
      	    	numOfClass ++ ;
      	    	size++;
      	    }
      	    
        	
    	}
//    	DBontology(1000);
    	
    	
    }
    
    /**
     * 
     * @param title
     * 
     */
    public static String DbData (String title){
//dbpedia stuff
    	
    	String input = title;    //lower case always
    	String prefix = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
        String queryStr = prefix + 
        		"SELECT ?label WHERE{"+ 
  			    "<http://dbpedia.org/ontology/"+input+"> rdfs:label ?label ."+
  			    "}";
        Query query = null;
        try{
        	query = QueryFactory.create(queryStr);
        } catch (Exception e) {
//        	System.out.println("here");
        	System.out.println("Query Error, the last query tried with the: " + title);
        	return "instance";
//        	e.printStackTrace();
//            throw new Exception();
        }
        

        // Remote execution.
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query) ) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;

            // Execute.
            ResultSet rs = qexec.execSelect();
            String classify = "";
            if( rs.hasNext()  ){
            	//this is a class
//            	System.out.println("here");
            	RDFNode n = rs.next().get("label");
            	classify = "class";
            	
            }else{
            	classify = "instance";
            }
            
            return classify;
            
            //ResultSetFormatter.out(System.out, rs, query);
        } catch (Exception e) {
        	System.out.println("here");
        	System.out.println("The last query tried with the: " + title);
            e.printStackTrace();
        }
        return "Not";
    }
    
    public static void DBontology(int size){
    	
    	String prefix = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
    			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>";;
        String queryStr = prefix+"SELECT distinct (STR(?l) AS ?label) {   ?type a owl:Class;  rdfs:label  ?l .   FILTER (LANG(?l) = \"en\") filter( regex( str(?type), \"^http://dbpedia.org/ontology/\" ) )}";
        Query query = null;
        
        query = QueryFactory.create(queryStr);
// Remote execution.
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query) ) {
        	
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            
            // Execute.
            ResultSet rs = qexec.execSelect();
            
            String textResult = "";
            String[] strArrayResult;
            
            if( rs.hasNext()  ){
            	textResult = ResultSetFormatter.asText(rs);
            	System.out.println(textResult);
            	strArrayResult = textResult.split("\n");
            	
            	for(int i = 0; i < Math.min(size, strArrayResult.length); i++){
            		System.out.println(strArrayResult[i]);
            		if(strArrayResult[i].contains("\"")){
            			File file = new File("lib/data/" + (i-2));
            			if (file.createNewFile()){
            			 //created
            			} else {
            			 //exists
            			
            			}
            			
            			strArrayResult[i] = strArrayResult[i].substring(strArrayResult[i].indexOf("\"")+1, strArrayResult[i].lastIndexOf("\""));
            			strArrayResult[i] = strArrayResult[i].replaceAll("_", " ");
            			BufferedWriter fw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));
            			fw.write("\n");
            			fw.write(strArrayResult[i]);
            			fw.write("\n");
                    	fw.write("class");
                    	fw.close();
            		} else {
            			size++;
            		}
        			
            	}
                
            }
        } catch (Exception e) {
        	
            e.printStackTrace();
        }

        
        return ;
    }
    
    private static void fileWrite(String file, String context){
//    	if(strArrayResult[i].contains("\"")){
//			
//			File file = new File("lib/data/ClassData/" + i);
//        	
//      	    if (file.createNewFile()){
//      	    	
//      	        System.out.println("File is created!");
//      	    }else{
//      	        System.out.println("File already exists.");
//      	    }
//      	    
//			strArrayResult[i] = strArrayResult[i].substring(strArrayResult[i].indexOf("\"")+1, strArrayResult[i].lastIndexOf("\""));
//			strArrayResult[i] = strArrayResult[i].replaceAll("_", " ");
//			BufferedWriter fw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));
//			fw.write("\n");
//			fw.write(strArrayResult[i]);
//        	fw.write("class");
//        	fw.close();
//		}else{
//			i--;
//		}
    }
}