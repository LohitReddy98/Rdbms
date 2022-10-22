import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.Arrays.asList;

public class TablesManager {

	HashMap<String, Table> tableMapping = new HashMap<String, Table>();

	public void addNewTable(String fileData, String name) {
		
		List<String> header = new ArrayList<String>();
		List<List<String>> data = new ArrayList<List<String>>();
		String lines[] = fileData.split("\\r?\\n");
		for (int i = 0; i < lines.length; i++) {
			if (i == 0) {
				header.addAll(Arrays.asList(lines[i].split("\\s*,\\s*")));
			} else
				data.add(Arrays.asList(lines[i].split("\\s*,\\s*")));
		}
		tableMapping.put(name, new Table(data, header));
	}

	public Table product(Table t1, Table t2) {
		List<String> header = new ArrayList<String>();
		List<List<String>> data = new ArrayList<List<String>>();

		header.addAll(t1.header);
		header.addAll(t2.header);
		for (int i = 0; i < t1.content.size(); i++) {
			for (int j = 0; j < t2.content.size(); j++) {
				List<String> newRow = new ArrayList<String>();
				newRow.addAll(t1.content.get(i));
				newRow.addAll(t2.content.get(j));
				data.add(newRow);
			}

		}
		return new Table(data, header);
	}

	public Table getSelection(String condition, Table table) {
		String col = "";
		String comp = "";
		String op = "";
		if (condition.contains(">")) {
			String[] s = condition.trim().split("\\s*>\\s*");
			col = s[0];
			comp = s[1];
			op = ">";
		}
		if (condition.contains("<")) {
			String[] s = condition.trim().split("\\s*<\\s*");
			col = s[0];
			comp = s[1];
			op = "<";
		}
		if (condition.contains("=")) {
			String[] s = condition.trim().split("\\s*=\\s*");
			col = s[0];
			comp = s[1];
			op = "=";
		}
		final String ops = op;
		final String comp1 = comp;

		int index = table.header.indexOf(col);
		List<List<String>> result = table.content.stream().filter(row -> checkOps(ops, row.get(index), comp1))
				.collect(Collectors.toList());
		return new Table(result, table.header);
	}

	public Table getProjection(String cols, Table table) {
		String[] columns = cols.split("\\s*,\\s*");
		List<List<String>> result = new ArrayList<List<String>>();
		List<String> resulth = Arrays.asList(cols);
		for (List<String> r : table.content) {
			List<String> newEow = new ArrayList<String>();
			for (String col : columns) {
				newEow.add(r.get(table.header.indexOf(col)));
			}
			result.add(newEow);
		}

		return new Table(result, resulth);

	}

	public Table getUnion(Table tab1, Table tab2) {
		List<List<String>> result = new ArrayList<List<String>>();
		result.addAll(tab1.content);
		result.addAll(tab2.content);
		return new Table(result, tab1.header);
	}

	public Table getJoin(Table tab1, Table tab2) {
		List<String> common = tab1.header.stream().filter(tab2.header::contains).collect(Collectors.toList());
		List<String> head = new ArrayList<String>();
		head.addAll(tab1.header);
		head.addAll(tab2.header);
		String header = common.get(0);
		head.remove(header);

		List<List<String>> result = new ArrayList<List<String>>();
		for (List<String> row : tab1.content) {
			
			for (List<String> row1 : tab2.content) {
				if (row.get(tab1.header.indexOf(header)).equals(row1.get(tab2.header.indexOf(header)))) {
					ArrayList<String> rowNew = new ArrayList<String>();
					rowNew.addAll(row);
					rowNew.remove(tab1.header.indexOf(header));
					rowNew.addAll(row1);
					result.add(rowNew);
				}
			}
		}
		return new Table(result, head);
	}

	public Table getMinus(Table tab2, Table tab1) {
		List<List<String>> result = new ArrayList<List<String>>();
		for (List<String> r : tab1.content) {
			boolean isUnique = true;
			for (List<String> r1 : tab2.content) {
				if (r.toString().equals(r1.toString())) {
					isUnique = false;
					break;
				}
			}
			if (isUnique)
				result.add(r);
		}
		return new Table(result, tab1.header);

	}

	public boolean checkOps(String op, String val1, String val2) {
		try {
			if (op.equals(">")) {
				return Long.parseLong(val1) > Long.parseLong(val2);
			}
			if (op.equals("<")) {
				return Long.parseLong(val1) < Long.parseLong(val2);
			}
			if (op.equals("=")) {
				return Long.parseLong(val1) == Long.parseLong(val2);
			}
		} catch (Exception e) {
			return false;
		}
		return false;

	}

}
