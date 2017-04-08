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
			 map.add(cell);
		 }
		 return map;
	}
	
	public static ArrayList<LinkedHashMap<String,LinkedHashSet<String>>> capitalization(List<String> text,ArrayList<LinkedHashMap<String,LinkedHashSet<String>>> map){
		System.out.println("-----------Cap check-----------");
		String model = "lib\\models\\english-bidirectional-distsim.tagger";
    	MaxentTagger tagger = new MaxentTagger(model);
		for(int i=0; i<text.size();i++){
			boolean flag = capCheck(text.get(i), tagger);
			if(flag){
				LinkedHashMap<String,LinkedHashSet<String>> cell = new LinkedHashMap<String,LinkedHashSet<String>>();
				LinkedHashSet<String> temp=new LinkedHashSet<String>();
				temp.add("true");
				cell.put("CAPITALIZATION", temp);
				map.add(i, cell);
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
	
	public static List<String> toString(ArrayList<LinkedHashMap<String,LinkedHashSet<String>>> map, List<String> content, List<String> titleStr, List<String> type){
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
		s.add("@attribute class {class, instance}\n");
		
		s.add("@data");
		for(int i=0;i<content.size();i++){
			LinkedHashMap<String,LinkedHashSet<String>> cur = map.get(i);
			//String nerFeatures = "'"+content.get(i).replaceAll("'", "\\\\'") + "',";
			String nerFeatures = "";
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
	 
	//Test case for using NER
	/*
	public static void main(String args[]){
		String content="Sachin Ramesh Tendulkar (Listeni/ˌsətʃɪn tɛnˈduːlkər/; Marathi: "
				 + " सचिन रमेश तेंडुलकर; born 24 April 1973) is an Indian former cricketer widely "
				 + " acknowledged as the greatest batsman of the modern generation, popularly holds the title \"God of Cricket\" among his fans [2] He is also acknowledged as the greatest cricketer of all time.[6][7][8][9] He took up cricket at the age of eleven, made his Test debut against Pakistan at the age of sixteen, and went on to represent Mumbai domestically and India internationally for close to twenty-four years. He is the only player to have scored one hundred international centuries, the first batsman to score a Double Century in a One Day International, and the only player to complete more than 30,000 runs in international cricket.[10] In October 2013, he became the 16th player and first Indian to aggregate "
				 + " 50,000 runs in all recognized cricket "
				 + " First-class, List A and Twenty20 combined)";
	 
		 // Here we can change the Classifier files to check different ClassLevel(3,4,7)
		 // Note that I already put all classifier files into the folder lib\classifiers
		 System.out.println(identifyNER(content, "lib\\classifiers\\english.muc.7class.distsim.crf.ser.gz").toString());
	 }
	 */
 
}