
public class CallNER {
	public static void main(String[] args){
		StanfordNER ner = new StanfordNER();
		String[] content = new String[2]; 
		content[0]="Sachin Ramesh Tendulkar (Listeni/ˌsətʃɪn tɛnˈduːlkər/; Marathi: "
				+ " सचिन रमेश तेंडुलकर; born 24 April 1973) is an Indian former cricketer widely "
				+ " acknowledged as the greatest batsman of the modern generation, popularly holds the title \"God of Cricket\" among his fans [2] He is also acknowledged as the greatest cricketer of all time.[6][7][8][9] He took up cricket at the age of eleven, made his Test debut against Pakistan at the age of sixteen, and went on to represent Mumbai domestically and India internationally for close to twenty-four years. He is the only player to have scored one hundred international centuries, the first batsman to score a Double Century in a One Day International, and the only player to complete more than 30,000 runs in international cricket.[10] In October 2013, he became the 16th player and first Indian to aggregate "
				+ " 50,000 runs in all recognized cricket "
				+ " First-class, List A and Twenty20 combined)";
		content[1]="I am a scientist";
		
		String model = "lib\\classifiers\\english.muc.7class.distsim.crf.ser.gz";
		System.out.println(ner.toString(ner.identify(content, model),content));

	}
}
