import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;
 //treetagger
public class TaggerDemo {
    public static void main(String[] args){
    	String model = "lib\\models\\english-bidirectional-distsim.tagger";
    	MaxentTagger tagger = new MaxentTagger(model);
    	String test = "Coach";
    	System.out.println(tagger.tagTokenizedString(test));
    	if(!StringUtils.isAlphanumeric("--")){
			//System.out.println(content.get(i));
			System.out.println("notAlph");
		}
    }
}