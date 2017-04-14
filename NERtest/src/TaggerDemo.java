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
    	String test = "book";
    	System.out.println(tagger.tagTokenizedString(test));
    	if(!StringUtils.isAlphanumeric("--")){
			//System.out.println(content.get(i));
			System.out.println("notAlph");
		}
    }
}