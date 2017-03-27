import edu.stanford.nlp.tagger.maxent.MaxentTagger;
 //treetagger
public class TaggerDemo {
    public static void main(String[] args){
    	MaxentTagger tagger = new MaxentTagger("lib\\taggers\\english-bidirectional-distsim.tagger");
    	String test = "I like this apple. How about you?";
    	System.out.println(tagger.tagString(test));
    }
}