import java.util.List;

/**
 * Prints data as a formatted text table in the console.
 * Concepts: String formatting, StringBuilder, arrays.
 */
public class TablePrinter {

    public static void print(String[] headers, List<String[]> rows) {
        int cols = headers.length;
        int[] w = new int[cols];
        for (int i = 0; i < cols; i++) w[i] = headers[i].length();
        for (String[] row : rows)
            for (int i = 0; i < cols && i < row.length; i++) {
                String v = row[i] == null ? "-" : row[i];
                if (v.length() > 40) v = v.substring(0, 37) + "...";
                row[i] = v;
                w[i] = Math.max(w[i], v.length());
            }

        StringBuilder sep = new StringBuilder("+");
        for (int x : w) sep.append("-".repeat(x + 2)).append("+");

        System.out.println(sep);
        System.out.println(fmtRow(headers, w));
        System.out.println(sep);
        for (String[] row : rows) System.out.println(fmtRow(row, w));
        System.out.println(sep);
        System.out.println("(" + rows.size() + " row" + (rows.size() == 1 ? "" : "s") + ")");
    }

    private static String fmtRow(String[] cells, int[] w) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < w.length; i++) {
            String v = (i < cells.length && cells[i] != null) ? cells[i] : "-";
            sb.append(" ").append(String.format("%-" + w[i] + "s", v)).append(" |");
        }
        return sb.toString();
    }
}
