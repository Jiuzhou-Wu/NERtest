import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CallNER {

	public static void main(String[] args) throws IOException{
		List<String> content = new ArrayList<String>();
		try(Stream<Path> paths = Files.walk(Paths.get("lib\\WekiPages"))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            try {
						List<String> s = Files.readAllLines(filePath);
						String title = s.get(1);
						title = title.substring(11, title.length()-8);
						content.add(title);
						//System.out.println(s.get(1));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    });
		}
		
		StanfordNER ner = new StanfordNER();
		/*
		String[] content = new String[2]; 
		content[0]="Sachin Ramesh Tendulkar (Listeni/ˌsətʃɪn tɛnˈduːlkər/; Marathi: "
				+ " सचिन रमेश तेंडुलकर; born 24 April 1973) is an Indian former cricketer widely "
				+ " acknowledged as the greatest batsman of the modern generation, popularly holds the title \"God of Cricket\" among his fans [2] He is also acknowledged as the greatest cricketer of all time.[6][7][8][9] He took up cricket at the age of eleven, made his Test debut against Pakistan at the age of sixteen, and went on to represent Mumbai domestically and India internationally for close to twenty-four years. He is the only player to have scored one hundred international centuries, the first batsman to score a Double Century in a One Day International, and the only player to complete more than 30,000 runs in international cricket.[10] In October 2013, he became the 16th player and first Indian to aggregate "
				+ " 50,000 runs in all recognized cricket "
				+ " First-class, List A and Twenty20 combined)";
		content[1]="I am a scientist";
		*/
		String model = "lib\\classifiers\\english.muc.7class.distsim.crf.ser.gz";
		//String model2 = "lib\\classifiers\\english.conll.4class.distsim.crf.ser.gz";
		//String model3 = "lib\\classifiers\\english.all.3class.distsim.crf.ser.gz";
		//System.out.println(ner.toString(ner.identify(content, model),content));
		
		//List<String> lines = Arrays.asList("The first line", "The second line");
		Path file = Paths.get("lib\\Result\\Res.arff");
		Files.write(file, ner.toString(ner.identify(content, model),content), Charset.forName("UTF-8"));
		//Path file2 = Paths.get("lib\\Result\\4ClassRes.txt");
		//Files.write(file2, ner.toString(ner.identify(content, model2),content), Charset.forName("UTF-8"));
		//Path file3 = Paths.get("lib\\Result\\3ClassRes.txt");
		//Files.write(file3, ner.toString(ner.identify(content, model3),content), Charset.forName("UTF-8"));
		
		
	}
}
