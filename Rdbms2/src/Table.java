import java.util.List;

public class Table {
	List<String> header;
	List<List<String>> content;

	public Table(List<List<String>> content, List<String> header) {
		super();
		this.header = header;
		this.content = content;
	}

	@Override
	public String toString() {
		return "Table header=" + header.toString() + ", content=" + content.toString() + "]";
	}
}
