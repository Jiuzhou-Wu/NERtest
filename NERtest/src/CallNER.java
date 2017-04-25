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

import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;


public class CallNER {
	static String tempPos;
	public static void main(String[] args) throws IOException, TreeTaggerException{
		ArrayList<String> content = new ArrayList<String>();
		List<String> typeList = new ArrayList<String>();
		List<String> abstracts = new ArrayList<String>();
		/*content.add("A B C");
		typeList.add("instance");
		abstracts.add("this is a test");*/
		try(Stream<Path> paths = Files.walk(Paths.get("lib\\data"))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            try {
						List<String> s = Files.readAllLines(filePath, Charset.forName("UTF-8"));
						//System.out.println(filePath);
						String title = s.get(1);
						String type = s.get(2);
						String abs = "EmptyAbs";
						if(s.get(3) != null){
							abs = s.get(3);
						}
						
						//title = title.substring(11, title.length()-8);
						content.add(title);
						//System.out.println(s.get(3));
						typeList.add(type);
						abstracts.add(abs);
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
    	plural = checkStructure(content, tagger, abstracts);
    	
    	List<String> dbFea = DbpediaData.dbFeature(content);
    	//System.out.println(content.size());
    	
    	List<String> pattern = patternCheck(abstracts,tagger);
    	
    	StanfordNER ner = new StanfordNER();
		String classifier = "lib\\classifiers\\english.muc.7class.distsim.crf.ser.gz";
		
		Path file = Paths.get("lib\\Result\\Res.arff");
		ArrayList<LinkedHashMap<String,LinkedHashSet<String>>> map = ner.identify(content, classifier);
		Files.write(file, ner.toString(map,content,plural,typeList,dbFea, pattern), Charset.forName("UTF-8"));
	}
	
	public static List<String> checkStructure(List<String> content, MaxentTagger tagger, List<String> absL) throws IOException, TreeTaggerException{
		List<String> res = new ArrayList<String>();
    	String temp;
    	
    	System.setProperty("treetagger.home", "C:\\tree-tagger-windows-3.2\\TreeTagger");
		TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
		tt.setModel("lib\\english-utf8.par:utf-8");

    	for(int i=0;i<content.size();i++){
    		temp = "";
    		// flags for different structure features
    		//below is the link for the tags list
    		//https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
    		boolean plural = false;
    		boolean notAlph = false;
    		boolean properNoun = false;
    		/*boolean commonNoun = false;*/
    		
    		
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
    		
    		//check single proper word
    		if(words.length == 1){
    			String testW = content.get(i);
    			String test = absL.get(i);
    			String word ="";
    			if(test.indexOf(testW.toLowerCase())> -1 && test.indexOf(testW.toLowerCase())<test.indexOf(".")){
    	    		int start = test.indexOf(testW.toLowerCase());
    	        	int end = start + testW.length();
    	        	word = test.substring(start, end);
    	    	}
    	    	else if(test.indexOf(testW)> -1 && test.indexOf(testW)<test.indexOf(".")){
    	    		int start = test.indexOf(testW);
    	        	int end = start + testW.length();
    	        	word = test.substring(start, end);
    	    	}
    	    	else{
    	    		word = "0";
    	    	}
    			
    			
    			tt.setHandler(new TokenHandler<String>(){
    				public void token(String token, String pos, String lemma)
    				{
    					tempPos = pos;
    				}
    			});
    			tt.process(new String[] {word});
    			//System.out.println(content.get(i) + "     " + word + "     "  + tempPos);
    			if(tempPos.equals("NP")|| tempPos.equals("NPS") ){
    				properNoun = true;
    			}else{
    				properNoun = false;
    			}
    		/*	if(tempPos.equals("NN")||tempPos.equals("NNS")){
    				commonNoun = true;
    			}else{
    				commonNoun = false;
    			}*/
    		}
    
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
    		/*if(commonNoun){
    			features = features + "1,";
    		}
    		else{
    			features = features + "0,";
    		}*/
    		
    		//features = features + absL.get(i);
    		
    		res.add(features);
    	}
    	
		return res;
	}
	
	public static List<String> patternCheck(List<String> absL, MaxentTagger tagger){
		List<String> pat = new ArrayList<String>();
		for(int i=0;i<absL.size();i++){
    		String abs = absL.get(i);
    		String res = "";
    		String pattern = "";
    		int end = abs.indexOf(".");
    		if(end >0){
    			abs = abs.substring(0, end);
    			//System.out.println(abs);
    			String[] words = tagger.tagString(abs).split("\\s+");
    			for(int j=0;j<words.length;j++){
    				String temp = words[j];
    				temp = temp.substring(temp.lastIndexOf("_")+1, temp.length());
    				res = res + temp + " ";
    			}
    		}
    		else{
    			//System.out.println(abs);
    			String[] words = tagger.tagString(abs).split("\\s+");
    			for(int j=0;j<words.length;j++){
    				String temp = words[j];
    				//System.out.println(temp);
    				temp = temp.substring(temp.lastIndexOf("_")+1, temp.length());
    				res = res + temp + " ";
    			}
    		}
    		
    		if(res.startsWith("NNP NNP") || res.startsWith("NNP NNP NNP") || res.startsWith("DT NNP NNP") || res.startsWith("DT NN NNP")){
    			pattern = pattern + "1,";
    		}
    		else{
    			pattern = pattern + "0,";
    		}
    		
    		if(res.startsWith("DT NN VBZ")){
    			pattern = pattern + "1,";
    		}
    		else{
    			pattern = pattern + "0,";
    		}
    		
    		pat.add(pattern);
    	}
		return pat;
	}
}
