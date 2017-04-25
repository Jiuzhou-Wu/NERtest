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
    	
//    	dbFeatureSingle("`Abdu'l-Bah¨¢");
    	
//    	testData();

//    	DbData(1000);
    	Test.test();
    	
    	
    	
    }
    
    public static boolean isInstanceDefinedUnderOntology(String title){
    	
    	String totalCount = "0";
		String queryStr = "select distinct ?class where { <http://dbpedia.org/resource/"
				+ title
				+ "> a ?class filter( regex( str(?class), \"^http://dbpedia.org/ontology/\" ) )} LIMIT 100";
//		System.out.println(queryStr);
		try{
			Query query = QueryFactory.create(queryStr);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;

            // Execute.
            ResultSet rs = qexec.execSelect();
//            
//            String textResult = "";
//            String[] strArrayResult;
            
            if(rs.hasNext())
            {
            	
//            	textResult = ResultSetFormatter.asText(rs);
//            	System.out.println(textResult);
//            	strArrayResult = textResult.split("\n");
            	
//            	for(int line = 0; line < strArrayResult.length; line++){
//            		System.out.println(strArrayResult[line]);
//            	}
//            	System.out.println(i+" "+Integer.parseInt(strArrayResult[3].substring(strArrayResult[3].indexOf("|")+1, strArrayResult[3].lastIndexOf("|")).replaceAll(" ", "")));
//            	totalCount = strArrayResult[3].substring(strArrayResult[3].indexOf("|")+1, strArrayResult[3].lastIndexOf("|")).replaceAll(" ", "");
            	return true;
            }
//            resultList.add(i, totalCount);
		} catch (Exception e) {
        	
            e.printStackTrace();
        }
//		return totalCount;
    	
    	
    	return false;
    }
    
    public static boolean isOntologyClass(String title){
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
        	return false;
//        	e.printStackTrace();
//            throw new Exception();
        }
        

        // Remote execution.
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query) ) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;

            // Execute.
            ResultSet rs = qexec.execSelect();
//            boolean isClass = false;
            if( rs.hasNext()  ){
            	//this is a class
//            	System.out.println(ResultSetFormatter.asText(rs));
//            	RDFNode n = rs.next().get("label");
            	return true;
            	
            }else{
            	return false;
            }
            
        } catch (Exception e) {
        	System.out.println("here");
        	System.out.println("The last query tried with the: " + title);
            e.printStackTrace();
        }
        return false;
    } 
    
    
    public static void testData() throws IOException{
    	System.out.println("something");
    	int size = 10000;
    	List<String> content = new ArrayList<String>();
    	try(Stream<Path> paths = Files.walk(Paths.get("lib/WekiPages"))) {
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
    	int numOfInstance = 0;
    	for(int i = 0; i < Math.min(size, content.size()); i++){
    		
    		String title = content.get(i);
    		
//    		String result = DbData(title);
//    		title = title.replaceAll("_", " ");
//    		System.out.println(result);
    		ArrayList<String> toWrite;
      	    if(!isOntologyClass(title)){
      	    	
      	    	if(Integer.parseInt(dbFeatureSingle(title)) > 0){
      	    		//this is a class for sure!!!!!!!!!
      	    		File file = new File("lib/data/c_" + numOfClass++);
      	    		toWrite = new ArrayList<String>();
        			toWrite.add("");
        			toWrite.add(title.replace('_', ' '));
        			
        			toWrite.add("class");
                	toWrite.add(DbDataClassAbstractHelper(title, false));
                	Files.write(file.toPath(), toWrite, Charset.forName("UTF-8"));
                	toWrite.clear();
      	    	} else if(isInstanceDefinedUnderOntology(title)){
      	    		File file = new File("lib/data/i_" + numOfInstance++);
      	    		toWrite = new ArrayList<String>();
        			toWrite.add("");
        			toWrite.add(title.replace('_', ' '));
        			toWrite.add("instance");
                	toWrite.add(DbDataClassAbstractHelper(title, false));
                	Files.write(file.toPath(), toWrite, Charset.forName("UTF-8"));
                	toWrite.clear();
      	    	}
//      	    	System.out.println("" + i + "/" + Math.min(size, content.size()) + " done; numOfClass:    " + numOfClass);
//      	    	System.out.println("" + i + "/" + Math.min(size, content.size()) + " done; numOfInstance: " + numOfInstance);
      	    } 
      	    
        	
    	}
    	System.out.println("im just not happy");
    }
    
    
    
    
    public static ArrayList<String> dbFeature(ArrayList<String> titles){
    	
    	ArrayList<String> resultList = new ArrayList<String>(titles.size());
//    	String totalCount = "0";
    	
    	for(int i = 0; i < titles.size(); i++){
    		System.out.println(titles.get(i));
    	}
    	
    	for(int i = 0; i<titles.size(); i++){
    		for(int j = 0; j<titles.get(i).length(); j++){
    			if(titles.get(i).charAt(j)=='\"'){
    				
				
    			}
    		}
    		titles.set(i, titles.get(i).replace('\"', ' '));
    		resultList.add(i, dbFeatureSingle(titles.get(i).replace(' ', '_')));
    	}
    	
    	for(int i = 0; i < titles.size(); i++){
    		System.out.println(titles.get(i));
    	}
    	
    	return resultList;
    }
    
    public static String dbFeatureSingle(String title){
//    	String title = titles.get(i);
    	String totalCount = "0";
    	

		
		String prefix = "PREFIX dbo: <http://dbpedia.org/ontology/>"
				+ "PREFIX dbr: <http://dbpedia.org/resource/>";
		String queryStr = prefix + "select ( count ( distinct ?instance ) AS ?total ) where {?instance dbo:type <http://dbpedia.org/resource/"
				+ title
				+ "> } LIMIT 100";
//		System.out.println(queryStr);
		try{
			Query query = QueryFactory.create(queryStr);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;

            // Execute.
            ResultSet rs = qexec.execSelect();
            
            String textResult = "";
            String[] strArrayResult;
            
            if(rs.hasNext())
            {
            	
            	textResult = ResultSetFormatter.asText(rs);
//            	System.out.println(textResult);
            	strArrayResult = textResult.split("\n");
            	
//            	for(int line = 0; line < strArrayResult.length; line++){
//            		System.out.println(strArrayResult[line]);
//            	}
//            	System.out.println(i+" "+Integer.parseInt(strArrayResult[3].substring(strArrayResult[3].indexOf("|")+1, strArrayResult[3].lastIndexOf("|")).replaceAll(" ", "")));
            	totalCount = strArrayResult[3].substring(strArrayResult[3].indexOf("|")+1, strArrayResult[3].lastIndexOf("|")).replaceAll(" ", "");
            }
//            resultList.add(i, totalCount);
		} catch (Exception e) {
        	
            e.printStackTrace();
        }
		return totalCount;
    }
    
    
    
    /**
     * This function was made for get the test data set from the dbpedia. 
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
                        	toWrite.add(DbDataClassAbstractHelper(postfix, true));
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
    
    private static String DbDataClassAbstractHelper(String uriPostfix, boolean fromDbData){
    	
    	StringBuffer buffer = new StringBuffer(uriPostfix);
    	
    	for(int i = 1; i< uriPostfix.length() && fromDbData; i++){
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
    	
    	String queryStr = prefix+"select distinct ?abstract where {<http://dbpedia.org/resource/"
        		+ uriPostfix
        		+ "> dbo:abstract ?abstract FILTER (LANG(?abstract) = \"en\")} LIMIT 1";

        try {
        	
        	Query query = QueryFactory.create(queryStr);
        	QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
        	
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
        	System.out.println("The last query tried: " + queryStr);
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