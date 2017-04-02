import edu.stanford.nlp.tagger.maxent.MaxentTagger;
 //treetagger
public class TaggerDemo {
    public static void main(String[] args){
    	String model = "lib\\models\\english-bidirectional-distsim.tagger";
    	MaxentTagger tagger = new MaxentTagger(model);
    	String test = "Arc de Triomphe";
    	System.out.println(tagger.tagTokenizedString(test));
    }
}