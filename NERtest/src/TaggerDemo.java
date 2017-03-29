import edu.stanford.nlp.tagger.maxent.MaxentTagger;
 //treetagger
public class TaggerDemo {
    public static void main(String[] args){
    	String model = "lib\\models\\english-bidirectional-distsim.tagger";
    	MaxentTagger tagger = new MaxentTagger(model);
    	String test = "apple";
    	System.out.println(tagger.tagTokenizedString(test).substring(test.length()+1, tagger.tagTokenizedString(test).length()));
    }
}