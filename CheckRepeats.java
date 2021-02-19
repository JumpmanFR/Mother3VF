/* CheckOverflow.java
 */
import java.util.*;
import java.io.*;
import java.lang.*;

public class CheckRepeats
{
	static HashMap<String, String> blocks;
	static HashMap<String, String[]> repeats;
    
    static boolean hotspringFlag = false;
	
	public static void main(String[] args) {
		blocks = new HashMap<String, String>();
		repeats = new HashMap<String, String[]>();
	
		int dialogLines = 0;
        int hotspringLines = 0;
        int emptyLines = 0;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF8"))) {
			String line;
			while ((line = br.readLine()) != null) {
			   int result = checkLine(line);
			   if (result == 2) {
                   dialogLines ++;
			   } else if (result == 3) {
                   hotspringLines ++;
               } else if (result == 4) {
                   emptyLines ++;
               }
			}
		} catch (IOException e) {
			System.out.println("FILE ERROR");
		}
		System.out.println("FIN PARSING");
        
        
		int repeatedLines = 0;
        int badRepeats = 0;
		Iterator it = repeats.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			String repeatKey = (String) pair.getKey();
			String[] repeatBlocksIds = (String[]) pair.getValue();
            repeatedLines += repeatBlocksIds.length;
			//System.out.println("=== " + repeatKey + " ===");
			for (String repeatBlockId: repeatBlocksIds) {
				//System.out.println(repeatBlockId);
				if (!blocks.get(repeatBlockId + "E").equals(blocks.get(repeatKey + "E"))
				&& !blocks.get(repeatBlockId + "E").contains("[HOTSPRING")) {
					System.out.println(repeatKey + ": " + blocks.get(repeatKey + "E"));
					System.out.println(repeatBlockId + ": " + blocks.get(repeatBlockId + "E"));
                    badRepeats++;
				}
			}
		}

        System.out.println("");
        System.out.println("FIN");
        System.out.println("Nombre de repeats incorrects : " + badRepeats);
        System.out.println("");
        System.out.println("Nombre de lignes : " + blocks.size());
        System.out.println("Nombre de lignes non vides : " + (dialogLines + hotspringLines));
        System.out.println("Nombre de dialogues : " + dialogLines);
        System.out.println("Nombre de dialogues répétés : " + repeats.size());
        System.out.println("Nombre de répétitions de dialogues : " + repeatedLines);
        System.out.println("Nombre de dialogues uniques : " + (dialogLines - repeatedLines + repeats.size()));
    }
	
	private static int checkLine(String line) {
        if (hotspringFlag) {
            hotspringFlag = false;
            return 0;
        }
        
		line = line.trim();

		if ("".equals(line)) {
			return 0;
		}
		
		if (line.toUpperCase().matches("\\[CONTAINER [0-9]+\\]")) {
			return 0;
		}
		
		if (line.startsWith("// Repeats: ")) {
			//System.out.println("Repeat");
			String repeatString = line.substring(12);
			//System.out.println(repeatString);
			String[] repeatArray = repeatString.split(", ");
			repeats.put(repeatArray[0], repeatArray);
			return 1;
		}
		
		int start = line.indexOf(":");
		int end = line.indexOf("[END]");
		
		if (start == -1 || end == -1 || start >= end) {
			return 0;
		}

		String lineId = line.substring(0, start);

		line = line.substring(start + 1, end);
        
		if (lineId.endsWith("E")) {
			//System.out.println("Line " + lineId);
			blocks.put(lineId, line);
            if (line.equals("")) {
                return 4;
            } else if (line.contains("HOTSPRING")) {
                hotspringFlag = true;
                return 3;
            }
			return 2;
		}
		
		return 0;	

	}

}