import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;
 //treetagger
public class TaggerDemo {
    public static void main(String[] args){
    	String model = "lib\\models\\wsj-0-18-bidirectional-nodistsim.tagger";
    	MaxentTagger tagger = new MaxentTagger(model);
    	String testW = "book";
    	String test = "The Book is the most important thing here. However, we don't really need that book.";
    	System.out.println(tagger.tagTokenizedString(test));
    	if(!StringUtils.isAlphanumeric("--")){
			//System.out.println(content.get(i));
			System.out.println("notAlph");
		}
    	
    	if(test.indexOf(testW.toLowerCase())> -1 && test.indexOf(testW.toLowerCase())<test.indexOf(".")){
    		int start = test.indexOf(testW.toLowerCase());
        	int end = start + testW.length();
        	System.out.println(test.substring(start, end) + 1);
    	}
    	else if(test.indexOf(testW)> -1 && test.indexOf(testW)<test.indexOf(".")){
    		int start = test.indexOf(testW);
        	int end = start + testW.length();
        	System.out.println(test.substring(start, end) + 2);
    	}
    	
    	
    }
}