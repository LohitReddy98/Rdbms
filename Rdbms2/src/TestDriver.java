import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class TestDriver {
	public static void main(String args[]) {
		String filePath = new File("").getAbsolutePath();
		Path file = Paths.get("RAoutput.csv");

		ArrayList<String> csvRes = new ArrayList<String>();
		TablesManager m = new TablesManager();
		m.addNewTable(getFileText(filePath + "\\datasets\\Play.txt"), "Play");
		m.addNewTable(getFileText(filePath + "\\datasets\\ACTORS.txt"), "ACTORS");
		m.addNewTable(getFileText(filePath + "\\datasets\\MOVIES.txt"), "MOVIES");
		String[] queries = getFileText(filePath + "\\datasets\\RAqueries.txt").split("\n");
		for (String query : queries) {
			if (query.equals(""))
				continue;
			Stack<String> stack = new Stack<String>();
			Stack<Table> stackf = new Stack<Table>();

			query = query.replaceAll("\\s+", "");
			String op = "";
			String[] s = { "*", "-", "U" };
			List<String> stop = Arrays.asList(s);
			for (int i = 0; i < query.length(); i++) {
				if (query.charAt(i) == '(')
					continue;
				if (query.charAt(i) == ')') {
					if (!op.equals("")) {
						stack.push(op);
					}
					op = "";
					popAll(stack, stackf, m);
					continue;
				}
				if (query.charAt(i) == '}') {
					op += query.charAt(i);
					stack.push(op);
					op = "";
					continue;
				}
				if (stop.contains(query.charAt(i) + "")) {
					if (!op.equals("")) {
						stack.push(op);
					}
					stack.push(query.charAt(i) + "");
					op = "";
					continue;
				}
				op += query.charAt(i);
			}
			if (!op.equals("")) {
				stack.push(op);
			}
			popAll(stack, stackf, m);
			csvRes.add(getFormatted(stackf.pop()) + "\n\n");
		}
		try {
			Files.write(file, csvRes, StandardCharsets.UTF_8);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private static String getFileText(String fileName) {
		String text = "";
		try {
			File myObj = new File(fileName);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				text += data + "\n";
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return text;
	}

	private static String getFormatted(Table tab) {
		String table = "";
		for (String a : tab.header) {
			table += a + ",";
		}
		table += "\n\n";

		for (List<String> s : tab.content) {
			for (String a : s) {
				table += a + ",";
			}
			table += "\n";
		}

		return table;
	}

	public static void popAll(Stack<String> s, Stack<Table> f, TablesManager m) {

		while (!s.isEmpty()) {
			String k = s.pop();
			if (k.contains("PROJ_")) {
				String result1 = getSubString(k);
				f.push(m.getProjection(result1, f.pop()));
			} else if (k.contains("SELE_")) {
				String result1 = getSubString(k);
				f.push(m.getSelection(result1, f.pop()));
			} else if (k.contains("U")) {
				f.push(m.getUnion(f.pop(), f.pop()));
			} else if (k.contains("-")) {
				f.push(m.getMinus(f.pop(), f.pop()));
			} else if (k.contains("*")) {
				f.push(m.getJoin(m.tableMapping.get(s.pop()), f.pop()));
			} else {
				f.push(m.tableMapping.get(k));
			}

		}
	}

	public static String getSubString(String k) {
		int start = k.indexOf("{");
		int close = k.indexOf("}");
		String result1 = k.substring(start + 1, close);
		return result1;
	}
}
