import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Test {

	public static void test() throws IOException {
		//调用data folder 里面的三行数据
		ArrayList<String> content = new ArrayList<String>();
		List<String> typeList = new ArrayList<String>();
		List<String> abstracts = new ArrayList<String>();
		
		try(Stream<Path> paths = Files.walk(Paths.get("lib\\data"))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            try {
						List<String> s = Files.readAllLines(filePath, Charset.forName("UTF-8"));
						System.out.println(filePath);
						String title = s.get(1);
						String type = s.get(2);
						String abs = "EmptyAbs";
						if(s.get(3) != null){
							abs = s.get(3);
						}
						
						//title = title.substring(11, title.length()-8);
						content.add(title);
						//System.out.println(s.get(3));
						typeList.add(filePath.toString());
						abstracts.add(abs);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    });
		}
//		System.out.println("the one have problem: "+typeList.get(395));
		ArrayList<String> dbfeatures = DbpediaData.dbFeature(content);
		Path file = Paths.get("lib\\Result\\test");
		Files.write(file, dbfeatures, Charset.forName("UTF-8"));
	}

}
