import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *------------------------------|
 * Standford Named Entity Demo  |
 * @author Ganesh               |
 *------------------------------|
 *modified by Jiuzhou Wu, Haolong Zhang
 */

public class StanfordNER
{
	 /**
	 * identify Name,organization location etc entities and return Map<List>
	 * @param text -- data
	 * @param model - Stanford model names out of the three models
	 * @return
	 */
	
	
	 public static LinkedHashMap <String,LinkedHashSet<String>> identifyNER(String text,String model){
		 
		 LinkedHashMap <String,LinkedHashSet<String>> map=new <String,LinkedHashSet<String>>LinkedHashMap();
		 String serializedClassifier =model;
		 System.out.println(serializedClassifier);
		 System.out.println("-----------check check-------------");
		 CRFClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
		 List<List<CoreLabel>> classify = classifier.classify(text);
	
		 for (List<CoreLabel> coreLabels : classify){
			 for (CoreLabel coreLabel : coreLabels){
				 String word = coreLabel.word();
				 String category = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
				 
				 if(!"O".equals(category)){
					 if(map.containsKey(category)){
						 // key is already their just insert in arraylist
						 map.get(category).add(word);
						 }
					 else{
						 LinkedHashSet<String> temp=new LinkedHashSet<String>();
						 temp.add(word);
						 map.put(category,temp);
					 }
					 //Print details for each single content
					 //System.out.println(word+":"+category);
				 }
			 }
		 }
		
		 return map;
	}
	 
	 public static ArrayList<LinkedHashMap<String,LinkedHashSet<String>>> identify(List<String> text,String model){
		 
		 ArrayList<LinkedHashMap<String,LinkedHashSet<String>>> map =new ArrayList<LinkedHashMap<String,LinkedHashSet<String>>>();
		 String serializedClassifier =model;
		 System.out.println(serializedClassifier);
		 System.out.println("-----------check check-------------");
		 CRFClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
		 for(int i=0;i<text.size();i++){
			 List<List<CoreLabel>> classify = classifier.classify(text.get(i));
			 
			 LinkedHashMap<String,LinkedHashSet<String>> cell = new LinkedHashMap<String,LinkedHashSet<String>>();
			 for (List<CoreLabel> coreLabels : classify){
				 for (CoreLabel coreLabel : coreLabels){
					 String word = coreLabel.word();
					 String category = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
					 
					 if(!"O".equals(category)){
						 if(cell.containsKey(category)){
							 // key is already their just insert in arraylist
							 cell.get(category).add(word);
							 }
						 else{
							 LinkedHashSet<String> temp=new LinkedHashSet<String>();
							 temp.add(word);
							 cell.put(category,temp);
						 }
						 //Print details for each single content
						 //System.out.println(word+":"+category);
					 }
				 }
			 }
			 map.add(i,cell);
		 }
		 return map;
	}
	
	public static ArrayList<LinkedHashMap<String,LinkedHashSet<String>>> capitalization(List<String> text,ArrayList<LinkedHashMap<String,LinkedHashSet<String>>> map){
		System.out.println("-----------Cap check-----------");
		String model = "lib\\models\\english-bidirectional-distsim.tagger";
    	MaxentTagger tagger = new MaxentTagger(model);
		for(int i=0; i<text.size();i++){
			boolean flag = capCheck(text.get(i), tagger);
			LinkedHashMap<String,LinkedHashSet<String>> cell = new LinkedHashMap<String,LinkedHashSet<String>>();
			cell = map.get(i);
			if(flag){
				LinkedHashSet<String> temp=new LinkedHashSet<String>();
				temp.add("true");
				cell.put("CAPITALIZATION", temp);
			}
		}
		return map;
	}
	
	public static boolean capCheck(String text, MaxentTagger tagger){
		String[] words = text.split("\\s+");
		for (int i = 0; i < words.length; i++) {
		    // You may want to check for a non-word character before blindly
		    // performing a replacement
		    // It may also be necessary to adjust the character class
		    words[i] = words[i].replaceAll("[^\\w]", "");
		    if(!words[i].equals("")&&!Character.isUpperCase(words[i].charAt(0))){
		    	String type = tagger.tagTokenizedString(words[i]);
		    	if(!type.contains("IN")){
		    		return false;
		    	}
		    }
		}
		
		if(words.length>1){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public static List<String> toString(ArrayList<LinkedHashMap<String,LinkedHashSet<String>>> map, List<String> content, List<String> titleStr, List<String> type, List<String> dbFeatures){
		map = capitalization(content,map);
		List<String> s = new ArrayList<String>();
		s.add("@relation wekipagesClassify\n");
		//s.add("@attribute title string");
		s.add("@attribute location numeric");
		s.add("@attribute person numeric");
		s.add("@attribute organization numeric");
		s.add("@attribute money numeric");
		s.add("@attribute percent numeric");
		s.add("@attribute date numeric");
		s.add("@attribute time numeric");
		s.add("@attribute capitalization numeric");
		s.add("@attribute plural numeric");
		s.add("@attribute notAlpha numeric");
		s.add("@attribute properNoun numeric");
		s.add("@attribute dbFeature numeric");
		s.add("@attribute class {class, instance}\n");
		
		s.add("@data");
		
		for(int i=0;i<content.size();i++){
			LinkedHashMap<String,LinkedHashSet<String>> cur = map.get(i);
			String nerFeatures = "'"+content.get(i).replaceAll("'", "\\\\'") + "',";
			//String nerFeatures = "";
			//boolean flag = false;
			if(cur.containsKey("LOCATION")){
				nerFeatures = nerFeatures + "1,";
				//flag = true;
			}
			else{
				nerFeatures = nerFeatures + "0,";
			}
				
			if(cur.containsKey("PERSON")){
				nerFeatures = nerFeatures + "1,";				
				//flag = true;
			}
			else{
				nerFeatures = nerFeatures + "0,";
			}
				
			if(cur.containsKey("ORGANIZATION")){
				nerFeatures = nerFeatures + "1,";
				//flag = true;
			}
			else{
				nerFeatures = nerFeatures + "0,";
			}
				
			if(cur.containsKey("MONEY")){
				nerFeatures = nerFeatures + "1,";
				//flag = true;
			}
			else{
				nerFeatures = nerFeatures + "0,";
			}
			
			if(cur.containsKey("PERCENT")){
				nerFeatures = nerFeatures + "1,";
				//flag = true;
			}
			else{
				nerFeatures = nerFeatures + "0,";
			}
				
			if(cur.containsKey("DATE")){
				nerFeatures = nerFeatures + "1,";
				//flag = true;
			}
			else{
				nerFeatures = nerFeatures + "0,";
			}
			
			if(cur.containsKey("TIME")){
				nerFeatures = nerFeatures + "1,";
				//flag = true;
			}
			else{
				nerFeatures = nerFeatures + "0,";
			}
				
			if(cur.containsKey("CAPITALIZATION")){
				nerFeatures = nerFeatures + "1,";
				//flag = true;
			}
			else{
				nerFeatures = nerFeatures + "0,";
			}
			
			nerFeatures = nerFeatures + titleStr.get(i);
			
			//nerFeatures = nerFeatures + dbFeatures.get(i);
			
			nerFeatures = nerFeatures + type.get(i);
			/*
			if(flag){
				nerFeatures = nerFeatures + "Instance";
			}
			else{
				nerFeatures = nerFeatures + "Class";
			}*/
			
			s.add(nerFeatures);
		}
		return s;
	}
 
}