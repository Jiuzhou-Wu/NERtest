import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;


public class CallNER {

	public static void main(String[] args) throws IOException{
		List<String> content = new ArrayList<String>();
		List<String> typeList = new ArrayList<String>();
		
		try(Stream<Path> paths = Files.walk(Paths.get("lib\\data"))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            try {
						List<String> s = Files.readAllLines(filePath, Charset.forName("UTF-8"));
						//System.out.println(filePath);
						String title = s.get(1);
						String type = s.get(2);
						//title = title.substring(11, title.length()-8);
						content.add(title);
						//System.out.println(s.get(1));
						typeList.add(type);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    });
		}
		
		String model = "lib\\models\\wsj-0-18-bidirectional-nodistsim.tagger";
    	MaxentTagger tagger = new MaxentTagger(model);
    	
    	List<String> plural = new ArrayList<String>();
    	plural = checkStructure(content, tagger);
    	
		StanfordNER ner = new StanfordNER();
		String classifier = "lib\\classifiers\\english.muc.7class.distsim.crf.ser.gz";
		
		Path file = Paths.get("lib\\Result\\Res.arff");
		ArrayList<LinkedHashMap<String,LinkedHashSet<String>>> map = ner.identify(content, classifier);
		Files.write(file, ner.toString(map,content,plural,typeList), Charset.forName("UTF-8"));
	}
	
	public static List<String> checkStructure(List<String> content, MaxentTagger tagger){
		List<String> res = new ArrayList<String>();
    	String temp;
    	for(int i=0;i<content.size();i++){
    		temp = "";
    		// flags for different structure features
    		//below is the link for the tags list
    		//https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
    		boolean plural = false;
    		boolean notAlph = false;
    		boolean properNoun = false;
    		
    		
    		// assign values for flags
    		temp = tagger.tagTokenizedString(content.get(i));
    		String[] words = temp.split("\\s+");
    		if(!StringUtils.isAlphanumeric(content.get(i))){
    			//System.out.println(content.get(i));
    			notAlph = true;
    		}
    		else if(temp.contains("CD")){
    			notAlph = true;
    		}
    		
    		/*if(words.length == 1){
    			temp = words[0].substring(words[0].lastIndexOf("_")+1, words[0].length());
    			if(temp.equals("NNP")|| temp.equals("NNPS")){
    				properNoun = true;
    			}
    		}*/
    
    		if(temp.contains("IN")){
    			for (int j = 0; j < words.length-1; j++) {
        		    temp = words[j+1].substring(words[j+1].lastIndexOf("_")+1, words[j+1].length());
        		    if(temp.equals("IN")){
        		    	temp = words[j].substring(words[j].lastIndexOf("_")+1, words[j].length());
        		    	if(temp.equals("NNS")|| temp.equals("NNPS")){
            		    	plural = true;
            		    }
        		    	break;
        		    } 		   
        		}
    		}
    		else{
    			temp = words[words.length-1].substring(words[words.length-1].lastIndexOf("_")+1, words[words.length-1].length());
    			if(temp.equals("NNS")|| temp.equals("NNPS")){
    		    	plural = true;
    		    }
    		}
    		
    		
    		
    		//depends on flags
    		//adding features true 1 false 0 for each
    		String features = "";
    		if(plural){
    			features = features + "1,";
    		}
    		else{
    			features = features + "0,";
    		}
    		if(notAlph){
    			features = features + "1,";
    		}
    		else{
    			features = features + "0,";
    		}
    		if(properNoun){
    			features = features + "1,";
    		}
    		else{
    			features = features + "0,";
    		}
    		
    		res.add(features);
    	}
    	
		return res;
	}

}
