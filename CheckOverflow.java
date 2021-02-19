/* CheckOverflow.java
 */
import java.util.*;
import java.io.*;
import java.lang.*;

public class CheckOverflow
{
	static Map<String, Integer> normalWidths;
	static Map<String, Integer> saturnWidths;
	static Map<String, String> charTags;
	static Map<String, String> items;
	
	static final int DEFAULT_CHAR_LENGTH = 10;
	static final int MAX_LINE_LENGTH = 210;
    
	static int TOO_SHORT_MODE_MIN_LINE_LENGTH = 170;
    
	static boolean checkTooShortMode = false;
    
	public static void main(String[] args) {
		fillWidths();
		fillCharTags();
		fillItems();
		
		int nbLines = 0;
		int nbDone = 0;
			
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF8"))) {
			checkTooShortMode = (args.length > 1 && "1".equals(args[1]));
			String line;
			while ((line = br.readLine()) != null) {
			   int result = checkLine(line);
			   if (result != 0) {
			   		nbLines++;
			   }
			   if (result == 1) {
			   		nbDone++;
			   }
			}
		} catch (IOException e) {
			System.out.println("FILE ERROR");
			System.exit(-1);
		}
		System.out.println("Nombre de lignes : " + nbLines);
		System.out.println("Effectué : " + nbDone);
		System.out.println("Progression : " + (nbDone * 100.0 / nbLines) + "%");
		
	}
	
	/**
	0 : ligne vide, ou en version anglaise, ou qui ne compte pas
	-1 : ligne non traitée ou non terminée
	1 : ligne terminée
	*/
	private static int checkLine(String line) {
		line = line.trim();
		
		if ("".equals(line) || line.startsWith("//")) {
			return 0;
		}
		
		if (line.toUpperCase().matches("\\[CONTAINER [0-9]+\\]")) {
			return 0;
		}
		
		int start = line.indexOf(":");
		int end = line.indexOf("[END]");
		
		if (start == -1 || end == -1 || start >= end) {
			System.out.println("\":\" OU \"[END]\" MANQUANT : " + line);
			/*try {
				System.in.read();
			} catch (IOException e) {}*/
			return -1;
		}
		
		String lineId = line.substring(0, start);
				
		line = line.substring(start + 1, end);
		
		if (line.matches("^.*\\[ITEM ([0-9A-F]+) [0-9A-F]+\\]?.*$")) {
		    String itemId = line.replaceAll("^.*\\[ITEM ([0-9A-F]+) [0-9A-F]+\\]?.*$", "$1");
			String item;
			if (items.containsKey(itemId)) {
				item = items.get(itemId);
			} else {
				item = "WWWWWWWWWWWWWWWW";
			}
    		line = line.replaceAll("\\[ITEM ([0-9A-F]+) [0-9A-F]+\\]", item);
		}
		line = line.replaceAll("\"(.*)\"", "<$1>");
		line = line.replaceAll("\"", "<");
		line = line.replaceAll("\\[ENEMYNAME [0-9A-F]+ [0-9A-F]+\\]", "WWWWWWWWWWWW");
		line = line.replaceAll("\\[SOUND [0-9A-F]+ [0-9A-F]+\\]", "");
		line = line.replaceAll("\\[HOTSPRING [0-9A-F]+ [0-9A-F]+\\]", "");
		line = line.replaceAll("\\[PAUSE [0-9A-F]+ [0-9A-F]+\\]", "");
		line = line.replaceAll("\\[COLOR [0-9A-F]+ [0-9A-F]+\\]", "");
		line = line.replaceAll("\\[[0-9A-F][0-9A-F] [0-9A-F][0-9A-F] [0-9A-F][0-9A-F] [0-9A-F][0-9A-F]\\]", "WWWWWWWW");
		line = line.replaceAll("\\[EF E[0-9A-F]\\]", "e•");
		line = line.replaceAll("\\[[0-9A-F][0-9A-F] [0-9A-F][0-9A-F]\\]", "00");
		line = line.replaceAll("\\[[A-Z]+ [0-9A-F]+ [0-9A-F]+ [0-9A-F]+\\]", "WWWWWWWW");
		line = line.replaceAll("\\[[A-Z]+ [0-9A-F]+ [0-9A-F]+ [0-9A-F]+ [0-9A-F]+ [0-9A-F]+\\]", "WWWWWWWW");
		line = line.replaceAll("\\[FAVFOOD\\]", "WWWWWWWWW");
		line = line.replaceAll("\\[FAVTHING\\]", "WWWWWWWW");
		line = line.replaceAll("\\[MENU [0-9A-F][0-9A-F] [0-9A-F][0-9A-F]\\]", "µ");
		line = line.replaceAll("\\[ENDCHOICES]", "");
		line = line.replaceAll("\\[CENTER\\]", "");
		line = line.replaceAll("\\[EVENT\\]", "");
		line = getFormattedString(line);
		
		String[] sublines = line.split("\\[WAIT\\]");
        
        for (int k = 0; k < sublines.length; k++) {
            String subline = sublines[k];
            String[] subsublines = subline.split("\\[BREAK\\]");
            
            boolean doCheckTooShort = subline.startsWith("@"); // filter out Hinawa’s letter and such
            int previousSubsublineWidth = 0;
            String firstWordInSubsubline = "";
            int firstWordInSubsublineWidth = 0;
                    
            for (int i = 0; i < subsublines.length; i++) {
                int width = 0;
                String subsubline = subsublines[i];
                String[] spans = subsubline.split("\\[ALTERNATEFONT\\]");
                for (int j = 0; j < spans.length; j++) {
                    width += getStringWidth(spans[j], (j % 2) == 1);
                }
                if (spans.length > 0) {
                    firstWordInSubsubline = getFirstWord(spans[0], false);
					firstWordInSubsublineWidth = getStringWidth(firstWordInSubsubline, false);
                }
                if (width > MAX_LINE_LENGTH) {
                    /*try {
                        System.in.read();
                    } catch (IOException e) {}*/
                    if (lineId.endsWith("E")) {
                        System.out.println("\u001B[31mLIGNE TROP LONGUE À " + lineId + " (taille : " + width + ") : " + subsubline);
                    }
                }
                
                if (checkTooShortMode && doCheckTooShort
				&& previousSubsublineWidth > 0
				&& previousSubsublineWidth > TOO_SHORT_MODE_MIN_LINE_LENGTH
				&& firstWordInSubsublineWidth > 0
				&& previousSubsublineWidth + firstWordInSubsublineWidth < MAX_LINE_LENGTH
				&& lineId.endsWith("E")) {
                    System.out.println("\u001B[35mLIGNE TROP COURTE À " + lineId + " (taille : " + previousSubsublineWidth + " : " + subsublines[i - 1] + " // " + firstWordInSubsubline + "(" + firstWordInSubsublineWidth + ")");
                }
                previousSubsublineWidth = width;
            }

        }
        		
		if ("".equals(line)) {
			return 0;
		}
		if (lineId.endsWith("E")) {
			return 1;
		} else {
			return 0;
		}
	}
	
	private static String getFormattedString(String string) {
		for (Map.Entry<String, String> entry : charTags.entrySet()) {
			if (!"".equals(entry.getValue())) {
				string = string.replace(entry.getValue(), "…");
			}
		}

		for (Map.Entry<String, String> entry : charTags.entrySet()) {
			string = string.replace(entry.getKey(), entry.getValue());
		}
		return string;
	}
    
    private static String getFirstWord(String string, boolean alternate) {
        String[] words = string.split(" ");
        if (words.length > 0) {
            String firstWord = words[0];
			if (firstWord.startsWith("¬")) {
				firstWord = firstWord.substring(1);
			} else if (firstWord.startsWith("@") || (firstWord.startsWith("µ"))) { // paragraph or menu
				return "";
			}
			if (words.length > 1 && 
				(firstWord.matches("M\\.|Maître|\\d+|PK|\\.\\.\\.") || words[1].matches("♥"))) {
				firstWord += " " + words[1];
			}
            return firstWord + " ";
        }
        return "";
    }
	
	private static int getStringWidth(String string, boolean alternate) {
		int length = 0;
		Map<String, Integer> widths = (alternate ? saturnWidths : normalWidths);
		for (int i = 0; i < string.length(); i++) {
			String character = String.valueOf(string.charAt(i));
			if (widths.containsKey(character)) {
				length += widths.get(character);
			} else {
				length += DEFAULT_CHAR_LENGTH;
			}
		}
		return length;
	}
	
	private static void fillCharTags() {
		charTags = new HashMap<String, String>();
		
		charTags.put("[SMALLBULLET]", "∙");
		charTags.put("[ALPHA]", "α");
		charTags.put("[BETA]", "β");
		charTags.put("[GAMMA]", "γ");
		charTags.put("[OMEGA]", "Ω");
		charTags.put("[LARGECIRCLE]", "Ο");
		charTags.put("[NOTE]", "♪");
		charTags.put("[RIGHT]", "→");
		charTags.put("[LEFT]", "←");
		charTags.put("[UP]", "↑");
		charTags.put("[DOWN]", "↓");
		charTags.put("[MENUSPACEYES]", "	");
		charTags.put("[MENUSPACENO]", "	");
		charTags.put("[PK ]", "¶");
		charTags.put("[HEART]", "♥");
		charTags.put("[NOWIDTH]", "");
	}
	
	private static void fillWidths() {
		normalWidths = new HashMap<String, Integer>();
		saturnWidths = new HashMap<String, Integer>();
				
		normalWidths.put("Ø", 10);
		normalWidths.put("!", 2);
		normalWidths.put("\"", 4);
		normalWidths.put("∙", 3);
		normalWidths.put("$", 6);
		normalWidths.put("%", 8);
		normalWidths.put("&", 8);
		normalWidths.put("'", 3);
		normalWidths.put("(", 4);
		normalWidths.put(")", 4);
		normalWidths.put("*", 4);
		normalWidths.put("+", 6);
		normalWidths.put(",", 3);
		normalWidths.put("-", 4);
		normalWidths.put(".", 3);
		normalWidths.put("/", 5);
		normalWidths.put("0", 6);
		normalWidths.put("1", 6);
		normalWidths.put("2", 6);
		normalWidths.put("3", 6);
		normalWidths.put("4", 6);
		normalWidths.put("5", 6);
		normalWidths.put("6", 6);
		normalWidths.put("7", 6);
		normalWidths.put("8", 6);
		normalWidths.put("9", 6);
		normalWidths.put(":", 3);
		normalWidths.put(";", 4);
		normalWidths.put("<", 7);
		normalWidths.put("=", 6);
		normalWidths.put(">", 7);
		normalWidths.put("?", 5);
		normalWidths.put("@", 5);
		normalWidths.put("A", 7);
		normalWidths.put("B", 6);
		normalWidths.put("C", 6);
		normalWidths.put("D", 6);
		normalWidths.put("E", 5);
		normalWidths.put("F", 5);
		normalWidths.put("G", 6);
		normalWidths.put("H", 6);
		normalWidths.put("I", 2);
		normalWidths.put("J", 5);
		normalWidths.put("K", 6);
		normalWidths.put("L", 5);
		normalWidths.put("M", 8);
		normalWidths.put("N", 6);
		normalWidths.put("O", 6);
		normalWidths.put("P", 6);
		normalWidths.put("Q", 6);
		normalWidths.put("R", 6);
		normalWidths.put("S", 6);
		normalWidths.put("T", 6);
		normalWidths.put("U", 6);
		normalWidths.put("V", 7);
		normalWidths.put("W", 8);
		normalWidths.put("X", 6);
		normalWidths.put("Y", 6);
		normalWidths.put("Z", 5);
		normalWidths.put("α", 6);
		normalWidths.put("β", 5);
		normalWidths.put("γ", 7);
		normalWidths.put("Ω", 6);
		normalWidths.put("Ο", 8);
		normalWidths.put(" ", 3);
		normalWidths.put("a", 5);
		normalWidths.put("b", 5);
		normalWidths.put("c", 5);
		normalWidths.put("d", 5);
		normalWidths.put("e", 5);
		normalWidths.put("f", 4);
		normalWidths.put("g", 5);
		normalWidths.put("h", 5);
		normalWidths.put("i", 2);
		normalWidths.put("j", 3);
		normalWidths.put("k", 5);
		normalWidths.put("l", 2);
		normalWidths.put("m", 8);
		normalWidths.put("n", 5);
		normalWidths.put("o", 5);
		normalWidths.put("p", 5);
		normalWidths.put("q", 5);
		normalWidths.put("r", 4);
		normalWidths.put("s", 5);
		normalWidths.put("t", 4);
		normalWidths.put("u", 5);
		normalWidths.put("v", 6);
		normalWidths.put("w", 8);
		normalWidths.put("x", 5);
		normalWidths.put("y", 5);
		normalWidths.put("z", 5);
		normalWidths.put("{", 3);
		normalWidths.put("♪", 6);
		normalWidths.put("}", 3);
		normalWidths.put("~", 7);
		normalWidths.put("¬", 5);
		normalWidths.put("¹", 0);
		normalWidths.put("Ó", 0);
		normalWidths.put("À", 7);
		normalWidths.put("Ú", 9);
		normalWidths.put("Â", 7);
		normalWidths.put("—", 7);
		normalWidths.put("Þ", 8);
		normalWidths.put("£", 0);
		normalWidths.put("†", 9);
		normalWidths.put("Ç", 6);
		normalWidths.put("È", 5);
		normalWidths.put("É", 5);
		normalWidths.put("Ê", 5);
		normalWidths.put("Ë", 5);
		normalWidths.put("¡", 6);
		normalWidths.put("ƒ", 7);
		normalWidths.put("Î", 4);
		normalWidths.put("Ï", 3);
		normalWidths.put("Ñ", 6);
		normalWidths.put("Œ", 9);
		normalWidths.put("Á", 9);
		normalWidths.put("Æ", 6);
		normalWidths.put("Ô", 6);
		normalWidths.put("·", 2);
		normalWidths.put("þ", 7);
		normalWidths.put("•", 3);
		normalWidths.put("Ù", 6);
		normalWidths.put("³", 0);
		normalWidths.put("Û", 6);
		normalWidths.put("Š", 8);
		normalWidths.put("Ü", 8);
		normalWidths.put("Ÿ", 8);
		normalWidths.put("à", 5);
		normalWidths.put("á", 0);
		normalWidths.put("â", 5);
		normalWidths.put("©", 5);
		normalWidths.put("ä", 5);
		normalWidths.put("°", 5);
		normalWidths.put("æ", 8);
		normalWidths.put("ç", 5);
		normalWidths.put("è", 5);
		normalWidths.put("é", 5);
		normalWidths.put("ê", 5);
		normalWidths.put("ë", 5);
		normalWidths.put("ì", 0);
		normalWidths.put("í", 0);
		normalWidths.put("î", 4);
		normalWidths.put("ï", 4);
		normalWidths.put("ñ", 5);
		normalWidths.put("œ", 8);
		normalWidths.put("ò", 0);
		normalWidths.put("Í", 7);
		normalWidths.put("ô", 5);
		normalWidths.put("õ", 3);
		normalWidths.put("ö", 5);
		normalWidths.put("ø", 0);
		normalWidths.put("ù", 5);
		normalWidths.put("ú", 0);
		normalWidths.put("û", 5);
		normalWidths.put("ý", 5);
		normalWidths.put("ü", 8);
		normalWidths.put("ÿ", 8);
		normalWidths.put("→", 11);
		normalWidths.put("←", 11);
		normalWidths.put("↑", 6);
		normalWidths.put("↓", 6);
		normalWidths.put("	", 30);
		normalWidths.put("	", 34);
		normalWidths.put("¶", 15);
		normalWidths.put("", 0);
		normalWidths.put("♥", 8);
		normalWidths.put("µ", 0); // menu mark

		saturnWidths.put(" ", 5);
		saturnWidths.put("@", 10);
		saturnWidths.put("!", 7);
		saturnWidths.put("\"", 9);
		saturnWidths.put("∙", 7);
		saturnWidths.put("Ù", 10);
		saturnWidths.put("À", 9);
		saturnWidths.put("Ê", 12);
		saturnWidths.put("'", 5);
		saturnWidths.put("(", 6);
		saturnWidths.put(")", 6);
		saturnWidths.put("*", 7);
		saturnWidths.put("+", 9);
		saturnWidths.put(",", 7);
		saturnWidths.put("-", 8);
		saturnWidths.put(".", 7);
		saturnWidths.put("/", 9);
		saturnWidths.put("?", 9);
		saturnWidths.put("0", 12);
		saturnWidths.put("1", 11);
		saturnWidths.put("2", 12);
		saturnWidths.put("3", 12);
		saturnWidths.put("4", 12);
		saturnWidths.put("5", 12);
		saturnWidths.put("6", 12);
		saturnWidths.put("7", 12);
		saturnWidths.put("8", 12);
		saturnWidths.put("9", 12);
		saturnWidths.put(":", 7);
		saturnWidths.put(";", 7);
		saturnWidths.put("<", 9);
		saturnWidths.put("=", 8);
		saturnWidths.put(">", 9);
		saturnWidths.put("Î", 8);
		saturnWidths.put("Û", 10);
		saturnWidths.put("A", 9);
		saturnWidths.put("B", 9);
		saturnWidths.put("C", 12);
		saturnWidths.put("D", 11);
		saturnWidths.put("E", 12);
		saturnWidths.put("F", 10);
		saturnWidths.put("G", 11);
		saturnWidths.put("H", 15);
		saturnWidths.put("I", 8);
		saturnWidths.put("J", 15);
		saturnWidths.put("K", 11);
		saturnWidths.put("L", 11);
		saturnWidths.put("M", 15);
		saturnWidths.put("N", 10);
		saturnWidths.put("O", 12);
		saturnWidths.put("P", 10);
		saturnWidths.put("Q", 12);
		saturnWidths.put("R", 10);
		saturnWidths.put("S", 10);
		saturnWidths.put("T", 9);
		saturnWidths.put("U", 10);
		saturnWidths.put("V", 11);
		saturnWidths.put("W", 14);
		saturnWidths.put("X", 11);
		saturnWidths.put("Y", 9);
		saturnWidths.put("Z", 11);
		saturnWidths.put("α", 14);
		saturnWidths.put("β", 14);
		saturnWidths.put("γ", 12);
		saturnWidths.put("Ω", 14);
		saturnWidths.put("ß", 10);
		saturnWidths.put("Ç", 12);
		saturnWidths.put("É", 12);
		saturnWidths.put("Œ", 15);
		saturnWidths.put("È", 12);
		saturnWidths.put("µ", 0); // menu mark
	}
	
	private static void fillItems() {
		items = new HashMap<String, String>();

		items.put("01", "Poutre de Briquet");
		items.put("02", "Poutre de bois frais");
		items.put("03", "Bâton");
		items.put("04", "Bâton amélioré");
		items.put("05", "Bâton·simple·d'emploi");
		items.put("06", "Bâton efficace");
		items.put("07", "Bâton très efficace");
		items.put("08", "Bâton rigolo");
		items.put("09", "Bâton tonifiant");
		items.put("0A", "Bâton ingénieux");
		items.put("0B", "Bâton de maître");
		items.put("0C", "Bâton mystique");
		items.put("0D", "Gants");
		items.put("0E", "Gants robustes");
		items.put("0F", "Gants élégants");
		items.put("10", "Gants puissants");
		items.put("11", "Gants magiques");
		items.put("12", "Stnag");
		items.put("13", "Gants onéreux");
		items.put("14", "Gants d'ange");
		items.put("15", "Gants mystiques");
		items.put("16", "Chaussures simples");
		items.put("17", "Chaussures robustes");
		items.put("18", "Chaussures pointues");
		items.put("19", "Chaussures ultimes");
		items.put("1A", "Souliers mystiques");
		items.put("1B", "Bonnes Chaussures");
		items.put("1C", "Bottes·en·caoutchouc");
		items.put("1D", "Chaussures nu-pieds");
		items.put("1E", "Bottes anti-glisse");
		items.put("1F", "Yo-yo·simple·d'emploi");
		items.put("20", "Arme canine");
		items.put("21", "Brochure DCMC");
		items.put("22", "Fausse Batte");
		items.put("23", "Fausse Poêle");
		items.put("24", "Batte véritable");
		items.put("25", "Charme mini-mini");
		items.put("26", "Charme puce");
		items.put("27", "Charme moustique");
		items.put("28", "Charme mouche");
		items.put("29", "Charme coq");
		items.put("2A", "Charme plume");
		items.put("2B", "Charme pierre");
		items.put("2C", "Charme lourd");
		items.put("2D", "Charme grondant");
		items.put("2E", "Collier blanc");
		items.put("2F", "Collier marron");
		items.put("30", "Collier noir");
		items.put("31", "Collier rayé");
		items.put("32", "Collier rouge");
		items.put("33", "T-shirt DCMC");
		items.put("34", "Cape en caoutchouc");
		items.put("35", "Manteau aloha");
		items.put("36", "Chandail chaud");
		items.put("37", "Corsage de déesse");
		items.put("38", "Cape incroyable");
		items.put("39", "Bandana viril");
		items.put("3A", "Bandana poussin");
		items.put("3B", "Bandana moineau");
		items.put("3C", "Bandana marouette");
		items.put("3D", "Bandana hirondelle");
		items.put("3E", "Bandana corneille");
		items.put("3F", "Bandana milan");
		items.put("40", "Bandana Horus");
		items.put("41", "Chapeau simple");
		items.put("42", "Chapeau vache");
		items.put("43", "Chapeau alligator");
		items.put("44", "Chapeau ours");
		items.put("45", "Chapeau loutre");
		items.put("46", "Ruban écarlate");
		items.put("47", "Ruban azur");
		items.put("48", "Ruban boing");
		items.put("49", "Ruban de fée");
		items.put("4A", "Ruban d'ange");
		items.put("4B", "Ruban de déesse");
		items.put("4C", "Casquette DCMC");
		items.put("4D", "Couronne incroyable");
		items.put("4E", "Bracelet Bélier");
		items.put("4F", "Bracelet Verseau");
		items.put("50", "Bracelet Poisson");
		items.put("51", "Bracelet Capricorne");
		items.put("52", "Bracelet Taureau");
		items.put("53", "Bracelet Gémeaux");
		items.put("54", "Bracelet Cancer");
		items.put("55", "Bracelet Lion");
		items.put("56", "Bracelet Vierge");
		items.put("57", "Bracelet Balance");
		items.put("58", "Bracelet Scorpion");
		items.put("59", "Bracelet Sagittaire");
		items.put("5A", "Bague DCMC");
		items.put("5B", "Bague incroyable");
		items.put("5C", "Nourriture préférée");
		items.put("5D", "Noix");
		items.put("5E", "Pain aux noix");
		items.put("5F", "Cookie aux noix");
		items.put("60", "Lait frais");
		items.put("61", "Lait avarié");
		items.put("62", "Yaourt");
		items.put("63", "Champi comestible");
		items.put("64", "Thé nespa");
		items.put("65", "Viande séchée");
		items.put("66", "Œuf frais");
		items.put("67", "Éclair avarié");
		items.put("68", "Bavarois avarié");
		items.put("69", "Fromage étrange");
		items.put("6A", "Chips de porc");
		items.put("6B", "Banane luxueuse");
		items.put("6C", "Brioche");
		items.put("6D", "Soda frais");
		items.put("6E", "Soda citadin");
		items.put("6F", "Cola citadin");
		items.put("70", "Burger citadin");
		items.put("71", "Frites citadines");
		items.put("72", "Biscuit pour chien");
		items.put("73", "Bâtonnet·pour·chien");
		items.put("74", "Croissant");
		items.put("75", "Nouilles au porc");
		items.put("76", "Ragoût de porc");
		items.put("77", "Boulettes·dþsincérité");
		items.put("78", "Tofu à la fraise");
		items.put("79", "Riz de la chance");
		items.put("7A", "Repas de la loterie");
		items.put("7B", "Soupe de homard");
		items.put("7C", "Steak·d'ormeau·géant");
		items.put("7D", "Double·Viande·séchée");
		items.put("7E", "Poulet grillé");
		items.put("7F", "Poisson grillé");
		items.put("80", "Pâtes·au lourd passé");
		items.put("81", "Pizza préférée");
		items.put("82", "King Burger");
		items.put("83", "Bifteck");
		items.put("84", "Ragoût de p'tit riche");
		items.put("85", "Parfait bien gras");
		items.put("86", "Sushi au hot-dog");
		items.put("87", "Gélatine magique");
		items.put("88", "Tarte magique");
		items.put("89", "Pudding magique");
		items.put("8A", "Gâteau magique");
		items.put("8B", "Antidote");
		items.put("8C", "Anti-paralysie");
		items.put("8D", "Cigale d'éveil");
		items.put("8E", "Menthe fraîche");
		items.put("8F", "Éventail en papier");
		items.put("90", "Cloche du souvenir");
		items.put("91", "Gouttes·pour·les·yeux");
		items.put("92", "Poudre anti-puces");
		items.put("93", "Herbe secrète");
		items.put("94", "Nouilles de vie");
		items.put("95", "Œuf mollet");
		items.put("96", "Bombe de foudre");
		items.put("97", "Bombe à pattes");
		items.put("98", "Bombe athlétique");
		items.put("99", "Crayon-missile");
		items.put("9A", "Bombe classique");
		items.put("9B", "Super Bombe");
		items.put("9C", "Bombe du Nouvel An");
		items.put("9D", "Pluie de miel");
		items.put("9E", "Banane ancestrale");
		items.put("9F", "Poignée de porte");
		items.put("A0", "Insecticide");
		items.put("A1", "Pistolet à eau salée");
		items.put("A2", "Spray offensif");
		items.put("A3", "Spray défensif");
		items.put("A4", "Renforce-ennemi");
		items.put("A5", "Fragilise-ennemi");
		items.put("A6", "Attire-attaque");
		items.put("A7", "Tumacru");
		items.put("A8", "Libellule");
		items.put("A9", "Libellule argentée");
		items.put("AA", "Poussin");
		items.put("AB", "Poulet");
		items.put("AC", "Légumes marinés");
		items.put("AD", "Météotite");
		items.put("AE", "Yo-yo du copain");
		items.put("AF", "Croc de Drago");
		items.put("B0", "Bouse");
		items.put("B1", "Osselet de dauphin");
		items.put("B2", "Arme à pointes");
		items.put("B3", "Pull en laine");
		items.put("B4", "Caleçon DCMC");
		items.put("B5", "Pendentif magique");
		items.put("B6", "Mots réconfortants");
		items.put("B7", "Patate douce");
		items.put("B8", "P'tit Taupe-grillon");
		items.put("B9", "Coquille d'ermite");
		items.put("BA", "Thé suspect");
		items.put("BB", "Pendentif de feu");
		items.put("BC", "Pendentif de glace");
		items.put("BD", "Pendentif de foudre");
		items.put("BE", "Badge du courage");
		items.put("BF", "Robe souvenir");
		items.put("C0", "Efface-crayon");
		items.put("C1", "Badge Franklin");
		items.put("C2", "T-shirt garçon");
		items.put("C3", "T-shirt grand garçon");
		items.put("C4", "Bonnet enfant");
		items.put("C5", "Cornichons suprêmes");
		items.put("C6", "Casquette rouge");
		items.put("C7", "Ruban blanc");
		items.put("C8", "Oiseau");
		items.put("C9", "Serpent du souvenir");
		items.put("CA", "Eau du temps");
		items.put("CB", "Souvenir de punaise");
		items.put("CC", "Carnet de Phrygie");
		items.put("CD", "Tire-bouclier");
		items.put("CE", "Test");
		items.put("CF", "VIDE");
		items.put("D0", "VIDE");
		items.put("D1", "VIDE");
		items.put("D2", "Carte quiz 1");
		items.put("D3", "Carte quiz 2");
		items.put("D4", "Carte quiz 3");
		items.put("D5", "Carte quiz 4");
		items.put("D6", "Agrafes murales");
		items.put("D7", "Scarabée hurleur");
		items.put("D8", "Bombe fumigène");
		items.put("D9", "Hypno-Pendule");
		items.put("DA", "Masque effrayant");
		items.put("DB", "Plumeau à chatou¡es");
		items.put("DC", "Clé du pont-levis");
		items.put("DD", "Noble Crachoir");
		items.put("DE", "Pendentif");
		items.put("DF", "Œuf de lumière");
		items.put("E0", "Bout de tissu");
		items.put("E1", "Carnet de cochon");
		items.put("E2", "Chaussure d'enfant");
		items.put("E3", "Lime");
		items.put("E4", "Serpent-corde");
		items.put("E5", "Serpent-corde de luxe");
		items.put("E6", "Talkie-walkie");
		items.put("E7", "Ticket");
		items.put("E8", "Bestiaire");
		items.put("E9", "Pigeon voyageur");
		items.put("EA", "RasoirÆ·rougeÁlèvres");
		items.put("EB", "Souvenir de Ionie");
		items.put("EC", "Souvenir de Dorie");
		items.put("ED", "Souvenir de Phrygie");
		items.put("EE", "Souvenir de Lydie");
		items.put("EF", "Souvenir de Mixie");
	}
}