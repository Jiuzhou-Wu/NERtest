import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.jena.query.* ;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP ;

public class DbpediaData
{
    static public void main(String[] argv) throws IOException
    {
//    	int size = 500;
//    	List<String> content = new ArrayList<String>();
//    	try(Stream<Path> paths = Files.walk(Paths.get("lib/wiki page"))) {
//		    paths.forEach(filePath -> {
//		        if (Files.isRegularFile(filePath)) {
//		            try {
//						List<String> s = Files.readAllLines(filePath);
//						String title = s.get(1);
//						title = title.substring(11, title.length()-8);
//						title = title.replaceAll(" ", "_");
////						title = title.toLowerCase();
//						content.add(title);
////						System.out.println(title);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
////						System.out.println(title);
//						e.printStackTrace();
//					}
//		        }
//		    });
//		}
//    	System.out.println("get data from ontology");
//    	int numOfClass = 0;
//    	for(int i = 0; i < Math.min(size, content.size()); i++){
//    		
//    		String title = content.get(i);
//    		
//    		String result = DbData(title);
//    		title = title.replaceAll("_", " ");
////    		System.out.println(result);
//    		
//      	    if(result == "instance"){
//      	    	File file = new File("lib/data/" + (i+501- numOfClass));
//        		file.createNewFile();
//      	    	BufferedWriter fw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));
//            	fw.write("\n");
//    			fw.write(title);
//    			fw.write("\n");
//            	fw.write("instance");
//            	fw.close();
//      	    } else {
//      	    	numOfClass ++ ;
//      	    	size++;
//      	    }
//      	    
//        	
//    	}
//    	DbData(1000);
    	Test.test();
    	
    }
    
    
    
    public static ArrayList<String> dbFeature(ArrayList<String> titles){
    	
    	ArrayList<String> resultList = new ArrayList<String>(titles.size());
    	String totalCount = "0";
    	
    	
    	for(int i = 0; i<titles.size(); i++){
    		String title = titles.get(i);
    		
    		for(int j = 0; j<title.length(); j++){
    			if(title.charAt(j)=='\"'){
    				title = title.replace('\"', ' ');
    				
    			}
    		}
    		
    		String prefix = "PREFIX dbo: <http://dbpedia.org/ontology/>"
    				+ "PREFIX dbr: <http://dbpedia.org/resource/>";
    		String queryStr = prefix + "select ( count ( distinct ?instance ) AS ?total ) where {?instance dbo:type <http://dbpedia.org/resource/"
    				+ title.replace(" ", "_")
    				+ "> } LIMIT 100";
//    		System.out.println(queryStr);
    		Query query = QueryFactory.create(queryStr);
    		try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query) ) {
                // Set the DBpedia specific timeout.
                ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;

                // Execute.
                ResultSet rs = qexec.execSelect();
                
                String textResult = "";
                String[] strArrayResult;
                
                if(rs.hasNext())
                {
                	
                	textResult = ResultSetFormatter.asText(rs);
//                	System.out.println(textResult);
                	strArrayResult = textResult.split("\n");
                	
//                	for(int line = 0; line < strArrayResult.length; line++){
//                		System.out.println(strArrayResult[line]);
//                	}
                	System.out.println(i+" "+Integer.parseInt(strArrayResult[3].substring(strArrayResult[3].indexOf("|")+1, strArrayResult[3].lastIndexOf("|")).replaceAll(" ", "")));
                	totalCount = strArrayResult[3].substring(strArrayResult[3].indexOf("|")+1, strArrayResult[3].lastIndexOf("|")).replaceAll(" ", "");
                }
                resultList.add(i, totalCount);
    		} catch (Exception e) {
            	
                e.printStackTrace();
            }
    		
    	}
    	
    	
    	
    	return resultList;
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
    			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>";
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
            			file.createNewFile();
//            			if (){
//            			 //created
//            			} else {
//            			 //exists
//            			
//            			}
            			
            			strArrayResult[i] = strArrayResult[i].substring(strArrayResult[i].indexOf("\"")+1, strArrayResult[i].lastIndexOf("\""));
            			strArrayResult[i] = strArrayResult[i].replaceAll("_", " ");
            			strArrayResult[i] = Character.toUpperCase(strArrayResult[i].charAt(0)) + strArrayResult[i].substring(1, strArrayResult[i].length());
            			BufferedWriter fw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));
            			fw.write("\n");
//            			a = Character.toUpperCase(a.charAt(0)) + a.substring(1, a.length());
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
    
    public static void DbData(int size){
    	
    	int counter = 1;
    	
    	String prefix = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
    			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>";
        String queryStr = prefix+"SELECT distinct ?type (STR(?l) AS ?label) {   ?type a owl:Class;  rdfs:label  ?l .   FILTER (LANG(?l) = \"en\") filter( regex( str(?type), \"^http://dbpedia.org/ontology/\" ) )}";
        Query query = QueryFactory.create(queryStr);
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
//            	System.out.println(textResult);
            	strArrayResult = textResult.split("\n");
            	
            	for(int i = 0; i < Math.min(size, strArrayResult.length); i++){
//            		System.out.println(strArrayResult[i]);
            		
            		
            		
            		if(strArrayResult[i].contains("\"")){
            			
            			String postfix = strArrayResult[i].substring(31, strArrayResult[i].indexOf(">"));
            			String label = DbDataHelper(postfix);
            			if(label != ""){
            				
            				
            				//write the class file
            				File file = new File("lib/data/" + counter);
                			file.createNewFile();
                			ArrayList<String> toWrite = new ArrayList<String>();
                			strArrayResult[i] = strArrayResult[i].substring(strArrayResult[i].indexOf("\"")+1, strArrayResult[i].lastIndexOf("\""));
                			strArrayResult[i] = strArrayResult[i].replaceAll("_", " ");
                			strArrayResult[i] = Character.toUpperCase(strArrayResult[i].charAt(0)) + strArrayResult[i].substring(1, strArrayResult[i].length());
                			toWrite.add("");
                			toWrite.add(strArrayResult[i]);
                			toWrite.add("class");
                        	toWrite.add(DbDataClassAbstractHelper(postfix));
                        	Files.write(file.toPath(), toWrite, Charset.forName("UTF-8"));
                        	//write the instance file
                        	toWrite.clear();
                        	file = new File("lib/data/" + ++counter);
                			file.createNewFile();
                			toWrite.add("");
                			toWrite.add(label);
                			Files.write(file.toPath(), toWrite, Charset.forName("UTF-8"));
                        	//increment the counter
                        	counter ++;
            			}
            			
            			
            			
            		} else {
            			size++;
            		}
        			
            	}
                
            }
        } catch (Exception e) {
        	
            e.printStackTrace();
        }

        System.out.println("Counter: " + --counter);
        return ;
    }
    
    private static String DbDataClassAbstractHelper(String uriPostfix){
    	
    	StringBuffer buffer = new StringBuffer(uriPostfix);
    	
    	for(int i = 1; i< uriPostfix.length(); i++){
    		if(Character.isUpperCase(uriPostfix.charAt(i))){
    			buffer.setCharAt(i, Character.toLowerCase(uriPostfix.charAt(i)));
    			buffer.insert(i++, '_');
    		}
    	}
    	uriPostfix = buffer.toString();
    	String prefix = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
    			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
    			+ "PREFIX dbo: <http://dbpedia.org/ontology/>"
    			+ "PREFIX dbr: <http://dbpedia.org/resource/>";
    	
    	String queryStr = prefix+"select distinct ?abstract where {dbr:"
        		+ uriPostfix
        		+ " dbo:abstract ?abstract FILTER (LANG(?abstract) = \"en\")} LIMIT 1";
//    	System.out.println(uriPostfix);
    	
    	
    	Query query = QueryFactory.create(queryStr);
    	
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query) ) {
        	 ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
             
             // Execute.
             ResultSet rs = qexec.execSelect();
             
             String textResult = "";
             String[] strArrayResult;
             
             if( rs.hasNext()  ){
             	textResult = ResultSetFormatter.asText(rs);
             	strArrayResult = textResult.split("\n");
//             	System.out.println(strArrayResult[3].substring(strArrayResult[3].indexOf("\"")+1, strArrayResult[3].lastIndexOf("\"")));
             	
             	return strArrayResult[3].substring(strArrayResult[3].indexOf("\"")+1, strArrayResult[3].lastIndexOf("\""));
             }
        	
        }catch(Exception e) {
        	
            e.printStackTrace();
        }
    	
    	return "";
    }
    
    private static String DbDataHelper(String uriPostfix){
    	
//    	System.out.println("we are at helper");
    	
    	String prefix = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
    			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
    			+ "PreFIX dbo: <http://dbpedia.org/ontology/>";
        String queryStr = prefix+"select distinct ?instance (STR(?l) AS ?label) ?abstract where {?instance a dbo:"
        		+ uriPostfix
        		+ "; rdfs:label  ?l;  dbo:abstract ?abstract  . FILTER (LANG(?l) = \"en\" && LANG(?abstract) = \"en\")} LIMIT 1";
        Query query = QueryFactory.create(queryStr);
    	
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query) ) {
        	 ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
             
             // Execute.
             ResultSet rs = qexec.execSelect();
             
             String textResult = "";
             String[] strArrayResult;
             
             if( rs.hasNext()  ){
             	textResult = ResultSetFormatter.asText(rs);
             	strArrayResult = textResult.split("\n");
             	StringBuffer temp = new StringBuffer(strArrayResult[3].substring(strArrayResult[3].indexOf("\"")+1, strArrayResult[3].lastIndexOf("\"")));
             	temp.replace(temp.indexOf("|")-2, temp.indexOf("|")+3, "\ninstance\n");
//             	System.out.println(temp);
             	
             	return temp.toString();
             }
        	
        }catch(Exception e) {
        	
            e.printStackTrace();
        }
    	
    	
    	return "";
    } 
}